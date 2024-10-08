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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.controllers.operations.FormValidator;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.validation.NumberValidator;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class NumberValidatorTest {

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
    public void testInvalidInteger() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Integer.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1.5");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertFalse(valid);

        // assert there's a message in the context for this field
        Assert.assertNotNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testValidInteger() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Integer.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertTrue(valid);

        // assert there's a message in the context for this field
        Assert.assertNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testInvalidShort() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Short.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "100000");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertFalse(valid);

        // assert there's a message in the context for this field
        Assert.assertNotNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testValidShort() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Short.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertTrue(valid);

        // assert there's a message in the context for this field
        Assert.assertNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testInvalidLong() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Long.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1.5");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertFalse(valid);

        // assert there's a message in the context for this field
        Assert.assertNotNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testValidLong() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Long.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertTrue(valid);

        // assert there's a message in the context for this field
        Assert.assertNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testInvalidDouble() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Double.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "a");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertFalse(valid);

        // assert there's a message in the context for this field
        Assert.assertNotNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testValidDouble() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Double.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1.5");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertTrue(valid);

        // assert there's a message in the context for this field
        Assert.assertNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testInvalidFloat() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Float.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "a");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertFalse(valid);

        // assert there's a message in the context for this field
        Assert.assertNotNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }

    @Test
    public void testValidFloat() {
        // use a recipe to create objects and preset form context
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        // set InputRequiredValidator
        recipe.field.addValidator(new NumberValidator(Float.class));

        // get the view context to access data
        WidgetContext widgetContext = recipe.env.getWidgetContext();
        ViewContext viewContext = widgetContext.getViewContext();
        viewContext.put(recipe.field.getId(), "1.5");

        // execute validation
        Widget fieldWidget = ViewHelper.findComponentWidget(recipe.viewWidget, recipe.field);
        FormValidator validator = new FormValidator(recipe.env);
        boolean valid = validator.validate((InputWidget) fieldWidget);

        Assert.assertTrue(valid);

        // assert there's a message in the context for this field
        Assert.assertNull(MessageHelper.getMessage(widgetContext, recipe.field.getId()));
    }
}
