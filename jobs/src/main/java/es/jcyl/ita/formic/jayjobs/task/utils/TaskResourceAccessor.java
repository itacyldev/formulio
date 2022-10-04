package es.jcyl.ita.formic.jayjobs.task.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

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
            return f.getAbsolutePath();
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
            return f.getAbsolutePath();
        }
    }

    /**
     * Método de utilidad para processors, readers y writers que busca el inputFile
     * de forma consecutiva en diferenes contextos
     *
     * @throws TaskException
     */
    public static File locateInputFile(String inputFile, CompositeContext gContxt)
            throws TaskException {
        File file = null;
        if (StringUtils.isBlank(inputFile)) {
            throw new TaskException(
                    "El parámetro inputFile es nulo o vacío, revisa la configuración json del job.");
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
                    "El fichero [%s] no se ha encontrado ni en el directorio de trabajo [%s] "
                            + "ni en la carpeta de recursos de la Aplicación [%s]",
                    fName, TaskResourceAccessor.getWorkingFile(gContxt, fName),
                    TaskResourceAccessor.getWorkingFile(gContxt, fName)));
        }
        return file;
    }

}
