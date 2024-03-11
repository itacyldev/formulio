package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.sharedTest.utils.TestFileUtils;

public class MkdirCmdTest {

	@Test
	public void testExecute() throws Exception {
		String folderName = TestFileUtils.getBuildFolder() + "/" + RandomStringUtils.randomAlphanumeric(10);

		FileCommand command = CommandParser.parseCommand(String.format("mkdir %s", folderName));
		command.execute(new BasicContext("t1"));

		MkdirCmd cmd = (MkdirCmd) command;
		assertEquals(folderName, cmd.getFolder());
		assertTrue((new File(folderName)).exists());
		assertTrue((new File(folderName)).isDirectory());
	}
}
