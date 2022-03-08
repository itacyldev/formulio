package es.jcyl.ita.formic.jayjobs.task.processor;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.ContextDebugger;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

/**
 * Writes the content of the context in file
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ContextDebugProcessor extends AbstractProcessor
        implements NonIterProcessor {
    protected static final org.apache.commons.logging.Log LOGGER = LogFactory.getLog(ContextDebugProcessor.class);
    private static final DateFormat timestamper = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private String output = "file";

    private String outputFile;

    @Override
    public void process() throws TaskException {
        configureOutputFile();
        CompositeContext ctx = this.getGlobalContext();
        List<String> printable = ContextDebugger.getPrintable(ctx);
        if (output.toLowerCase().equals("log")) {
            LOGGER.info(printable);
        } else if (output.toLowerCase().equals("file")) {
            try {
                FileUtils.writeLines(new File(this.outputFile), printable);
            } catch (IOException e) {
                throw new TaskException(e);
            }
        } else {
            // console
            for(String s: printable){
                System.out.println(s);
            }
        }
    }

    /**
     * Determines the final name of the output file.
     */
    private void configureOutputFile() {
        if (StringUtils.isBlank(this.outputFile)) {
            this.outputFile = String.format("%s_%s.%s", RandomStringUtils.randomAlphanumeric(10),
                    timestamper.format(new Date()), "log");
            LOGGER.info(String.format(
                    "The 'outputFile' attribute is not set in the ContextDebugProcessor, a random file name will be " +
                            "used [%s].", this.outputFile));
        }
        this.outputFile = TaskResourceAccessor
                .getWorkingFile(this.getGlobalContext(), this.outputFile);
        LOGGER.info("Output file path: " + this.outputFile);
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
