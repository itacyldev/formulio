package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.greendao.database.StandardDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.db.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.impl.DynamicListContext;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.util.DataUtils;

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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class TableFieldRenderer extends AbstractFieldRenderer {

    private static final int FIELD_LIMIT = 50;

    private List<Entity> entities = new ArrayList<>();
    private EntityMeta meta;
    private DaoMaster daoMaster;
    private EntityDao dao;
    private SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    private int offset = 0;
    private int pageSize = 20;
    private DynamicListContext listContext;

    public TableFieldRenderer(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        try {
            initDatabase();
        } catch (Exception e) {
            Log.e("repo", "Error trying to open database.", e);
        }

    }


    @Override
    public View render(UIField field) {
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition, field.getId());
        }

        LinearLayout linearLayout = (LinearLayout) View.inflate(context,
                R.layout.list_field_layout, null);

        final TextView fieldLabel = linearLayout
                .findViewById(R.id.field_layout_name);
        fieldLabel.setText(field.getLabel());
        fieldLabel.setTag("label");

        LinearLayout tableView = linearLayout.findViewById(R.id.list_layout);

        ListView listView = tableView.findViewById(R.id.list_view);
        LinearLayout headersLayout = tableView.findViewById(R.id.list_layout_headers);

        fillHeader(headersLayout);
        loadEntities();

        ListEntityAdapter dataAdapter = new ListEntityAdapter(this.context,
                R.layout.list_item, entities, FIELD_LIMIT);
        listView.setAdapter(dataAdapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view,
                                             final int scrollState) {
                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count - threshold) {
                        loadNextPage();
                    }
                }
            }

            @Override
            public void onScroll(final AbsListView view,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
            }
        });

        return linearLayout;
    }

    private void loadEntities() {
        loadNextPage();
    }

    private View createHeaderView(final ViewGroup parent,
                                  final String columnName) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(this.context);
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);
        final TextView fieldName = output
                .findViewById(R.id.list_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));
        return output;
    }

    private void fillHeader(LinearLayout headersLayout) {
        headersLayout.removeAllViews();

        for (PropertyType p : this.meta.getProperties()) {
            final View dataHeader = createHeaderView(
                    headersLayout, p.getName());
            headersLayout.addView(dataHeader);
        }
        headersLayout.setVisibility(View.VISIBLE);
    }

    private void initDatabase() {
        // ESTA PARTE TIENE QUE QUEDAR OCULTA POR LA CONFIGURACIÓN
        RepositoryFactory repoFactory = RepositoryFactory.getInstance();

        String entityType = "inspecciones";
        File dbFile = new File("/sdcard/test/ribera.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        DBTableEntitySource source = new DBTableEntitySource(new StandardDatabase(db), entityType);
        // obtain repository builder
        RepositoryBuilder builder = repoFactory.getBuilder(entityType, source);

        // read meta information from source
        SQLiteMetaModeler modeler = new SQLiteMetaModeler();
        EntityMeta meta = modeler.readFromSource(source);

        // create repository using repo builder using entityDaoConfig
        EntityDaoConfig conf = new EntityDaoConfig(meta, source, createDefaultConverters(meta),
                createDefaultMapper(meta));
        builder.setProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.register();

        // ESTO ES LO QUE TIENE QUE QUEDAR AL INTENTAR ACCEDER A UN REPOSITORIO DESDE PRESENTACIÓN
        Repository repo = repoFactory.getRepo(entityType);

        // ESto tiene que quedar oculto por la configuración de los contextos
        this.listContext = new DynamicListContext(repo);
    }

    private void loadNextPage() {
        listContext.getFilter().setOffset(this.offset);
        listContext.getFilter().setPageSize(this.pageSize);
        this.entities = this.listContext.getList();
        this.offset += this.pageSize;
    }

    private Map<String, String> createDefaultMapper(EntityMeta meta) {
        Map<String, String> mapper = new HashMap<String, String>();
        for (PropertyType p : meta.getProperties()) {
            mapper.put(p.getName(), p.getName()); // Property name as columnName
        }
        return mapper;
    }

    private Map<String, SQLitePropertyConverter> createDefaultConverters(EntityMeta meta) {
        Map<String, SQLitePropertyConverter> converters = new HashMap<String, SQLitePropertyConverter>();
        SQLitePropertyConverter conv;
        for (PropertyType p : meta.getProperties()) {
            conv = convFactory.getDefaultConverter(p.getType());
            converters.put(p.getName(), conv);
        }
        return converters;
    }


}
