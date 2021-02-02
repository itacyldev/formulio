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

import android.util.Log;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.collections.CollectionUtils;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormControllerFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static es.jcyl.ita.formic.repo.test.utils.AssertUtils.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectWithMetaIntegrationTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
//        DevConsole.setLevel(Log.DEBUG);
    }


    /**
     * Check formEdit and formList are properly created
     */
    private static final String TEST_BASIC1 = "<main repo=\"contacts\"/>";

    @Test
    public void testInsertEntityWithCalculatedProps() throws Exception {
        File baseFolder = TestUtils.findFile("config/project1");
        Project prj = ProjectRepository.createFromFolder(baseFolder);

        Config config = Config.getInstance();
        config.setCurrentProject(prj);

        RepositoryFactory repoFactory = RepositoryFactory.getInstance();
        EditableRepository repo = repoFactory.getEditableRepo("superHRepo");

        // list all entities
        List list = repo.listAll();

        // check the property "current_time" is set
        Entity e1 = (Entity) list.get(0);
        Date current_time = (Date) e1.get("current_time");
        assertThat(current_time, notNullValue());

        // modify the entity twice and check the creation_date and update_date have been set
        repo.save(e1);
        Entity e1Saved = repo.findById(e1.getId());
        Date creation_date = (Date) e1Saved.get("creation_date");
        assertThat(creation_date, notNullValue());

        repo.save(e1);
        e1Saved = repo.findById(e1.getId());
        Date update_date = (Date) e1Saved.get("update_date");
        assertThat(update_date, notNullValue());
        assertThat(update_date, greaterThan(creation_date));
        // check creation time hasn't been modified
        assertThat((Date) e1Saved.get("creation_date"), equalTo(creation_date));
    }

}
