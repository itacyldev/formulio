package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.utils.TestContextUtils;
import es.jcyl.ita.formic.jayjobs.utils.TestFileUtils;

public class FileCmdProcessorTest {

    @Test
    public void testProcess() throws TaskException {
        Context taskContext = new BasicContext("t1");
        // create task mock and set contexts
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(TestContextUtils.createGlobalContext());

        FileCmdProcessor processor = new FileCmdProcessor();
        processor.setTask(taskMock);

        String file1 = TestFileUtils.createTmpFile();
        String file2 = TestFileUtils.createTmpFile();
        List<String> files = new ArrayList<String>();
        files.add(file1);
        files.add(file2);

        String testFolder = TestFileUtils.getBuildFolder() + "/" + RandomStringUtils.randomAlphanumeric(10);
        String testFile = TestFileUtils.createTmpFile();

        String[] cmdList = new String[]{
                String.format("mkdir %s", testFolder),
                String.format("copy %s %s", testFile, testFolder),
                String.format("zip %s %s", testFile + ".zip", testFile),
                String.format("mkdir %s", testFolder + "_uncompressed"),
                String.format("unzip %s %s", testFile + ".zip", testFolder + "_uncompressed"),
        };
        processor.setCommands(Arrays.asList(cmdList));
        processor.process();

        String fileName = FilenameUtils.getName(testFile);
        assertTrue((new File(testFolder + "_uncompressed", fileName)).exists());
    }

}
