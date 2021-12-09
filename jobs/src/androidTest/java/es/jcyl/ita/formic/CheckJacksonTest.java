package es.jcyl.ita.formic;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CheckJacksonTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("es.jcyl.ita.formic.test", appContext.getPackageName());

        File file = TestUtils.getResourceAsFile("job_exporter.json");

        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            System.out.println(data);
        }
        myReader.close();

        ObjectMapper mapper = new ObjectMapper();
        // try basic mapping using task and
        Map<?, ?> map = mapper.readValue(file, Map.class);
        System.out.println(map);
    }
}

