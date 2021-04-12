package es.jcyl.ita.formic.forms.components;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Set;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.validation.RequiredValidator;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.mock;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class InputFieldRendererTest {

    ViewRenderer renderHelper = new ViewRenderer();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);
    }

    @Test
    public void fieldLabelRendererTest() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        UIField field = new UIField();
        field.setId(RandomUtils.randomString(4));
        field.setLabel("some text");
        View view = renderHelper.render(env, field);

        Assert.assertEquals("The label is not correct.",
                "some text",
                ViewHelper.getLabelValue(view, field));

        RequiredValidator mockRequired = mock(RequiredValidator.class);
        field.addValidator(mockRequired);
        view = renderHelper.render(env, field);

        Assert.assertEquals("The label must be marked with asterisk.",
                "some text *",
                ViewHelper.getLabelValue(view, field));
    }

    @Test
    @Ignore("Erase button is no needed anymore")
    public void clearFieldTest() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        UIField field = new UIField();
        field.setId(RandomUtils.randomString(4));
        InputWidget inputFieldView = (InputWidget) renderHelper.render(env, field);
        Set<ImageView> viewSet = ViewHelper.findViewsContainingText(inputFieldView,
                "Cancel", ImageView.class);
        ImageView eraseImage = viewSet.isEmpty() ? null : viewSet.iterator().next();
        inputFieldView.setValue("filling");

        Assert.assertNotNull("Erase image missing.", eraseImage);
        Assert.assertEquals("Incorrectly filled.",
                "filling", inputFieldView.getValue());

        eraseImage.performClick();
        Assert.assertEquals("The field has not been cleaned.",
                "", inputFieldView.getValue());
    }
}
