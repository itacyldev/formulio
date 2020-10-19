package es.jcyl.ita.formic.forms.validator;
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

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.core.context.FormContextHelper;
import es.jcyl.ita.formic.forms.context.impl.FormViewContext;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.validation.CommonsValidatorWrapper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class EmailValidatorTest {

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Use viewContext to set a value in the component to simulate userInput and run validation
     * on the component.
     * A message related to the component id must be set in the FormContext
     */
    @Test
    public void testEmptyValue() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new CommonsValidatorWrapper(EmailValidator.getInstance()));

        // get the view context to access data
        FormViewContext formViewContext = recipe.form.getContext().getViewContext();
        formViewContext.put(recipe.field.getId(), "");

        // execute validation
        ((FormEditController) recipe.mc.getFormController()).validate(recipe.field);

        // assert there's a message in the context for this field
        Assert.assertNull(FormContextHelper.getMessage(recipe.form.getContext(), recipe.field.getId()));
    }

    @Test
    public void testValidEmail() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new CommonsValidatorWrapper(EmailValidator.getInstance()));

        // get the view context to access data
        FormViewContext formViewContext = recipe.form.getContext().getViewContext();
        formViewContext.put(recipe.field.getId(), "myemil@subdomain.domain.org");

        // execute validation
        ((FormEditController) recipe.mc.getFormController()).validate(recipe.field);

        // assert there's a message in the context for this field
        Assert.assertNull(FormContextHelper.getMessage(recipe.form.getContext(), recipe.field.getId()));
    }


    @Test
    public void testInvalidEmail() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new CommonsValidatorWrapper(EmailValidator.getInstance()));

        // get the view context to access data
        FormViewContext formViewContext = recipe.form.getContext().getViewContext();
        formViewContext.put(recipe.field.getId(), "myemil-subdomain.domain.org");

        // execute validation
        ((FormEditController) recipe.mc.getFormController()).validate(recipe.field);

        // assert there's a message in the context for this field
        Assert.assertNotNull(FormContextHelper.getMessage(recipe.form.getContext(), recipe.field.getId()));
    }
}
