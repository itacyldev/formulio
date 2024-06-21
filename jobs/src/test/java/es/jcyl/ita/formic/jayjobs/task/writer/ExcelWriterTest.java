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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.reader.RandomDataReader;
import es.jcyl.ita.formic.sharedTest.utils.JobContextTestUtils;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ExcelWriterTest {

    @Test
    public void testWriteExcel() throws TaskException, IOException, InvalidFormatException {
        // create task mock and set global and task contexts
        Context taskContext = new BasicContext("t1");
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(JobContextTestUtils.createJobExecContext());

        // create writer and link to task
        ExcelWriter writer = new ExcelWriter();
        String outputFile = String.format(
                "ExcelWriterTest_testWriteExcel_%s.xlsx",
                System.currentTimeMillis());
        writer.setOutputFile(outputFile);
        writer.setSheetName("Hoja1_testWriteExcel");
        writer.setCellStart("A2");

        writer.setTask(taskMock);

        // create random data using randomReader
        RandomDataReader reader = new RandomDataReader();
        reader.setNumColumns(10);
        int EXPECTED_NUM_LINES = 20;
        reader.setMaxResults(EXPECTED_NUM_LINES);
        RecordPage page = reader.read();

        writer.open();
        writer.write(page);
        writer.close();

        // se abre el excel y se comprueba que tenga una hoja con enl nombre
        // sheetName
        File excelFile = new File(writer.getOutputFile());
        XSSFWorkbook libro = new XSSFWorkbook(new FileInputStream(excelFile));
        Assert.assertThat("El libro es nulo", libro, notNullValue());
        XSSFSheet hoja = libro.getSheet("Hoja1_testWriteExcel");
        Assert.assertThat("La hoja es nula", hoja, notNullValue());

    }
}
