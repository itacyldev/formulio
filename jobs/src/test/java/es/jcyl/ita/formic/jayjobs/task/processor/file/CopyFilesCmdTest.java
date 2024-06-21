package es.jcyl.ita.formic.jayjobs.task.processor.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.sharedTest.utils.TestFileUtils;

public class CopyFilesCmdTest {

    @Test
    public void testExecute() throws Exception {
        String file1 = TestFileUtils.createTmpFile();
        String file2 = TestFileUtils.getTmpFileName();
        FileCommand command = CommandParser.parseCommand(String.format("copy %s %s", file1, file2));
        command.execute(new BasicContext("t1"));

        CopyFilesCmd cmd = (CopyFilesCmd) command;
        assertEquals(file1, cmd.getSource());
        assertEquals(file2, cmd.getDest());

        File f = new File(file2);
        assertTrue(f.exists());
    }
}
