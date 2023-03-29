package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.processor.UnzipProcessorTest;
import es.jcyl.ita.formic.jayjobs.utils.TestFileUtils;

public class ZipFilesCmdTest {

	@Test
	public void testExecute() throws Exception {
		// create a temporal zip with two files inside
		String zipFileName = TestFileUtils.getTmpFileName() + ".zip";
		String f1 = TestFileUtils.createTmpFile();
		String f2 = TestFileUtils.createTmpFile();
		String f3 = TestFileUtils.createTmpFile();

		FileCommand command = CommandParser.parseCommand(String.format("zip %s %s %s %s", zipFileName, f1, f2, f3));
		command.execute(new BasicContext("t1"));

		ZipFilesCmd cmd = (ZipFilesCmd) command;

		assertEquals(zipFileName, cmd.getZipFile());
		assertEquals(3, cmd.getInputFiles().length);

		assertTrue((new File(zipFileName).exists()));
	}
}
