package es.jcyl.ita.formic.jayjobs.task.processor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

/**
 * Processor to add compressing tasks in jobs
 *
 * @author gustavo.rio@itacyl.es
 *
 */

public class ZipProcessor extends AbstractProcessor implements NonIterProcessor {

	protected static final Log LOGGER = LogFactory.getLog(ZipProcessor.class);
	/** Input file list */
	private List<String> inputFiles;

	private String outputFile;
	private String outputFileExtension = "zip";
	private boolean appendPrevOutput = false;

	private List<File> inputFileObjects;
	private String outputContext;


	@Override
	public void process() throws TaskException {
		init();
		// comprimir ficheros
		File fout = new File(outputFile);
		String currentEntry = null;
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fout))) {
			for (String file : inputFiles) {
				currentEntry = file;
				File fEntry = new File(file);
				ZipEntry e = new ZipEntry(fEntry.getName());
				out.putNextEntry(e);
				out.write(FileUtils.readFileToByteArray(fEntry));
				out.closeEntry();
			}
		} catch (IOException e) {
			throw new TaskException(
					"An error occurred while processing input file " + currentEntry, e);
		}
	}

	private void init() throws TaskException {
		outputFile = TaskResourceAccessor.locateOutputFile(getGlobalContext(), outputFile, outputFileExtension);
		LOGGER.info("outputFile:" + outputFile);
		inputFileObjects = TaskResourceAccessor.locateInputFiles(getGlobalContext(), inputFiles);
		LOGGER.info("inputFiles:" + inputFiles);
	}
	public List<String> getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}

	@Override
	public String getOutputFile() {
		return outputFile;
	}

	@Override
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public String getOutputFileExtension() {
		return outputFileExtension;
	}

	@Override
	public void setOutputFileExtension(String outputFileExtension) {
		this.outputFileExtension = outputFileExtension;
	}

	public boolean isAppendPrevOutput() {
		return appendPrevOutput;
	}

	public void setAppendPrevOutput(boolean appendPrevOutput) {
		this.appendPrevOutput = appendPrevOutput;
	}

	public String getOutputContext() {
		return outputContext;
	}

	public void setOutputContext(String outputContext) {
		this.outputContext = outputContext;
	}
}
