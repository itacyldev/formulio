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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.persister.EntityMeta;
import es.jcyl.ita.frmdrd.util.DataUtils;

public class AsListLayerDialog extends Dialog {
    protected static final Log LOGGER = LogFactory
            .getLog(AsListLayerDialog.class);

    private final Context context;
    private final ListView listView;
    private final LinearLayout headersLayout;
    //private final TextView loadingView;
    private AsListLayerAdapter dataAdapter;

    private final List<View> headers = new ArrayList<View>();
    private List<Entity> entities;

    private static final int FIELD_LIMIT = 150;

    public AsListLayerDialog(final Context context, String formId) {
        super(new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog));

        this.setContentView(R.layout.aslist_dialog);
        this.setCanceledOnTouchOutside(false);

        this.context = context;

        setTitle("entidades");

        listView = (ListView) findViewById(R.id.list);
        headersLayout = (LinearLayout) findViewById(R.id.aslist_layout_headers);
        //loadingView = (TextView) findViewById(R.id.aslist_layout_loading);

        loadEntities();

        fillHeader(entities.get(0));

        setListViewAdapter();

        dataAdapter.notifyDataSetChanged();

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
        dataAdapter = new AsListLayerAdapter(this.getContext(),
                R.layout.aslist_list_item, entities, this, FIELD_LIMIT);
        listView.setAdapter(dataAdapter);
    }

    private void loadEntities() {
        entities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Entity newEntity = newEntity("entidad_" + i);
            entities.add(newEntity);
        }
    }

    private Entity newEntity(String name) {
        Entity entity = new Entity();
        entity.setId(name);

        EntityMeta metadata = new EntityMeta();
        metadata.setColumnNames(new String[]{"campo1", "campo2", "campo3",
                "campo4"});
        metadata.setColumnTypes(new String[]{"TEXT", "BOOLEAN", "TEXT", "DATE"});
        metadata.setIdColumns(new String[]{"campo1", "campo2", "campo3",
                "campo4"});
        entity.setMetadata(metadata);

        Map<String, Object> properties = new HashMap<>();
        properties.put("campo1", RandomStringUtils.randomAlphanumeric(8));
        properties.put("campo2", Boolean.TRUE);

        properties.put("campo3", RandomStringUtils.randomAlphanumeric(8));
        properties.put("campo4", new Date());

        entity.setProperties(properties);


        return entity;
    }

    private View createHeaderView(final ViewGroup parent,
                                  final String columnName) {
        View output = null;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        output = inflater.inflate(R.layout.aslist_list_item_header, parent,
                false);

        final LinearLayout headerNameLayout = (LinearLayout) output
                .findViewById(R.id.aslistitem_header_name_layout);
        final TextView fieldName = (TextView) output
                .findViewById(R.id.aslistitem_header_textview);

        fieldName.setText(DataUtils.nullFormat(columnName));
        return output;
    }

    private void fillHeader(final Entity entity) {
        if (entity == null || headersLayout.getChildCount() > 0) {
            return;
        }
        headersLayout.removeAllViews();
        headers.clear();

        final String[] columns = entity.getMetadata().getColumnNames();
        if (columns != null) {

            for (final String column : columns) {
                if (column != null) {
                    final View dataHeader = createHeaderView(
                            headersLayout, column);
                    headersLayout.addView(dataHeader);
                    headers.add(dataHeader);
                }
            }

            headersLayout.setVisibility(View.VISIBLE);
        }
    }
}
