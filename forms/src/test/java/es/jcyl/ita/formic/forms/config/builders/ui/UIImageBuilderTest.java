package es.jcyl.ita.formic.forms.config.builders.ui;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.media.meta.FileMeta;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIImageBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
        RepositoryUtils.registerMock("DEFAULT_PROJECT_IMAGES", new FileMeta());
    }

    /**
     * Create empty form and check its created and repo attribute is set
     *
     * @throws Exception
     */
    private static final String XML_TEST_BASIC = "<image value=\"$$$\"/>";

    @Test
    public void testStaticImageConfig() throws Exception {

        File imgFile = RandomUtils.createRandomImageFile();

        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC.replace("$$$", imgFile.getAbsolutePath()));
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIImage> imgs = UIComponentHelper.findByClass(editCtl.getView(), UIImage.class);
        Assert.assertNotNull(imgs);
        Assert.assertTrue("One image is expected, found: " + imgs.size(), imgs.size() == 1);

        // repo must be set with parent value "contacts"
        UIImage img = imgs.get(0);
        Assert.assertNotNull(img.getId());
        Assert.assertNotNull(img.getEntityRelation());
        // an entity relation with a literal expression must be created
        Object value =  img.getEntityRelation().getEntityPropertyExpr().toString();
        Assert.assertEquals(imgFile.getAbsolutePath(), value);
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}