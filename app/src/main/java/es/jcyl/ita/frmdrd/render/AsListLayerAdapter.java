package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.NavigationManager;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.UserFormAlphaEditActivity;
import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.util.DataUtils;

/**
 * Created by cuedieje on 10/06/2019.
 */

class AsListLayerAdapter extends ArrayAdapter<Entity> {
    protected static final Log LOGGER = LogFactory
            .getLog(AsListLayerAdapter.class);

    private Context context;

    private final AsListLayerDialog asListLayerDialog;
    private LayoutInflater inflater;
    private View[] cacheViews;

    private final int FIELD_LIMIT;
    private static final int RECORDS_CACHE = 20;

    public AsListLayerAdapter(final Context context,
                              final int textViewResourceId, final
                              List<Entity> entities, AsListLayerDialog asListLayerDialog, int field_limit) {
        super(context, textViewResourceId, entities);
        this.context = context;
        this.asListLayerDialog = asListLayerDialog;
        this.FIELD_LIMIT = field_limit;
        this.cacheViews = new View[RECORDS_CACHE];
        this.inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(final int position, View convertView,
                        final ViewGroup parent) {

        View item = this.cacheViews[position % cacheViews.length];
        ViewHolder holder;

        final Entity currentEntity = getItem(position);

        if (item == null) {

            item = inflater.inflate(R.layout.aslist_list_item, parent, false);

            holder = new ViewHolder();
            holder.position = position;
            holder.charged = false;
            holder.layout =
                    (LinearLayout) item.findViewById(R.id.aslistitem_layoutlabels);
            holder.viewList = new ArrayList<>();

            createViewsLayout(holder, parent, currentEntity);

            item.setTag(holder);
        } else {
            holder = (ViewHolder) item.getTag();
        }

        if (!holder.charged || holder.position != position) {
            setViewsLayout(holder, currentEntity);
            setOnClickListener(holder.layout, currentEntity);
            holder.position = position;
            holder.charged = true;
            cacheViews[position % cacheViews.length] = item;
        }

        return item;
    }

    private void setOnClickListener(LinearLayout layout,
                                    Entity currentEntity) {
        layout.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                asListLayerDialog.dismiss();
                NavigationManager navigationManager = new NavigationManager();
                Map<String, Serializable> params = new HashMap<>();
                params.put("formId", "UIForm1");
                params.put("entity", currentEntity);
                navigationManager.navigate(context, UserFormAlphaEditActivity.class, params);
            }
        });
    }


    private void setViewsLayout(final ViewHolder holder, Entity entity) {
        final String[] columnNames = entity.getMetadata().getColumnNames();
        int numberOfColumns = columnNames.length;

        for (int index = 0; index < numberOfColumns; index++) {

            String fieldName = columnNames[index].toLowerCase();

            TextView textView = (TextView) holder.viewList.get(index);

            Object value = entity.getProperties().get(fieldName);
            textView.setText(DataUtils.nullFormat(value.toString()));
        }

    }

    private void createViewsLayout(final ViewHolder holder,
                                   final ViewGroup parent, Entity entity) {
        LinearLayout layout = holder.layout;
        final String[] columnNames = entity.getMetadata().getColumnNames();
        if (columnNames != null) {
            int i = 0;
            for (final String columnName : columnNames) {
                View view = null;
                if (columnName != null) {

                    view = createTextView(parent);
                }
                layout.addView(view);
                holder.viewList.add(view);

                i++;
                if (i >= FIELD_LIMIT) {
                    final TextView overflowTextView = createTextView(parent);
                    layout.addView(overflowTextView);
                    holder.viewList.add(view);
                    break;
                }
            }
        }

    }

    private TextView createTextView(final ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TextView output = (TextView) inflater.inflate(
                R.layout.aslist_list_item_textview, parent, false);
        return output;
    }

    static class ViewHolder {
        int position;
        boolean charged;
        LinearLayout layout;
        List<View> viewList;
    }
}
