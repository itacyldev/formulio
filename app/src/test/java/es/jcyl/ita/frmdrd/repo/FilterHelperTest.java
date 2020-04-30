package es.jcyl.ita.frmdrd.repo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;
import es.jcyl.ita.frmdrd.repo.query.CriteriaVisitor;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class FilterHelperTest {

    CriteriaVisitor visitor = new CriteriaVisitor();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();


    /**
     * Test mandatory fileds
     */
    @Test
    public void testMandatoryFields() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // define entity Meta with one pk and two additional properties
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        String propertyName1 = "intprop";
        String propertyName2 = "strprop";
        EntityMeta meta = metaBuilder.withNumProps(1).addProperty(propertyName1, Integer.class)
                .addProperty(propertyName2, String.class).build();
        // create data base
        DevDbBuilder builder = new DevDbBuilder();
        builder.withMeta(meta).withNumEntities(100).build(ctx);

        Repository repo = builder.getSQLiteRepository();
        Filter effFilter = FilterHelper.createInstance(repo);
        Filter filterDef = FilterHelper.createInstance(repo);

        BasicContext bCtx = new BasicContext("test");
        bCtx.set("a", 10);
        bCtx.set("b", "a");

        Criteria criteria = Criteria.single(ConditionBinding.cond(propertyName1, Operator.EQ, exprFactory.create("${c}", Integer.class)));
        filterDef.setCriteria(criteria);

        String[] mandatoryFilters = new String[]{"a", "b", "c"};
        FilterHelper.evaluateFilter(bCtx, filterDef, effFilter, mandatoryFilters);

        // filter should include one immpossible condition
        Assert.assertEquals(1, effFilter.getCriteria().getChildren().length);
        List list = repo.find(effFilter);
        Assert.assertEquals(0, list.size());



//        mandatoryFilters = new String[]{"a", "b"};

    }
}