package es.jcyl.ita.formic.jayjobs.task.processor;

import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtcyl.sync.config.SyncFile;
import es.jcyl.ita.crtdrd.task.TaskProgressNotifier;
import es.jcyl.ita.crtsyn.v2.resources.SyncResource;
import es.jcyl.ita.crtsyn.v2.resources.SyncStateEnum;
import es.jcyl.ita.crtsyn.v2.services.SyncFileService;
import es.jcyl.ita.crtsyn.v2.services.SyncService;
import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.OrderedCompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.utils.DbTestUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CartodruidSyncProcessorTest {
    android.content.Context ctx;

    String dbFileName = "";
    String tableName = "";
    Task taskMock = null;
    TaskProgressNotifier notifierMock = null;
    File tempDirectory;

    @Before
    public void setup() throws Exception {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        //ctx.setTheme(R.style.FormudruidLight);

        initContext();
    }

    private void initContext() {
        Context taskContext = new BasicContext("t1");
        // create task mock and set the task context
        taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);

        // create user context
        CompositeContext globalContext = new OrderedCompositeContext();
        Context userContext = new BasicContext("user");
        userContext.put("id", "test");
        globalContext.addContext(userContext);
        when(taskMock.getGlobalContext()).thenReturn(globalContext);

        notifierMock = mock(TaskProgressNotifier.class);


    }

    private void initValues() {
        tempDirectory = TestUtils.createTempDirectory();
        dbFileName = String.format("%s/%s.sqlite", tempDirectory.getAbsolutePath(),
                RandomStringUtils.randomAlphabetic(10));
        tableName = RandomStringUtils.randomAlphabetic(10);
    }

    private SyncService mockSyncService(String state) throws Exception {
        Integer syncId = RandomUtils.nextInt();

        if (state == null) {
            state = SyncStateEnum.FINISHED.name();
        }

        SyncService syncServiceMock = mock(SyncService.class);
        when(syncServiceMock.createInServer(any(File.class))).thenReturn(syncId);

        SyncResource syncResource = new SyncResource();
        syncResource.setState(state);
        List<String> resources = new ArrayList<>();
        resources.add("resource1");
        syncResource.setResources(resources);
        when(syncServiceMock.getSync(syncId)).thenReturn(syncResource);

        return syncServiceMock;
    }

    @Test
    public void testPush() throws TaskException {
        initValues();
        DbTestUtils.createPopulatedDatabase(dbFileName, tableName, 10);


        CartodruidSyncProcessor processor = new CartodruidSyncProcessor();
        processor.setNotifier(notifierMock);
        processor.setTask(taskMock);
        processor.setEndpoint("endpoint");
        processor.setPassword("password");

        List<SyncFile> files = new ArrayList<>();
        SyncFile file = new SyncFile();
        file.setCompress(true);
        file.setReadwrite("W");
        file.setName(dbFileName);
        files.add(file);

        processor.setFiles(files);

        processor.setWorkspace("test");
        processor.setTmpFolder(tempDirectory);
        processor.setDownloadFolder(tempDirectory);

        SyncService syncServiceMock = null;
        try {
            syncServiceMock = mockSyncService(null);
        } catch (Exception ex) {
            fail();
        }
        processor.setSyncService(syncServiceMock);

        CartodruidSyncProcessor processor1 = Mockito.spy(processor);
        processor1.process();

        verify(processor1, times(1)).finishExecution();
    }



    @Test
    public void testPushAndPull() throws TaskException {
        initValues();
        DbTestUtils.createPopulatedDatabase(dbFileName, tableName, 10);

        CartodruidSyncProcessor processor = new CartodruidSyncProcessor();
        processor.setNotifier(notifierMock);
        processor.setTask(taskMock);
        processor.setEndpoint("endpoint");
        processor.setPassword("password");

        List<SyncFile> files = new ArrayList<>();
        SyncFile file = new SyncFile();
        file.setCompress(true);
        file.setReadwrite("R");
        file.setName(dbFileName);
        files.add(file);

        processor.setFiles(files);

        processor.setWorkspace("test");
        processor.setTmpFolder(tempDirectory);
        processor.setDownloadFolder(tempDirectory);

        SyncService syncServiceMock = null;
        try {
            syncServiceMock = mockSyncService(null);
        } catch (Exception ex) {
            fail();
        }
        processor.setSyncService(syncServiceMock);

        SyncFileService syncFileServiceMock = mock(SyncFileService.class);
        processor.setSyncFileService(syncFileServiceMock);

        CartodruidSyncProcessor processor1 = Mockito.spy(processor);
        processor1.process();

        verify(processor1, times(1)).finishExecution();
    }

}
