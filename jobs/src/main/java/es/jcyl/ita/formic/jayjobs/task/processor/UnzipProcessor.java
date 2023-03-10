package es.jcyl.ita.formic.jayjobs.task.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;


/**
 * Processor to add uncompressing tasks in jobs
 *
 * @author gustavo.rio@itacyl.es
 */
public class UnzipProcessor extends AbstractProcessor implements NonIterProcessor {

    protected static final Log LOGGER = LogFactory.getLog(UnzipProcessor.class);

    private String inputFile;
    private String outputFolder;
    private boolean mkdir = false;

    private File file;

    @Override
    public void process() throws TaskException {
        init();
        FileInputStream fis;
        // buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder, fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            // close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            throw new TaskException("An error occurred while uncompressing the file " + inputFile,
                    e);
        }
    }

    private void init() throws TaskException {
        checkParams();
        outputFolder = TaskResourceAccessor.getWorkingFile(getGlobalContext(), outputFolder);

        LOGGER.info("outputFolder:" + outputFolder);
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            if (!mkdir) {
                throw new TaskException(
                        String.format("The destination folder %s doesn't exists and the parameter " +
                                        "mkdir is false ", outputFolder));
            } else {
                folder.mkdirs();
            }
        }
        file = TaskResourceAccessor.locateInputFile(getGlobalContext(), inputFile);
        LOGGER.info("inputFile:" + file);
    }

    private void checkParams() throws TaskException{
        if (StringUtils.isBlank(inputFile)) {
            throw new TaskException("The parameter 'inputFile' cannot be null.");
        }
        if (StringUtils.isBlank(outputFolder)) {
            throw new TaskException("The parameter 'outputFolder' cannot be null.");
        }
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public boolean isMkdir() {
        return mkdir;
    }

    public void setMkdir(boolean mkdir) {
        this.mkdir = mkdir;
    }

}
