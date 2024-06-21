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

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.processor.ZipProcessor;

/**
 * Deletes files and folders.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ZipFilesCmd implements FileCommand {
    private String[] inputFiles;
    private String zipFile;

    @Override
    public void execute(Context context) throws TaskException {
        ZipProcessor processor = new ZipProcessor();
        List<File> lstFiles = new ArrayList<>();
        for(String file: inputFiles){
            lstFiles.add(new File(file));
        }
        processor.setInputFileObjects(lstFiles);
        processor.setOutputFile(zipFile);
        processor.compressFiles();
    }

    @Override
    public void parse(CmdParsed parsedCmd) {
        if (ArrayUtils.getLength(parsedCmd.args) < 2) {
            throw new IllegalArgumentException(String.format("Wrong number of arguments. " +
                    "Usage: zip file.zip file1 [file2 file3]", parsedCmd));
        }
        this.zipFile = parsedCmd.args[0];
        this.inputFiles = ArrayUtils.subarray(parsedCmd.args,1,parsedCmd.args.length);
        for(String inputFile: inputFiles){
            if (!(new File(inputFile)).exists()) {
                throw new IllegalArgumentException(String.format("InputFile doesn't exists: [%s]", inputFile));
            }
        }
    }

    public String[] getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(String[] inputFiles) {
        this.inputFiles = inputFiles;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
}
