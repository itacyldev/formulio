package es.jcyl.ita.formic.forms.config.builders.ui;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
public class UIButtonBarBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_BOTTOMBAR = "<buttonbar type=\"bottom\"><button id=\"btn1\" action=\"save\"/></buttonbar>";
    @Test
    public void testBottonNav() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_BOTTOMBAR);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        UIView view = editCtl.getView();

        UIButtonBar bottomNav = view.getBottomNav();
        Assert.assertNotNull(bottomNav);
        Assert.assertNotNull(bottomNav.getChildById("btn1"));
    }

    private static final String XML_DEFAULT = "<buttonbar type=\"default\">" +
            "<button id=\"btn1\" action=\"save\"/>" +
            "</buttonbar>";
    @Test
    public void testDefaultButtonbar() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_DEFAULT);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        UIView view = editCtl.getView();

        UIButtonBar bottomNav = view.getBottomNav();
        // a default bottombar has been created
        Assert.assertNotNull(bottomNav);

        // there's a buttonbar nested to the uiview component
        List<UIButtonBar> buttonBars = UIComponentHelper.getChildrenByClass(view, UIButtonBar.class);
        Assert.assertThat(buttonBars, not(empty()));
        Assert.assertNotNull(buttonBars.get(0).getChildById("btn1"));
    }



    private static final String XML_FAB_LIST_CTRL = "<datatable/><buttonbar type=\"fab\"><button id=\"btn1\" action=\"save\"/></buttonbar>";
    @Test
    public void testListControllerFabBar() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_FAB_LIST_CTRL);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormListController listCtl = formConfig.getList();
        UIView view = listCtl.getView();

        UIButtonBar fabBar = view.getFabBar();
        // a default bottombar has been created
        Assert.assertNotNull(fabBar);
    }
}
