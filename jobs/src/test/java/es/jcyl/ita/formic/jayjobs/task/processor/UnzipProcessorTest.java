package es.jcyl.ita.formic.jayjobs.task.processor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static es.jcyl.ita.formic.repo.test.utils.AssertUtils.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.sharedTest.utils.TestContextUtils;
import es.jcyl.ita.formic.sharedTest.utils.TestFileUtils;

public class UnzipProcessorTest {

    @Test
    public void testProcess() throws IOException, TaskException {
        // prepare files
        String zipFile = createTmpZip();
        String outputFolder = TestFileUtils.createTmpFolder();

        // prepare task and processor
        Task task = createTask();
        UnzipProcessor processor = new UnzipProcessor();
        processor.setTask(task);
        processor.setInputFile(zipFile);
        processor.setOutputFolder(outputFolder);

        // Act
        processor.process();

        File folder = new File(outputFolder);
        assertTrue(folder.exists());
        String[] list = folder.list();
        assertEquals(2, list.length);

        processor.isMkdir();
        processor.getInputFile();
        processor.getOutputFolder();
    }

    private static Task createTask() {
        Context taskContext = new BasicContext("t1");
        // create task mock and set contexts
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(TestContextUtils.createGlobalContext());
        return taskMock;
    }

    @Test(expected = TaskException.class)
    public void testNotExistingFolderNoMkdir() throws IOException, TaskException {
        String zipFile = createTmpZip();
        String outputFolder = "not_existing_folder_" + RandomStringUtils.randomAlphanumeric(5);

        Task task = createTask();
        UnzipProcessor processor = new UnzipProcessor();
        processor.setInputFile(zipFile);
        processor.setOutputFolder(outputFolder);
        processor.setTask(task);

        processor.process();
    }

    @Test
    public void testNotExistingFolderMkdir() throws IOException, TaskException {
        String zipFile = createTmpZip();
        String outputFolder = "not_existing_folder" + RandomStringUtils.randomAlphanumeric(5);

        Task task = createTask();
        UnzipProcessor processor = new UnzipProcessor();
        processor.setInputFile(zipFile);
        processor.setOutputFolder(outputFolder);
        processor.setTask(task);
        processor.setMkdir(true);

        processor.process();

        File folder = new File(processor.getOutputFolder());
        assertTrue(folder.exists());
    }


    @Test(expected = TaskException.class)
    public void testInvalidInput() throws IOException, TaskException {
        UnzipProcessor processor = new UnzipProcessor();
        processor.setInputFile(null);
        processor.setOutputFolder(null);
        processor.process();
    }

    public static String createTmpZip() throws TaskException {
        Task taskMock = createTask();

        ZipProcessor processor = new ZipProcessor();
        processor.setTask(taskMock);

        String file1 = TestFileUtils.createTmpFile();
        String file2 = TestFileUtils.createTmpFile();
        List<String> files = new ArrayList<String>();
        files.add(file1);
        files.add(file2);

        processor.setInputFiles(files);
        processor.process();

        String outputFile = processor.getOutputFile();
        File out = new File(outputFile);
        return out.getAbsolutePath();
    }
}
