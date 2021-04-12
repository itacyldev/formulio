package es.jcyl.ita.formic.forms.config.builders.repo;
/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class MemoRepoBuilderConfigTest {
    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST_BASIC = "<memoRepo id=\"memoRepoTest1\"/>";

    @Test
    public void testRepoRegistered() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC);
        XmlConfigUtils.readFormConfig(xml);

        Assert.assertNotNull(repoFactory.getRepo("memoRepoTest1"));
    }

    private static final String XML_TEST_PROPS_ATT = "<memoRepo id=\"memoRepoTest1\" properties=\"f1,f2,f3\"/>";
    @Test
    public void testPropertiesAttribute() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TEST_PROPS_ATT);
        XmlConfigUtils.readFormConfig(xml);

        Repository repo = repoFactory.getRepo("memoRepoTest1");
        EntityMeta meta = repo.getMeta();
        Assert.assertEquals(3+1, meta.getProperties().length); // 3 properties + id column added autom.
    }
    @AfterClass
    public static void tearDown() {

    }

}
