package es.jcyl.ita.formic.forms.validator;
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

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.core.context.FormContextHelper;
import es.jcyl.ita.formic.forms.context.impl.FormViewContext;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorFactory;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class RegexValidatorTest {

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Use viewContext to set a value in the component to simulate userInput and run validation
     * on the component.
     * A message related to the component id must be set in the FormContext
     */
    @Test
    public void testVoidPattern() {

        this.thrown.expect(ConfigurationException.class);
        this.thrown.expectMessage("Regex validator must have a pattern");

        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        Map<String, String> params = new HashMap<>();

        Validator regexValidator = ValidatorFactory.getInstance().getRegexValidator(params);
        recipe.field.addValidator(regexValidator);

        // get the view context to access data
        FormViewContext formViewContext = recipe.form.getContext().getViewContext();
        formViewContext.put(recipe.field.getId(), "a");

        // execute validation
        ((FormEditController) recipe.mc.getFormController()).validate(recipe.field);

        // assert there's a message in the context for this field
        Assert.assertNotNull(FormContextHelper.getMessage(recipe.form.getContext(), recipe.field.getId()));
    }

    /**
     * Use viewContext to set a value in the component to simulate userInput and run validation
     * on the component.
     * A message related to the component id must be set in the FormContext
     */
    @Test
    public void testInvalidPattern() {
        String pattern = "[A-Za-z0-9\\]";

        this.thrown.expect(ConfigurationException.class);
        this.thrown.expectMessage("Validator pattern " + pattern + " is not valid");

        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        Map<String, String> params = new HashMap<>();
        params.put("pattern", pattern);

        Validator regexValidator = ValidatorFactory.getInstance().getRegexValidator(params);
    }
}

