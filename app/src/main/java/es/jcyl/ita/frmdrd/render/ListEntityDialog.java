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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.sources.SampleSourceDescriptor;
import es.jcyl.ita.frmdrd.util.DataUtils;

public class ListEntityDialog extends Dialog {
    protected static final Log LOGGER = LogFactory
            .getLog(ListEntityDialog.class);

    private final Context context;
    private final ListView listView;
    private final LinearLayout headersLayout;
    private ListEntityAdapter dataAdapter;

    private final List<View> headers = new ArrayList<View>();
    private List<Entity> entities;

    private static final int FIELD_LIMIT = 150;

    public ListEntityDialog(final Context context, String formId) {
        super(new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Dialog));

        this.setContentView(R.layout.list_field_layout);
        this.setCanceledOnTouchOutside(false);

        this.context = context;

        setTitle("entidades");

        listView = findViewById(R.id.list_view);
        headersLayout = (LinearLayout) findViewById(R.id.list_layout_headers);

        loadEntities();
        fillHeader(entities.get(0));

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
        SampleSourceDescriptor source = new SampleSourceDescriptor();
        entities = source.getEntities();
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
