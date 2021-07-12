package es.jcyl.ita.formic.forms.config.builders.scripts;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.javascript.Script;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * <Your description here>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ScriptSourceBuilderTest {
    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_INLINE_TEST = "<script>\n<![CDATA[" +
            "function myfunction(a,b){\n" +
            "\treturn (a<b);\n" +
            "}\n" +
            "]]></script>";

    @Test
    public void testInlineScriptSource() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_INLINE_TEST);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);

        // check there's a script related to current form
        Script script = ScriptEngine.getInstance().getScript(editCtl.getId());
        assertNotNull(script);
    }

    private static final String XML_INFILE_TEST = "<script src=\"script1.js\"/>";

    @Test
    public void testInFileScriptSource() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_INFILE_TEST);
        // mock a project with script folder as base form folder
        Project prjMock = mock(Project.class);
        when(prjMock.getFormsFolder()).thenReturn(TestUtils.findFile("scripts").getAbsolutePath());

        FormConfig formConfig = XmlConfigUtils.readFormConfig(prjMock, xml);
        ViewController editCtl = formConfig.getEdits().get(0);

        // check there's a script related to current form
        Script script = ScriptEngine.getInstance().getScript(editCtl.getId());
        assertNotNull(script);
    }
}
