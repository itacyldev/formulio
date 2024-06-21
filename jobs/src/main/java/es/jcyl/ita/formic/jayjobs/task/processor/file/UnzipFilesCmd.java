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
import es.jcyl.ita.formic.jayjobs.task.processor.UnzipProcessor;

/**
 * Deletes files and folders.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class UnzipFilesCmd implements FileCommand {
    private String input;
    private String destFolder;

    @Override
    public void execute(Context context) throws TaskException {
        UnzipProcessor processor = new UnzipProcessor();
        processor.setFile(new File(this.input));
        processor.setOutputFolder(destFolder);
        processor.extractFiles();
    }

    @Override
    public void parse(CmdParsed parsedCmd) {
        if (ArrayUtils.getLength(parsedCmd.args) != 2) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments. " +
                    "Usage: unzip file.zip destination_folder. Found %s", parsedCmd));
        }
        this.input = parsedCmd.args[0];
        this.destFolder = parsedCmd.args[1];
        if (!(new File(input)).exists()) {
            throw new IllegalArgumentException(String.format("File to unzip doesn't exists: [%s]", this.input));
        }
        if (!(new File(destFolder)).exists()) {
            throw new IllegalArgumentException(String.format("Destination folder doesn't exists: [%s]", this.destFolder));
        }
        if (!(new File(destFolder)).isDirectory()) {
            throw new IllegalArgumentException(String.format("The destination is not a directory: [%s]", this.destFolder));
        }
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getDestFolder() {
        return destFolder;
    }

    public void setDestFolder(String destFolder) {
        this.destFolder = destFolder;
    }
}
