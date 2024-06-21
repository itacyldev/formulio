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

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.ContextDebugger;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * Writes the content of the context in file
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ContextDebugProcessor extends AbstractProcessor
        implements NonIterProcessor {

    private String output = "file";

    @Override
    public void process() throws TaskException {
        configureOutputFile();
        CompositeContext ctx = this.getGlobalContext();
        List<String> printable = ContextDebugger.getPrintable(ctx);
        if (output.toLowerCase().equals("log")) {
            LOGGER.info(String.valueOf(printable));
        } else if (output.toLowerCase().equals("file")) {
            try {
                FileUtils.writeLines(new File(getOutputFile()), printable);
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
