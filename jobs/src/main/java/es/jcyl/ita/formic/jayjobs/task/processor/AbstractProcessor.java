package es.jcyl.ita.formic.jayjobs.task.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

public abstract class AbstractProcessor extends AbstractTaskSepItem {

     protected static final Logger LOGGER = LoggerFactory.getLogger(ContextDebugProcessor.class);

    protected static final DateFormat timeStamper = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");

    private boolean failOnError = true;

    public boolean isFailOnError() {
        return this.failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    private String outputFile;
    private String outputExtension = "out";

    /**
     * Determines the final name of the output file.
     */
    protected File configureOutputFile() {
        if (StringUtils.isBlank(this.outputFile)) {
            String tag = this.getClass().getSimpleName();
            this.outputFile = String.format("%s_%s.%s", timeStamper.format(new Date()), tag, getOutputFileExtension());
            LOGGER.info(String.format(
                    "The 'outputFile' attribute is not set in the %s, a random file name will be " +
                            "used [%s].", tag, this.outputFile));
        }
        // set folder
        this.outputFile = TaskResourceAccessor
                .getWorkingFile(this.getGlobalContext(), this.outputFile);
        LOGGER.info("Output file path: " + this.outputFile);
        return new File(outputFile);
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFileExtension() {
        return outputExtension;
    }

    public void setOutputFileExtension(String outputExtension) {
        if (outputExtension.startsWith(".")) {
            outputExtension = outputExtension.substring(1);// remove dot if included
        }
        this.outputExtension = outputExtension;
    }
}
