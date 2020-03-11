package es.jcyl.ita.frmdrd.render;

import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.db.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.DaoSession;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.spatialite.SpatialiteDataBase;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.util.DataUtils;
import jsqlite.Constants;
import jsqlite.Database;

public class ListEntityDialog extends Dialog {
    protected static final Log LOGGER = LogFactory
            .getLog(ListEntityDialog.class);

    private final Context context;
    private final ListView listView;
    private final LinearLayout headersLayout;
    private ListEntityAdapter dataAdapter;

    private final List<View> headers = new ArrayList<View>();
    private List<Entity> entities;

    private EntityMeta meta;
    private DaoMaster daoMaster;
    private EntityDao dao;
    private SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();


    private static final int FIELD_LIMIT = 150;

    public ListEntityDialog(final Context context, String formId) {
        super(new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog));

        this.setContentView(R.layout.list_field_layout);
        this.setCanceledOnTouchOutside(false);

        initDatabase();
        this.context = context;

        setTitle("entidades");

        listView = findViewById(R.id.list_view);
        headersLayout = (LinearLayout) findViewById(R.id.list_layout_headers);

        loadEntities();
        fillHeader();

        setListViewAdapter();

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view,
                                             final int scrollState) {
                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count - threshold) {
                        // loadNextPage();
                        listView.invalidateViews();
                    }
                }
            }

            @Override
            public void onScroll(final AbsListView view,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
            }
        });

    }

    private void setListViewAdapter() {
        dataAdapter = new ListEntityAdapter(this.getContext(),
                R.layout.list_item, entities, FIELD_LIMIT);
        listView.setAdapter(dataAdapter);
    }

    private void loadEntities() {
        this.entities = this.dao.loadAll();
    }

    private View createHeaderView(final ViewGroup parent,
                                  final String columnName) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        output = inflater.inflate(R.layout.list_item_header, parent,
                false);

        final LinearLayout headerNameLayout = (LinearLayout) output
                .findViewById(R.id.list_header_name_layout);
        final TextView fieldName = (TextView) output
                .findViewById(R.id.list_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));
        return output;
    }

    private void fillHeader() {
        headersLayout.removeAllViews();

        for (PropertyType p : this.meta.getProperties()) {
            final View dataHeader = createHeaderView(
                    headersLayout, p.getName());
            headersLayout.addView(dataHeader);
            headers.add(dataHeader);

        }
        headersLayout.setVisibility(View.VISIBLE);
    }


//    private void fillHeader(final Entity entity) {
//        if (entity == null || headersLayout.getChildCount() > 0) {
//            return;
//        }
//        headersLayout.removeAllViews();
//        headers.clear();
//
//        final String[] columns = entity.getMetadata().getColumnNames();
//        if (columns != null) {
//
//            for (final String column : columns) {
//                if (column != null) {
//                    final View dataHeader = createHeaderView(
//                            headersLayout, column);
//                    headersLayout.addView(dataHeader);
//                    headers.add(dataHeader);
//                }
//            }
//
//            headersLayout.setVisibility(View.VISIBLE);
//        }
//    }


    private void initDatabase() {
//        DaoMaster.OpenHelper helper = new BasicOpenHelper(context, "notes-db");
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);

        File dbFile = new File("/sdcard/test/ribera.sqlite");

        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new Database());
        db.open(dbFile.getAbsolutePath(), Constants.SQLITE_OPEN_READWRITE);

        this.daoMaster = new DaoMaster(db);

        SQLiteMetaModeler modeler = new SQLiteMetaModeler();
        DBTableEntitySource source = new DBTableEntitySource(db, "TESTDATA");

        this.meta = modeler.readFromSource(source);
        String entityType = meta.getName();

        // create dao configuration
        Map<String, SQLitePropertyConverter> converters = createDefaultConverters(meta);
        Map<String, String> mappers = createDefaultMapper(meta);
        EntityDaoConfig daoConfig = new EntityDaoConfig(meta, source, converters, mappers);
        daoMaster.register(entityType, daoConfig);
//        daoMaster.createAllTables(true);

        DaoSession daoSession = daoMaster.newSession();
        this.dao = daoSession.getEntityDao(entityType);

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
