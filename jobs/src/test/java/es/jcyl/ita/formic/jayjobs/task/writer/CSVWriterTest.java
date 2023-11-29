package es.jcyl.ita.formic.jayjobs.task.writer;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.reader.RandomDataReader;
import es.jcyl.ita.formic.sharedTest.utils.JobContextTestUtils;

import static org.mockito.Mockito.*;

public class CSVWriterTest {

    @Test
    public void testWriteCSV() throws TaskException, IOException {
        // create task mock and set global and task contexts
        Context taskContext = new BasicContext("t1");
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(JobContextTestUtils.createJobExecContext());

        // create writer and link to task
        CSVWriter writer = new CSVWriter();
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

        // check the file has been created
        String outputFile = writer.getOutputFile();
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        // read as string and count number of lines
        String fileContent = FileUtils.readFileToString(f, "UTF-8");
        Assert.assertEquals(EXPECTED_NUM_LINES, fileContent.split("\\n").length-1); // minus header line
    }
}
