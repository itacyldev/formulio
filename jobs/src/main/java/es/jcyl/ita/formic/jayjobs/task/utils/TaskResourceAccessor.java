package es.jcyl.ita.formic.jayjobs.task.utils;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;


public class TaskResourceAccessor {

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
}
