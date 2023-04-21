package es.jcyl.ita.formic.jayjobs.task.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;


public class TaskResourceAccessor {

    public static String getResource(CompositeContext ctx, String filePath) {
        // si la ruta es absoluta, se devuelve directamente el fichero
        File file = new File(filePath);
        if (file.isAbsolute()) {
            return filePath;
        } else {
            // devolver el fichero relativo al directorio de recursos de la
            // aplicación
            String dirRecursos = getWorkingFile(ctx, filePath);
            return dirRecursos;
        }
    }

    public static List<String> getWorkingFile(CompositeContext ctx, List<String> paths) {
        List<String> files = new ArrayList<>();
        for(String path: paths){
            files.add(getWorkingFile(ctx, path));
        }
        return files;
    }

    public static String getWorkingTempFile(CompositeContext ctx, String infix, String extension) {
        // si no se proporciona fichero de entrada, generar un nombre aleatorio
        if (StringUtils.isNoneBlank(extension)) {
            extension = "." + extension;
        }
        if (StringUtils.isNoneBlank(infix)) {
            infix = infix + "_";
        }
        String outputFile = String.format("%s_%s_%s%s%s", ctx.get("job.jobExecId"),
                RandomStringUtils.randomAlphanumeric(10), infix, System.currentTimeMillis(),
                extension);
        return getWorkingFile(ctx, outputFile);
    }


    /**
     * Converts relative file references to absolute file paths using project base folder.
     *
     * @param filePath
     * @return
     */
    public static String getWorkingFile(CompositeContext ctx, String filePath) {
        // if absolute path, return without changes
        File file = new File(filePath);
        if (file.isAbsolute()) {
            return filePath;
        } else {
            String appWorkingFolder = ContextAccessor.workingFolder(ctx);
            // create absolute path to working folder
            File f = new File(appWorkingFolder, filePath);
            return f.getAbsolutePath().replace("\\","/");
        }
    }

    public static String getProjectFile(CompositeContext ctx, String filePath){
        // if absolute path, return without changes
        File file = new File(filePath);
        if (file.isAbsolute()) {
            return filePath;
        } else {
            String projectBaseFolder = ContextAccessor.projectFolder(ctx);
            // create absolute path to project base folder
            File f = new File(projectBaseFolder, filePath);
            return f.getAbsolutePath().replace("\\","/");
        }
    }


    public static List<File> locateInputFiles(CompositeContext ctx, List<String> paths) throws TaskException {
        List<File> files = new ArrayList<>();
        for(String path: paths){
            files.add(locateInputFile(ctx, path));
        }
        return files;
    }


    /**
     * Calculates final output file name
     */
    public static String locateOutputFile(CompositeContext context, String outputFile) {
        return locateOutputFile(context, outputFile, "", ".out");
    }
    public static String locateOutputFile(CompositeContext context, String outputFile, String extension) {
        return locateOutputFile(context, outputFile, "", extension);
    }

    public static String locateOutputFile(CompositeContext context, String outputFile, String infix, String extension) {
        outputFile = StringUtils.isBlank(outputFile)
                ? TaskResourceAccessor.getWorkingTempFile(context, infix, extension)
                : TaskResourceAccessor.getWorkingFile(context, outputFile);
        return outputFile.replace("\\","/");
    }

    /**
     * Método de utilidad para processors, readers y writers que busca el inputFile
     * de forma consecutiva en diferenes contextos
     *
     * @throws TaskException
     */
    public static File locateInputFile(CompositeContext gContxt, String inputFile)
            throws TaskException {
        File file = null;
        if (StringUtils.isBlank(inputFile)) {
            throw new TaskException(
                    "The 'inputFile' parameter is null or empty, check the job's json configuration.");
        }
        String fName = inputFile;
        // buscar fichero en el directorio de trabajo
        file = new File(TaskResourceAccessor.getWorkingFile(gContxt, fName));

        // buscar fichero en el directorio de recursos
        if (file == null || !file.exists()) {
            file = new File(TaskResourceAccessor.getProjectFile(gContxt, fName));
        }
        // si no se encunt
        if (!file.exists()) {
            throw new TaskException(String.format(
                    "The file [%s] is not found no se ha encontrado ni en el directorio de trabajo [%s] "
                            + "ni en la carpeta de recursos de la Aplicación [%s]",
                    fName, TaskResourceAccessor.getWorkingFile(gContxt, fName),
                    TaskResourceAccessor.getWorkingFile(gContxt, fName)));
        }
        return file;
    }

}
