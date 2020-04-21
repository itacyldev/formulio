package es.jcyl.ita.frmdrd.view.converters;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.AutoCompleteDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Test settting/getting values to/from and static option autocomplete
 */
@RunWith(RobolectricTestRunner.class)
public class AutocompleteStaticViewConverterTest {

    AutoCompleteDataBuilder autoBuilder = new AutoCompleteDataBuilder();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Generates a static autocomplete with random String values, and sets/gets the values
     * using a AutoCompleteStaticValueConverter
     */
    @Test
    public void testStringOptionValues() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        String[] options = RandomUtils.randomObjectArray(5, String.class);
        List<String> lstValues = new ArrayList<>();
        // use the options as testing values and add null values.
        lstValues.addAll(Arrays.asList(options));
        lstValues.addAll(Arrays.asList(new String[]{null, "", "   ", " "}));

        UIAutoComplete field = autoBuilder.withRandomData().withOptionValues(options, options).build();
        field.setForceSelection(true);
        InputFieldView<AutoCompleteView> baseView = (InputFieldView<AutoCompleteView>) renderHelper.render(env, field);

        AutoCompleteStaticValueConverter conv = new AutoCompleteStaticValueConverter();
        for (int i = 0; i < lstValues.size(); i++) {
            AutoCompleteView inputView = baseView.getInputView();

            // transform the value using the converter and check the result against the original value
            String expected = lstValues.get(i);
            conv.setViewValue(inputView, expected);
            String actual = conv.getValueFromViewAsString(inputView);
            if (StringUtils.isBlank(expected)) {
                Assert.assertNull(actual); // is value is blank, expected values is null
            } else {
                Assert.assertEquals(expected, actual);
            }
        }
    }
}