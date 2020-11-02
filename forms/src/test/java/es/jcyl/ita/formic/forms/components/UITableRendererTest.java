package es.jcyl.ita.formic.forms.components;
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
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.components.table.UIRow;
import es.jcyl.ita.formic.forms.components.table.UITable;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderHelper;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class UITableRendererTest {

    Context ctx;

    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Test empty Table
     */
    @Test
    public void testEmptyTable() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UITable table = new UITable();
        Widget tableView = (Widget) renderHelper.render(env, table);
        // check there's a TextView element
        Assert.assertNotNull(tableView);
    }

    /**
     * Test table header
     */
    @Test
    public void testTableHeader() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UITable table = new UITable();
        table.setHeaderText("col1,col2,col3");
        Widget tableView = (Widget) renderHelper.render(env, table);
        TableLayout tabLayout = (TableLayout) tableView.getChildAt(0);
        // check there's a TextView element
        Assert.assertNotNull(tableView);
        Assert.assertEquals(tableView.getChildCount(), 1);
        TableRow row = (TableRow) tabLayout.getChildAt(0);
        Assert.assertEquals(row.getChildCount(), 3); // cols1, col2,col3
    }

    /**
     * Test 2x5 table one of the rows with label, the other not
     */
    @Test
    public void testSimpleTable() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UITable table = new UITable();
        UIRow row = new UIRow();
        row.addChild(buildTextFields(5));
        row.setLabel("myLabel1");
        table.addChild(row);
        row = new UIRow();
        row.addChild(buildTextFields(5));
        table.addChild(row);

        Widget tableView = (Widget) renderHelper.render(env, table);
        // check there's a TextView element
        Assert.assertNotNull(tableView);

        TableLayout tabLayout = (TableLayout) tableView.getChildAt(0);
        Assert.assertEquals(2, tabLayout.getChildCount());
        TableRow row1 = (TableRow) tabLayout.getChildAt(0);
        Assert.assertEquals(6, row1.getChildCount()); //  5+1 label
        View label = row1.getChildAt(0);
        Assert.assertTrue(label instanceof TextView);
        Assert.assertTrue(((TextView) label).getText().equals("myLabel1"));

        TableRow row2 = (TableRow) tabLayout.getChildAt(1);
        Assert.assertEquals(5, row2.getChildCount());
    }

    /**
     * 2x4 table with colspan attriute in rows, check layout parameters in children
     */
    @Test
    public void testTableWithColspan() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setViewContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UITable table = new UITable();
        UIRow row = new UIRow();
        row.addChild(buildTextFields(2));
        row.setColspans("2,3");
        table.addChild(row);
        row = new UIRow();
        row.addChild(buildTextFields(5));
        table.addChild(row);

        Widget tableView = (Widget) renderHelper.render(env, table);
        // check there's a TextView element
        Assert.assertNotNull(tableView);

        TableLayout tabLayout = (TableLayout) tableView.getChildAt(0);
        TableRow row1 = (TableRow) tabLayout.getChildAt(0);
        // check colspan of elememts
        int span1 = ((TableRow.LayoutParams) row1.getChildAt(0).getLayoutParams()).span;
        Assert.assertTrue(span1 == 2);
        int span2 = ((TableRow.LayoutParams) row1.getChildAt(1).getLayoutParams()).span;
        Assert.assertTrue(span2 == 3);
    }

    private UIComponent[] buildTextFields(int size) {
        FieldDataBuilder fieldBuilder = new FieldDataBuilder();
        UIComponent[] fiedls = new UIComponent[size];
        for (int i = 0; i < size; i++) {
            fiedls[i] = fieldBuilder.withRandomData().build();
        }
        return fiedls;
    }

}
