package es.jcyl.ita.formic.jayjobs.task.processor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.sharedTest.utils.TestContextUtils;
import es.jcyl.ita.formic.sharedTest.utils.TestFileUtils;

public class ZipProcessorTest {

	@Test
	public void testProcess() throws TaskException {
		Context taskContext = new BasicContext("t1");
		// create task mock and set contexts
		Task taskMock = mock(Task.class);
		when(taskMock.getTaskContext()).thenReturn(taskContext);
		when(taskMock.getGlobalContext()).thenReturn(TestContextUtils.createGlobalContext());

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
		assertTrue(out.exists());
	}

}
