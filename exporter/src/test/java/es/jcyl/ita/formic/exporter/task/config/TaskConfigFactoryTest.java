package es.jcyl.ita.formic.exporter.task.config;

import org.junit.Test;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.exporter.task.models.Task;

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
                "      \"type\": \"contextPopulator\",\n" +
                "      \"value\": 123.45\n" +
                "    }" +
                "}";

        TaskConfigFactory factory = TaskConfigFactory.getInstance();

        Context ctx = new BasicContext("");

        Task task = factory.getTask(json, ctx);

        org.junit.Assert.assertNotNull(task);

    }
}

