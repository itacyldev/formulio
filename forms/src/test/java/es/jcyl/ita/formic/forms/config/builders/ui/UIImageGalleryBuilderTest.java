package es.jcyl.ita.formic.forms.config.builders.ui;
/*
 *
 *  * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.image.UIImageGallery;
import es.jcyl.ita.formic.forms.components.image.UIImageGalleryItem;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIImageGalleryBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<imagegallery/>";

    @Test
    public void testBasicImageGallery() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIImageGallery> imageGalleries = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIImageGallery.class);
        Assert.assertNotNull(imageGalleries);

        // repo must be set with parent value "contacts"
        UIImageGallery imageGallery = imageGalleries.get(0);
        Assert.assertNotNull(imageGallery.getId());
        Assert.assertEquals(formConfig.getRepo(), imageGallery.getRepo());
    }

    private static final String XML_TEST_REPO = "<imagegallery id=\"myimagegallery\" repo=\"pictures\"/>";

    @Test
    public void tesDatalistAttributes() throws Exception {
        RepositoryUtils.registerMock("pictures");

        String xml = XmlConfigUtils.createMainList(XML_TEST_REPO);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIImageGallery> imageGalleries = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIImageGallery.class);
        Assert.assertNotNull(imageGalleries);

        // repo must be set with parent value "contacts"
        UIImageGallery imageGallery = (UIImageGallery) imageGalleries.get(0);

        Assert.assertEquals("myimagegallery", imageGallery.getId());
        Assert.assertEquals(RepositoryUtils.getRepo("pictures"), imageGallery.getRepo());
    }

    private static final String XML_TEST_WITH_TITEM = "<imagegallery  id=\"myimagegallery\" >"
            + "  <imagegalleryitem id=\"my_imagegalleryitem\">"
            + "  </imagegalleryitem>"
            + "</imagegallery>";

    @Test
    public void testDatalistitem() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_WITH_TITEM);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIImageGallery> imageGalleries = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIImageGallery.class);
        Assert.assertNotNull(imageGalleries);

        UIImageGallery imageGallery = imageGalleries.get(0);
        Assert.assertNotNull(imageGallery.getId());

        UIComponent[] children = imageGallery.getChildren();
        Assert.assertEquals(1, children.length);
        UIImageGalleryItem item = (UIImageGalleryItem) children[0];
        Assert.assertEquals("my_imagegalleryitem", item.getId());
    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("pictures");
        RepositoryUtils.unregisterMock("pictures2");
    }

}
