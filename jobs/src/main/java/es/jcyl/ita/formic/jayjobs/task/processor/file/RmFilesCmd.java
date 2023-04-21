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

import java.io.File;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * Deletes files and folders.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class RmFilesCmd implements FileCommand {
    private String[] files;

    @Override
    public void execute(Context context) throws TaskException {
        for (String file : files) {
            FileUtils.deleteQuietly(new File(file));
        }
    }

    @Override
    public void parse(CmdParsed parsedCmd) {
        if (ArrayUtils.getLength(parsedCmd.args) == 0) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments. " +
                    "Usage: rm file1 [file2 file3]. Found %s", parsedCmd));
        }
        this.files = parsedCmd.args;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }
}
