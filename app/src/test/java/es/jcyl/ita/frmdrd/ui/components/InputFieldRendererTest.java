package es.jcyl.ita.frmdrd.ui.components;
/*
 * Copyright 2020 José Ramón Cuevas (joseramon.cuevas@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Set;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.validation.RequiredValidator;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

import static org.mockito.Mockito.mock;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class InputFieldRendererTest {

    ViewRenderHelper renderHelper = new ViewRenderHelper();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.Theme_App_Light);
    }

    @Test
    public void fieldLabelRendererTest() {
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        UIField field = new UIField();
        field.setId(RandomUtils.randomString(4));
        field.setLabel("some text");
        View view = renderHelper.render(env, field);

        Assert.assertEquals("The label is not correct.",
                "some text",
                ((TextView)ViewHelper.findLabelView(view, field)).getText());

        RequiredValidator mockRequired = mock(RequiredValidator.class);
        field.addValidator(mockRequired);
        view = renderHelper.render(env, field);

        Assert.assertEquals("The label must be marked with asterisk.",
                "* some text",
                ((TextView)ViewHelper.findLabelView(view, field)).getText());
    }

    @Test
    public void clearFieldTest(){
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        UIField field = new UIField();
        field.setId(RandomUtils.randomString(4));
        InputFieldView inputFieldView = (InputFieldView) renderHelper.render(env, field);
        Set<ImageView> viewSet = ViewHelper.findViewsContainingText(inputFieldView,
                "Cancel", ImageView.class);
        ImageView eraseImage = viewSet.isEmpty()? null : viewSet.iterator().next();
        inputFieldView.setValueString("filling");

        Assert.assertNotNull("Erase image missing.", eraseImage);
        Assert.assertEquals("Incorrectly filled.",
                "filling", inputFieldView.getValueString());

        eraseImage.performClick();
        Assert.assertEquals("The field has not been cleaned.",
                "", inputFieldView.getValueString());
    }
}
