package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.processor.UnzipProcessorTest;
import es.jcyl.ita.formic.jayjobs.utils.TestFileUtils;

public class UnzipFilesCmdTest {

	@Test
	public void testExecute() throws Exception {
		// create a temporal zip with two files inside
		String zipFile = UnzipProcessorTest.createTmpZip();
		String tmpFolder = TestFileUtils.createTmpFolder();

		FileCommand command = CommandParser.parseCommand(String.format("unzip %s %s", zipFile, tmpFolder));
		command.execute(new BasicContext("t1"));

		UnzipFilesCmd cmd = (UnzipFilesCmd) command;

		assertEquals(zipFile, cmd.getInput());
		assertEquals(tmpFolder, cmd.getDestFolder());

		File folder = new File(tmpFolder);
		File[] files = folder.listFiles();
		assertEquals(2, files.length);
	}
}
