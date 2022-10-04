package es.jcyl.ita.formic.forms.controllers;
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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.components.form.FormWidget;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.FormValidator;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests checking the integration of Mozilla Rhino to executing form validation functions.
 */
@RunWith(RobolectricTestRunner.class)
public class FormScriptingTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;

    private static Context ctx;

    @Before
    public void setUp() {
        if (ctx == null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
            ctx.setTheme(R.style.FormudruidLight);
        }

        App.init(ctx, "");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    /**
     * Test form validation using a js script. It generates a form from an entityMeta, creates
     * a random meta, and modifies the field value using the ViewContext.
     * The js function set in the onValidate attribute checks the value of field f1 to make sure
     * it is longer that 10.
     * Two cases are tested, value less and greater that the limit value 10.
     */
    @Test
    public void testValidationScriptOk() throws Exception {
        // create random entity meta and use databuilder to populate entity data
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        // entity meta with 1 prop for pk and a second string property
        EntityMeta meta = metaBuilder.withNumProps(1).addProperties(new Class[]{String.class}).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // create form using entity meta to define UIFields
        UIForm form = formBuilder.withMeta(meta).withRandomData().build();
        form.setOnValidate("validateForm");
        // get one of the fields and set a name we'll use in the validation script
        form.getChildren()[1].setId("f1"); // the string field for the string property

        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);

        // prepare data/state and render the view
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx)
                .withForm(form)
                .withParam("entityId", entity.getId())
                .withRepo(mockRepo)
                .load()
                .render();

        // load script: the value si valid if length > 10
        String source = TestUtils.readAsString(TestUtils.findFile("scripts/formValidation1.js"));

        ScriptEngine engine = recipe.mc.getScriptEngine();
        engine.store(recipe.mc.getViewController().getId(), source);
        engine.initEngine(null);
        engine.initScope(recipe.mc.getViewController().getId());
        engine.putProperty("out", System.out);

        FormValidator formValidator = new FormValidator(recipe.env);
        InputWidget inputWidget = (InputWidget) ViewHelper.findComponentWidget(recipe.viewWidget, "f1");

        // set field f1 to a value > 10
        ViewContext viewContext = recipe.env.getWidgetContext().getViewContext();
        viewContext.put("f1", "12345678910111213");
        // call save method to
        FormWidget formWidget = (FormWidget) inputWidget.getWidgetContext().getHolder();
        boolean valid = formValidator.validate(formWidget);
        Assert.assertTrue(valid);

        // set a field shorter than 10, the validation has to throw an exception with message
        viewContext.put("f1", "12345");
        valid = formValidator.validate(formWidget);

        Assert.assertFalse(valid);
        String errorMessage = MessageHelper.getMessage(recipe.env.getWidgetContext(), form.getId());
        Assert.assertTrue(StringUtils.isNotBlank(errorMessage));

        // Try validating the full form
//        FormWidget formWidget = (FormWidget) ViewHelper.findComponentWidget(recipe.viewWidget, form.getId());
        valid = formValidator.validate(formWidget);
        // check the message has been set from the validation function
        Assert.assertFalse(valid);
        errorMessage = MessageHelper.getMessage(recipe.env.getWidgetContext(), form.getId());
        Assert.assertTrue(StringUtils.isNotBlank(errorMessage));
    }
}
