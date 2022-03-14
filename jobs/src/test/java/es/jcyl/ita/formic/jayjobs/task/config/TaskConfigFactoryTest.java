package es.jcyl.ita.formic.jayjobs.task.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.processor.CartodruidSyncProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TaskConfigFactoryTest {

    @Test
    public void readBasicTask() throws Exception {
//        String json = TestUtils.readAsString(TestUtils.findFile("basic_tasks.json"));

        String json = "{\n" +
                "    \"name\": \"t2\",\n" +
                "    \"processor\": {\n" +
                "      \"type\": \"contextPopulateProcessor\",\n" +
                "      \"value\": 123.45\n" +
                "    }" +
                "}";

        TaskConfigFactory factory = TaskConfigFactory.getInstance();

        Context ctx = new BasicContext("");

        Task task = factory.getTask(json, ctx);

        assertNotNull(task);

    }


    @Test
    public void readCartoDruidSyncTask() throws Exception {


        String json = "{\n" +
                "    \"name\": \"t1\",\n" +
                "    \"processor\": {\n" +
                "      \"type\": \"cartodruidSync\",\n" +
                "      \"endpoint\": \"sampleEndpoint\",\n" +
                "      \"workspace\": \"test\",\n" +
                "      \"password\": \"password\",\n" +
                "      \"files\": [\n" +
                "          {\n"+
                "            \"name\": \"file1\","+
                "            \"readwrite\": \"RW\","+
                "            \"compress\": true"+
                "          },\n"+
                "          {\n"+
                "            \"name\": \"file2\","+
                "            \"readwrite\": \"RW\","+
                "            \"compress\": true"+
                "          }\n"+
                "        ]\n "+
                "    }" +
                "}";

        TaskConfigFactory factory = TaskConfigFactory.getInstance();

        Context ctx = new BasicContext("");

        NonIterTask task = (NonIterTask)factory.getTask(json, ctx);
        assertNotNull(task);

        List<NonIterProcessor> processors = task.getProcessors();
        assertNotNull(processors);
        assertEquals(1, processors.size());

//        CartodruidSyncProcessor processor = (CartodruidSyncProcessor)processors.get(0);
//        assertEquals("sampleEndpoint",processor.getEndpoint());
//        assertEquals("test",processor.getWorkspace());
//        assertEquals("password",processor.getPassword());
//
//        List<SyncFile>  files = processor.getFiles();
//        assertNotNull(files);
//        assertEquals(2, files.size());
//        SyncFile file1 = files.get(0);
//        SyncFile file2 = files.get(1);
//
//        assertEquals("file1", file1.getName());
//        assertEquals("file2", file2.getName());
    }
}

