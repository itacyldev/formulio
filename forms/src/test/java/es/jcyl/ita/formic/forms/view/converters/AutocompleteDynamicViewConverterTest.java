package es.jcyl.ita.formic.forms.view.converters;
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.AutoCompleteDataBuilder;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Test settting/getting values to/from an AutocompleteView with options retrieved form repository
 */
@RunWith(RobolectricTestRunner.class)
public class AutocompleteDynamicViewConverterTest {
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    AutoCompleteDataBuilder autoBuilder = new AutoCompleteDataBuilder();
    ViewRenderer renderHelper = new ViewRenderer();
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

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
    public void testOptionsFromRepo() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());

        env.setAndroidContext(ctx);
        env.initialize();
        // meta with one pk column and three string properties
        EntityMeta meta = metaBuilder.withNumProps(1)
                .addProperties(new Class[]{String.class, String.class, String.class})
                .build();
        List<Entity> entities = DevDbBuilder.buildEntities(meta, 1);

        // mock the repository to return random entities
        EditableRepository repoMock = mock(EditableRepository.class);
        when(repoMock.find(any())).thenReturn(entities);
        when(repoMock.getMeta()).thenReturn(meta);
        when(repoMock.getFilterClass()).thenReturn(SQLQueryFilter.class);

        UIAutoComplete field = autoBuilder.withRandomData().build();
        field.setRepo(repoMock);
        // force option selection
        field.setForceSelection(true);
        String valueProperty = meta.getIdProperties()[0].name;
        field.setOptionValueProperty(valueProperty);
        String labelFilteringProp = meta.getPropertyNames()[1];
        field.setOptionLabelFilteringProperty(labelFilteringProp);
        field.setOptionLabelExpression(exprFactory.create(String.format("${entity.%s}", labelFilteringProp)));

        InputWidget<UIAutoComplete, AutoCompleteView> widget =
                (InputWidget<UIAutoComplete, AutoCompleteView>) renderHelper.render(env, field);

        // user input values, check
        // set empty values and the third property of one o the entities
        Entity entity = entities.get(0);
        // if forceSelection = true, the expected value for a string that doesn't exists in the options is null
        Object[] values = new Object[]{entity.get(meta.getProperties()[2].name), null, "", "   ", "randomString",};
        Object[] expectedValues = new Object[]{entity.get(meta.getProperties()[2].name), null, null, null, null};
        int i = 0;
        for (Object value : values) {
            AutoCompleteView inputView = widget.getInputView();

            // change mock
            if (i > 0) {
                when(repoMock.find(any())).thenReturn(new ArrayList());
            } else {
                when(repoMock.find(any())).thenReturn(entities);
            }
            inputView.setValue(value);
            Object actualValue = inputView.getValue();

            Assert.assertEquals(expectedValues[i], actualValue);
            i++;
        }
    }

}
