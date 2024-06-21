package es.jcyl.ita.formic.jayjobs.task.processor.file;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class CopyFilesCmd implements FileCommand {
    private String source;
    private String dest;

    @Override
    public void execute(Context context) throws TaskException {
        try {
            File fDestination = new File(dest);
            if(fDestination.isDirectory()){
                // destination is a folder
                FileUtils.copyToDirectory(new File(source), fDestination);
            } else {
                // destination is a file
                FileUtils.copyFile(new File(source), fDestination);
            }
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    public void parse(CmdParsed parsedCmd) {
        if (ArrayUtils.getLength(parsedCmd.args) != 2) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments." +
                    "Usage: copy source destination. Found %s", parsedCmd));
        }
        this.source = parsedCmd.args[0];
        this.dest = parsedCmd.args[1];

        if (!(new File(source)).exists()) {
            throw new IllegalArgumentException(String.format("File to copy doesn't exists: [%s]", this.source));
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
}
