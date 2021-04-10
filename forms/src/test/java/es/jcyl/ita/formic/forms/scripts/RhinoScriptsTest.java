package es.jcyl.ita.formic.forms.scripts;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.context.impl.RepoAccessContext;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class RhinoScriptsTest {
    private static final String RHINO_LOG = "var log = Packages.io.vec.ScriptAPI.log;";

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        DevConsole.setLevel(Log.ERROR);
    }

    public static void log(String msg) {
        android.util.Log.i("RHINO_LOG", msg);
    }

    @Test
    public void testAccessRepo() throws Exception {
        // create mock entity repository
        List<Entity> entities = DevDbBuilder.buildEntitiesRandomMeta(10);
        EditableRepository repo = mock(EditableRepository.class);
        when(repo.listAll()).thenReturn(entities);
        RepositoryFactory.getInstance().register("contacts", repo);

        // load script
        File file = TestUtils.findFile("scripts/repoMockAccess.js");
        String source = TestUtils.readSource(file);
        try {
            UnPrefixedCompositeContext gCtx = new UnPrefixedCompositeContext();
            BasicContext simpleTx = new BasicContext("basic");
            simpleTx.put("a", "aaa");
            simpleTx.put("bbba", "bbbbb");
            gCtx.addContext(simpleTx);
            gCtx.put("repos", new RepoAccessContext());

            runScript(source, gCtx);

        } finally {
            RepositoryFactory.getInstance().unregister("testRepo1");
        }
    }


    @Test
    public void testFormConfig() throws Exception {

        File baseFolder = TestUtils.findFile("config");
        Config.init(baseFolder.getAbsolutePath());
        Config config = Config.getInstance();

        ProjectRepository projectRepo = config.getProjectRepo();
        Project prj = projectRepo.findById("project1");
        Config.getInstance().setCurrentProject(prj);

        RepositoryFactory repoFactory = RepositoryFactory.getInstance();

        CompositeContext gCtx = new UnPrefixedCompositeContext();
        gCtx.put("repos", new RepoAccessContext());

        File file = TestUtils.findFile("scripts/repoAccess.js");
        String source = TestUtils.readSource(file);

        runScript(source, gCtx);

    }


    private void runScript(String source, CompositeContext gCtx) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        Scriptable scope = rhino.initStandardObjects();

        ScriptableObject.putProperty(scope, "out", Context.javaToJS(System.out, scope));
        ScriptableObject.putProperty(scope, "ctx", Context.javaToJS(gCtx, scope));

        Script script = rhino.compileString(source, "src", 1, null);
        script.exec(rhino, scope);
        Context.exit();
    }

}
