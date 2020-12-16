package es.jcyl.ita.formic.forms.config.builders.ui;
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

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.card.UICard;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.media.meta.FileMeta;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UICardBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
        RepositoryUtils.registerMock("DEFAULT_PROJECT_IMAGES", new FileMeta());
    }


    private static final String XML_TEST_BASIC = "<card/>";

    @Test
    public void testBasicCard() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UICard> cards = UIComponentHelper.findByClass(formConfig.getList().getView(), UICard.class);
        Assert.assertNotNull(cards);
        UICard card = cards.get(0);
        Assert.assertNotNull(card.getId());

    }

    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.*
     *
     * @throws Exception
     */
    private static final String XML_TEST_CARD_WITH_CHILDREN = "<card template=\"template\">" +
            "<head name=\"title\" value=\"value_title\"/>" +
            "<head name=\"subtitle\" value=\"value_subtitle\"/>" +
            "<paragraph name=\"description\" value=\"value_description\"/>" +
            "<image name=\"image\" id=\"card_image\" value=\"$entity.image\"/>" +
            "</card>";


    @Test
    public void testCardWithChildren() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_CARD_WITH_CHILDREN);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UICard> cards = UIComponentHelper.findByClass(formConfig.getList().getView(), UICard.class);
        Assert.assertNotNull(cards);

        UICard card = cards.get(0);

        UIHeading title = card.getTitle();
        Assert.assertNotNull(title);

        UIHeading subtitle = card.getSubtitle();
        Assert.assertNotNull(subtitle);

        UIImage image = (UIImage) card.getChildren()[0];
        Assert.assertNotNull(image);
        Assert.assertEquals("card_image", image.getId());
    }

    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.*
     *
     * @throws Exception
     */
    private static final String XML_TEST_CARD_WITH_ATTS = "<card template=\"template\" title=\"titleValue\" subtitle=\"subtitleValue \" image=\"$entity.image\" />";


    @Test
    public void testCardWithAtts() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_CARD_WITH_ATTS);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UICard> cards = UIComponentHelper.findByClass(formConfig.getList().getView(), UICard.class);
        Assert.assertNotNull(cards);

        UICard card = cards.get(0);

        UIHeading title = card.getTitle();
        Assert.assertNotNull(title);

        UIHeading subtitle = card.getSubtitle();
        Assert.assertNotNull(subtitle);

        UIImage image = (UIImage) card.getChildren()[0];
        Assert.assertNotNull(image);
    }


    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.*
     *
     * @throws Exception
     */
    private static final String XML_TEST_CARD_WITH_HEADER = "<card label=\"card_label\" expandable=\"true\" expanded=\"true\" />";


    @Test
    public void testCardWithHeader() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_CARD_WITH_HEADER);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UICard> cards = UIComponentHelper.findByClass(formConfig.getList().getView(), UICard.class);
        Assert.assertNotNull(cards);

        UICard card = cards.get(0);

        Assert.assertEquals("card_label", card.getLabel());
        Assert.assertTrue(card.isExpandable());
        Assert.assertTrue(card.isExpanded());
    }

    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.*
     *
     * @throws Exception
     */
    private static final String XML_TEST_CARD_IMAGE_POSITION = "<card label=\"card_label\" imagePosition=\"right\" />";


    @Test
    public void testCardImagePosition() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_CARD_IMAGE_POSITION);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UICard> cards = UIComponentHelper.findByClass(formConfig.getList().getView(), UICard.class);
        Assert.assertNotNull(cards);

        UICard card = cards.get(0);

        String imagePosition = card.getImagePosition();
        Assert.assertEquals(UICard.ImagePosition.RIGHT.getPosition(), imagePosition);
    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
        RepositoryUtils.unregisterMock("contacts2");
    }

}
