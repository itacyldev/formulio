package es.jcyl.ita.formic.forms.repo.config;
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

import android.net.Uri;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.FormConfig;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.dummy.DummyFormConfigReader;
import es.jcyl.ita.formic.forms.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.project.ProjectResource;
import es.jcyl.ita.formic.forms.project.handlers.FormConfigHandler;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class TestXmlConfigReader {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
        DevConsole.clear();

    }

    @Test
    public void testDummyConfig() {

        RepositoryUtils.registerMock("agents");
        RepositoryUtils.registerMock("provincia");
        RepositoryUtils.registerMock("municipio");
        DummyFormConfigReader reader = new DummyFormConfigReader();
        reader.read("", Uri.EMPTY);
    }

    /**
     * Reads basic xml and checks the configNode tree is correctly read.
     *
     * @throws Exception
     */
    @Test
    public void testReadConfigNodes() throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlConfigFileReader reader = new XmlConfigFileReader();

        File file = TestUtils.findFile("config/formConfig.xml");

        InputStream is = new FileInputStream(file);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(is, null);

        ConfigNode node = reader.readFile(xpp);

        Assert.assertNotNull(node);
        // check attributes
        Assert.assertNotNull(node.getName());
        Assert.assertNotNull(node.getAttribute("description"));
        Assert.assertNotNull(node.getAttribute("name"));
        Assert.assertNotNull(node.getAttribute("repo"));
        Assert.assertEquals(node.getAttribute("id"), "form1");

        // check nested elements ids
        List<ConfigNode> kids = node.getChildren();
        Assert.assertNotNull(kids);
        // list
        Assert.assertEquals(kids.get(0).getName(), "list");
        Assert.assertNotNull(kids.get(0).getId());
        // edit1
        Assert.assertEquals(kids.get(1).getName(), "edit");
        Assert.assertEquals(kids.get(1).getId(), "form1#edit");
        // edit2
        Assert.assertEquals(kids.get(2).getName(), "edit");
        Assert.assertNotNull(kids.get(2).getId());
    }

    @Test
    public void testBasicBuild() throws Exception {
        File file = TestUtils.findFile("config/formConfig.xml");

        FormConfigHandler handler = new FormConfigHandler();
        handler.setFormConfigRepo(mock(FormConfigRepository.class));

        Project p = ProjectRepository.createFromFolder(new File("."));
        ProjectResource resource = new ProjectResource(p, file, ProjectResource.ResourceType.FORM);
        FormConfig formConfig = handler.handle(resource);

        Assert.assertNotNull(formConfig);
        Assert.assertNotNull(formConfig.getList());
        Assert.assertNotNull(formConfig.getEdits());
        Assert.assertEquals(2, formConfig.getEdits().size());
        Assert.assertNotNull(formConfig.getEdits().get(0));
        Assert.assertNotNull(formConfig.getEdits().get(1));

        // check every object has a repo value
        Assert.assertNotNull(formConfig.getRepo());
        Assert.assertNotNull(formConfig.getList().getRepo());
        for (FormEditController c : formConfig.getEdits()) {
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getRepo());
        }
    }

}
