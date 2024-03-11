package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.sharedTest.utils.TestFileUtils;

public class RmFilesCmdTest {

	@Test
	public void testExecute() throws Exception {
		String file1 = TestFileUtils.createTmpFile();
		String file2 = TestFileUtils.createTmpFile();

		FileCommand command = CommandParser.parseCommand(String.format("rm %s %s", file1, file2));
		command.execute(new BasicContext("t1"));

		RmFilesCmd cmd = (RmFilesCmd) command;
		assertEquals(2, cmd.getFiles().length);
		assertTrue(!(new File(file1)).exists());
		assertTrue(!(new File(file2)).exists());
	}
}
