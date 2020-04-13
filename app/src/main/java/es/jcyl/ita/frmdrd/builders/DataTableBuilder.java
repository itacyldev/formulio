package es.jcyl.ita.frmdrd.builders;
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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.configuration.ConfigurationException;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DataTableBuilder {
    private ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();


    public UIDatatable createDataTableFromRepo(Repository repo) {
        // select all entity properties
        EntityMeta meta = repo.getMeta();
        String[] fieldFilter = meta.getPropertyNames();
        return createDataTableFromRepo(repo, fieldFilter);
    }

    /**
     * Creates a datatable using repo meta using just the given fields
     *
     * @param repo
     * @param fields
     * @return
     */
    public UIDatatable createDataTableFromRepo(Repository repo, String[] fields) {
        UIDatatable datatable = new UIDatatable();
        datatable.setRepo(repo);
        EntityMeta meta = repo.getMeta();
        List<UIColumn> lstCols = new ArrayList<UIColumn>();

        for (String propName : fields) {
            PropertyType p = meta.getPropertyByName(propName);
            if (p == null) {
                throw new ConfigurationException(String.format("No property found with name [%s] in " +
                        "EntityMeta from repository [%s]", propName, repo.getId()));
            }
            // create columns
            UIColumn col = new UIColumn();
            col.setId(p.getName());
            col.setHeaderText(p.getName());
            col.setValueExpression(expressionFactory.create("${entity." + p.getName() + "}"));
            lstCols.add(col);
        }
        datatable.setColumns(lstCols.toArray(new UIColumn[lstCols.size()]));

        return datatable;
    }
}
