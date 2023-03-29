package es.jcyl.ita.formic.jayjobs.task.writer;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

public abstract class AbstractWriter extends AbstractTaskSepItem implements Writer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractWriter.class);
    protected static final DateFormat timeStamper = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");

    private Integer pageSize;
    private Integer offset;
    private Boolean paginate;
    protected String outputFile;

    @Override
    public void setPageSize(Integer size) {
        pageSize = size;
    }

    @Override
    public void setOffset(Integer size) {
        offset = size;
    }

    @Override
    public void setPaginate(Boolean active) {
        paginate = active;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Boolean getPaginate() {
        return paginate;
    }

    public Integer getOffset() {
        return offset;
    }

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
        this.outputFile = TaskResourceAccessor.locateOutputFile(this.getGlobalContext(), this.outputFile, ".csv");
        LOGGER.info("Output file path: " + this.outputFile);
        return new File(outputFile);
    }

    private String getOutputFileExtension() {
        return "out";
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
