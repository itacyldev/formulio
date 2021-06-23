package es.jcyl.ita.formic.forms.integration;
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

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.MainControllerMock;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.datatable.DatatableWidget;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.ui.UIDatatableBuilder;
import es.jcyl.ita.formic.forms.context.impl.RepoAccessContext;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.scripts.RhinoViewRenderHandler;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.utils.DevFormNav;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.memo.MemoRepository;
import es.jcyl.ita.formic.repo.memo.source.MemoSource;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectRhinoScriptIntegrationTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        DevConsole.setLevel(Log.DEBUG);
    }

    @Test
    public void testFormConfig() throws Exception {

        File baseFolder = TestUtils.findFile("config");
        Config.init(baseFolder.getAbsolutePath());
        Config config = Config.getInstance();

        // Open project config
        ProjectRepository projectRepo = config.getProjectRepo();
        Project prj = projectRepo.findById("project1");
        Config.getInstance().setCurrentProject(prj);

        // mock main controller
        MainController mc = new MainControllerMock();
        mc.setContext(config.getGlobalContext());

        // navigate to form
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        DevFormNav formNav = new DevFormNav(ctx, mc);
        formNav.nav("form2-edit1");
    }

    @Test
    public void testUseRepoInMemory() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        // register memory repo
        RepositoryFactory factory = RepositoryFactory.getInstance();
        RepositoryBuilder repoBuilder = factory.getBuilder(new MemoSource("memoRepoTest"));
        MemoRepository repo = (MemoRepository) repoBuilder.build();
        repo.setPropertyNames(new String[]{"prop1", "prop2", "prop3"});

        // create a form with a datatable, and set repo to datatable
        UIForm form = DevFormBuilder.createOneFieldForm();
        ViewController formController = DevFormBuilder.createFormEditController(form);
        UIDatatableBuilder dtBuilder = new UIDatatableBuilder("table");
        UIDatatable table = dtBuilder.createDataTableFromRepo(repo);
        form.addChild(table);
        // call function
        form.setOnBeforeRenderAction("mixRepoData");

        // Store JS related to form controller
        File srcFile = TestUtils.findFile("scripts/mixRepoData.js");
        ScriptEngine engine = ScriptEngine.getInstance();
        engine.store(formController.getId(), FileUtils.readFileToString(srcFile, "UTF-8"));

        // prepare rendering env.
        RenderingEnv env = prepareRenderingEnv(ctx, engine);

        // add event handler to execute scripts during component rendering
        ViewRenderer renderer = new ViewRenderer();
        renderer.setEventHandler(new RhinoViewRenderHandler(ScriptEngine.getInstance()));

        // render form
        engine.initScope(formController.getId());
        View formView = renderer.render(env, form);

        // The script will insert new entities in the memory repo, the number of rows
        // of the datable widget
        long count = repo.count(null);
        Assert.assertEquals(10, count);

        View dtView = ViewHelper.findComponentWidget(formView, table);
        Assert.assertNotNull(dtView);
        Assert.assertNotNull(((DatatableWidget) dtView).getEntities());
        Assert.assertEquals(10, ((DatatableWidget) dtView).getEntities().size());
    }

    private RenderingEnv prepareRenderingEnv(Context ctx, ScriptEngine engine) {
        // Prepare global CONTEXT
        CompositeContext globalContext = ContextTestUtils.createGlobalContext();
        globalContext.put("repos", new RepoAccessContext());
        ActionController mcAC = mock(ActionController.class);

        // Prepare rendering environment
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(globalContext);
        env.setAndroidContext(ctx);

        // init scripting environment with common objects
        Map<String, Object> props = new HashMap<>();
        props.put("ctx", globalContext);
        props.put("renderEnv", env);
        props.put("console", new DevConsole());
        engine.initEngine(props);
        return env;
    }

}
