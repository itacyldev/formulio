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
import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * Deletes files and folders.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class MkdirCmd implements FileCommand {
    private String folder;

    @Override
    public void execute(Context context) throws TaskException {
        try {
            FileUtils.forceMkdir(new File(folder));
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    @Override
    public void parse(CmdParsed parsedCmd) {
        if (ArrayUtils.getLength(parsedCmd.args) != 1) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments. " +
                    "Usage: mkdir folder. Found %s", parsedCmd));
        }
        this.folder = parsedCmd.args[0];
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
