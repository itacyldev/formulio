package es.jcyl.ita.frmdrd.context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access (get/set) to Android view elements through AndViewContext
 */
@RunWith(RobolectricTestRunner.class)
public class AndViewContextTest {

    ViewValueConverterFactory converterFactory = ViewValueConverterFactory.getInstance();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme( R.style.Theme_App_Light);
    }

    /**
     * Get ui android element value using FormViewContext
     */
    @Test
    public void testSetValueViewElements() {
        // register elements as alias
        View baseView = LayoutInflater.from(ctx).inflate(R.layout.component_textfield, null);
        AndViewContext viewContext = new AndViewContext(baseView);
        viewContext.registerViewElement("label", R.id.label_view, converterFactory.get("text"));
        viewContext.registerViewElement("input", R.id.input_view, converterFactory.get("text"));

        viewContext.put("label", "valueLabel");
        viewContext.put("input", "valueInput");

        // get element from view and check Values
        TextView txtView = baseView.findViewById(R.id.label_view);
        Assert.assertEquals("valueLabel", txtView.getText().toString());
        txtView = baseView.findViewById(R.id.input_view);
        Assert.assertEquals("valueInput", txtView.getText().toString());
    }

    /**
     * Get ui android element value using FormViewContext
     */
    @Test
    public void testGetValueViewElements() {
        // register elements as alias
        View baseView = LayoutInflater.from(ctx).inflate(R.layout.component_textfield, null);
        AndViewContext viewContext = new AndViewContext(baseView);
        viewContext.registerViewElement("label", R.id.label_view, converterFactory.get("text"));
        viewContext.registerViewElement("input", R.id.input_view, converterFactory.get("text"));

        // set values directly to the view
        TextView txtView = baseView.findViewById(R.id.label_view);
        txtView.setText("labelValue");
        txtView = baseView.findViewById(R.id.input_view);
        txtView.setText("inputValue");


        viewContext.put("label", "valueLabel");
        viewContext.put("input", "valueInput");

        // get element from view and check Values
        Assert.assertEquals("valueLabel", viewContext.get("label"));
        Assert.assertEquals("valueInput", viewContext.get("input"));
    }

    @Test
    public void testAccessThroughCompositeContext() {
        // register elements as alias
        View baseView = LayoutInflater.from(ctx).inflate(R.layout.component_textfield, null);
        AndViewContext viewContext = new AndViewContext(baseView);

        viewContext.registerViewElement("label", R.id.label_view, converterFactory.get("text"));
        viewContext.registerViewElement("input", R.id.input_view, converterFactory.get("text"));
        viewContext.setPrefix("this");
        // set values directly to the view
        TextView txtView = baseView.findViewById(R.id.label_view);
        txtView.setText("labelValue");
        txtView = baseView.findViewById(R.id.input_view);
        txtView.setText("inputValue");

        CompositeContext gContext = new UnPrefixedCompositeContext();
        gContext.addContext(viewContext);

        // try to access using "this" prefix
        Assert.assertEquals("labelValue", gContext.get("this.label"));
        Assert.assertEquals("inputValue", gContext.get("this.input"));

    }



    @Test
    public void testShowContent(){
        // register elements as alias
        View baseView = LayoutInflater.from(ctx).inflate(R.layout.component_textfield, null);
        AndViewContext viewContext = new AndViewContext(baseView);
        System.out.println(viewContext.entrySet());
        System.out.println(viewContext.values());

    }
}
