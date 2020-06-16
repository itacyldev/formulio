package es.jcyl.ita.frmdrd.config.builders;
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

import java.util.List;

import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.elements.RepoFilter;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.RepositoryUtils;
import es.jcyl.ita.frmdrd.utils.XmlConfigUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class RepoFilterBuilderTest {

    EntityDataBuilder entityBuilder;
    private static RepoFilterBuilder filterBuilder;

    @BeforeClass
    public static void setUp() {

        filterBuilder = (RepoFilterBuilder) ComponentBuilderFactory.getInstance().getBuilder("repofilter", RepoFilter.class);

        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC =
            "  <form repo=\"contacts\">" +
                    "  <repofilter>" +
                    "    <eq property=\"profile\" value=\"agenteForestal\" mandatory=\"true\"/>" +
                    "    <eq property=\"provincia\" value=\"${view.provincia}\" />" +
                    "  </repofilter>" +
                    "</form>";


    @Test
    public void testBasicRepoFilter() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> formList = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);
        Assert.assertNotNull(formList);

        // repo must be set with parent value "contacts"
        UIForm form = formList.get(0);
        Filter filter = form.getFilter();
        Assert.assertNotNull(filter);

        Criteria criteria = filter.getCriteria();
        Assert.assertEquals(Criteria.CriteriaType.AND, criteria.getType());
        Assert.assertEquals(3, criteria.getChildren().length);
    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
