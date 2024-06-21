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

import java.util.Locale;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class CommandParser {
    public enum CmdType {COPY, RM, UNZIP, ZIP, MKDIR}

    public static Class<? extends FileCommand> getCmdType(String cmdLine) {
        String[] parts = cmdLine.split(" ");

        CmdType cmdType;
        String strCmdType = parts[0].toUpperCase();
        try {
            cmdType = CmdType.valueOf(strCmdType);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Command type not" +
                    " recognized [%s] in line [%s]", strCmdType, cmdLine));
        }
        switch (cmdType) {
            case COPY:
                return CopyFilesCmd.class;
            case RM:
                return RmFilesCmd.class;
            case UNZIP:
                return UnzipFilesCmd.class;
            case ZIP:
                return ZipFilesCmd.class;
            case MKDIR:
                return MkdirCmd.class;
            default:
                throw new IllegalArgumentException("Command Not supported!");
        }
    }

    public static FileCommand parseCommand(String cmdLine) {
        Class<? extends FileCommand> cmdType = CommandParser.getCmdType(cmdLine);
        try {
            FileCommand command = cmdType.newInstance();
            CmdParsed parsedCmd = parse(cmdLine);
            command.parse(parsedCmd);
            return command;
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occurred while parsing command " + cmdLine, e);
        }
    }

    public static CmdParsed parse(String line) {
        String[] parts = line.split(" ");
        String[] args = (parts.length == 1) ? null :
                ArrayUtils.subarray(parts, 1, parts.length);
        return new CmdParsed(parts[0], args);
    }
}
