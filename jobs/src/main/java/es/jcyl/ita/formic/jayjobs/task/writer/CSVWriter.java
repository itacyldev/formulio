package es.jcyl.ita.formic.jayjobs.task.writer;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import util.Log;

/**
 * Writer implementation to output reader resultSet as delimited text files.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class CSVWriter extends AbstractWriter {
    private static final DateFormat timestamper = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private static final Object DEFAULT_EXTENSION = "csv";
    /**
     * ************************************
     * Configurable parameters via JSON
     * ************************************
     */
    private String outputFile;
    private boolean writeHeader = true;
    private String columnSep = ";";
    private String quoteChar = "\"";
    private String escapeChar = "\\";
    private String lineEnd = com.opencsv.CSVWriter.DEFAULT_LINE_END;
    /**
     * Headers texts
     */
    private String headers = "";
    /**
     * Selected columns from the reader and write-order in csv outputFile.
     */
    private String selectedColumns = "";

    /**
     * ************************************
     * Internal state
     * ************************************
     */
    private int count = 0;
    private boolean isFirstRow = true;
    private String[] selectedColumnsKeys = null;

    private FileWriter fileOutputfile;
    private com.opencsv.CSVWriter csvWriter;

    @Override
    public void open() throws TaskException {
        configureOutputFile();
        configureWriter();
    }

    public void write(RecordPage page) throws TaskException {
        List<Map<String, Object>> results = page.getResults();
        writeData(results);
    }

    private void configureWriter() throws TaskException {
        try {
            // create FileWriter object with file as parameter
            this.fileOutputfile = new FileWriter(this.outputFile);

            char QUOTE_CHAR;
            if (StringUtils.isEmpty(this.quoteChar)) {
                QUOTE_CHAR = com.opencsv.CSVWriter.NO_QUOTE_CHARACTER;
            } else {
                QUOTE_CHAR = this.quoteChar.charAt(0);
            }

            char ESCAPE_CHAR;
            if (StringUtils.isEmpty(this.escapeChar)) {
                ESCAPE_CHAR = com.opencsv.CSVWriter.NO_ESCAPE_CHARACTER;
            } else {
                ESCAPE_CHAR = this.escapeChar.charAt(0);
            }
            // create CSVWriter object filewriter object as parameter
            this.csvWriter = new com.opencsv.CSVWriter(this.fileOutputfile,
                    this.columnSep.charAt(0), QUOTE_CHAR, ESCAPE_CHAR,
                    this.lineEnd);
        } catch (Exception e) {
            throw new TaskException(
                    "An occurred during the CSV writer initialization to access the file "
                            + this.outputFile, e);
        }
    }

    private void writeData(List<Map<String, Object>> resultados) throws TaskException {
        if (resultados == null) {
            return;
        }
        try {
            for (Map<String, Object> row : resultados) {
                this.count += resultados.size();
                if (this.isFirstRow) {
                    this.selectedColumnsKeys = this.getSelectedColumns(row);
                    if (this.writeHeader) {
                        // adding header to csv
                        String[] header = getHeader(row);
                        this.csvWriter.writeNext(header);
                    }
                    this.isFirstRow = false;
                }
                String[] line = convertData(row);
                this.csvWriter.writeNext(line);
            }
        } catch (Exception e) {
            throw new TaskException(
                    "An occurred while reading the CSV file " + this.outputFile, e);
        }
    }

    private String[] convertData(Map<String, Object> row) {
        String[] line = new String[this.selectedColumnsKeys.length];
        for (int i = 0; i < this.selectedColumnsKeys.length; i++) {
            line[i] = String.valueOf(row.get(this.selectedColumnsKeys[i]));
        }
        return line;
    }

    private String[] getHeader(Map<String, Object> row) {
        if (StringUtils.isBlank(this.headers)) {
            return this.selectedColumnsKeys;
        } else {
            return this.headers.split(",");
        }
    }

    /**
     * Defines the selected strings that will be used to select the columns to of the CSV file.
     *
     * @param row
     * @return
     */
    private String[] getSelectedColumns(Map<String, Object> row) throws TaskException {

        Set<String> kSet = row.keySet();
        if (StringUtils.isBlank(this.selectedColumns)) {
            // Use the map keys as headers
            return kSet.toArray(new String[kSet.size()]);
        } else {
            String[] headerNames = this.headers.split(",");
            List<String> selectedCols = new ArrayList<String>();
            List<String> notFoundCols = new ArrayList<String>();

            boolean found = false;
            for (String hd : headerNames) {
                found = false;
                for (String k : kSet) {
                    if (hd.equalsIgnoreCase(k)) {
                        found = true;
                        selectedCols.add(k);
                        break;
                    }
                }
                if (!found) {
                    notFoundCols.add(hd);
                }
            }
            if (notFoundCols.size() > 0) {
                throw new TaskException(String.format(
                        "The CSVWriter is not properly configured, these columns haven't " +
                                "been found in the row: [%s]", notFoundCols));
            }
            return selectedCols.toArray(new String[selectedCols.size()]);
        }
    }

    /**
     * Determines the final name of the output file.
     */
    private void configureOutputFile() {

        if (StringUtils.isBlank(this.outputFile)) {
            this.outputFile = String.format("%s_%s.%s",
                    RandomStringUtils.randomAlphanumeric(10),
                    timestamper.format(new Date()), DEFAULT_EXTENSION);
            Log.info(String.format(
                    "The 'outputFile' attribute is not set in the CSVWriter, " +
                            "a random file name will be used [%s].", this.outputFile));
        }
        this.outputFile = TaskResourceAccessor
                .getWorkingFile(this.getGlobalContext(), this.outputFile);
        Log.info("Output file path: " + this.outputFile);
    }

    @Override
    public void close() throws TaskException {
        try {
            if (this.csvWriter != null) {
                this.csvWriter.close();
            }
            // publish the created outputFile name in the task context
            this.getTaskContext().put("outputFile", this.outputFile);
            this.getTaskContext().put("count", this.count);
        } catch (IOException e) {
            throw new TaskException(
                    "An error occurred while trying to close the output stream on file "
                            + this.outputFile, e);
        }
        Log.info("CSV file successfully written.");
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isWriteHeader() {
        return this.writeHeader;
    }

    public void setWriteHeader(boolean writeHeader) {
        this.writeHeader = writeHeader;
    }

    public String getHeaders() {
        return this.headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getSelectedColumns() {
        return this.selectedColumns;
    }

    public void setSelectedColumns(String selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public String getColumnSep() {
        return this.columnSep;
    }

    public void setColumnSep(String columnSep) {
        this.columnSep = columnSep;
    }

    public String getQuoteChar() {
        return this.quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    public String getLineEnd() {
        return this.lineEnd;
    }

    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

}
