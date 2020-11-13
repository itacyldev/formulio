package es.jcyl.ita.formic.forms.view.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link +} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FCItemRecyclerViewAdapter extends RecyclerView.Adapter<FCItemRecyclerViewAdapter.ViewHolder> {

    private final List<FormListController> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final List<ViewHolder> mViews;

    public FCItemRecyclerViewAdapter(List<FormListController> items,
                                     OnListFragmentInteractionListener listener) {
        super();
        mValues = items;
        mListener = listener;
        mViews = new ArrayList<>();


        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                // Do nothing
                int i = 0;
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                // do nothing
                int i = 0;
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                // fallback to onItemRangeChanged(positionStart, itemCount) if app
                // does not override this method.
                onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                // do nothing
                int i = 0;
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                // do nothing
                int i = 0;
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                // do nothing
                int i = 0;
            }
        });

        FormListController a = items.get(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.form_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getId());
        holder.mContentView.setText(holder.mItem.getName());
        holder.numEntities.setText(holder.mItem.count() + " entities");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        mViews.add(position, holder);

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.numEntities.setText(holder.mItem.count() + " entities");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView numEntities;

        public FormListController mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            numEntities = (TextView) view.findViewById(R.id.numEntities);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }

    public void updateViews() {
        for(ViewHolder holder: mViews){
            holder.numEntities.setText(holder.mItem.count() + " entities");
        }
    }
}
