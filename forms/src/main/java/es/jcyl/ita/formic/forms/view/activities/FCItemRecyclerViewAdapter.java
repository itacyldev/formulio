package es.jcyl.ita.formic.forms.view.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.export.CSVExporter;
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
    private final Context context;

    public FCItemRecyclerViewAdapter(List<FormListController> items,
                                     OnListFragmentInteractionListener listener, Context ctx) {
        super();
        mValues = items;
        mListener = listener;
        mViews = new ArrayList<>();
        context = ctx;
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

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.ActionBarPopupStyle);
                PopupMenu popup = new PopupMenu(ctw, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_item);

                boolean exportable = false;
                if (holder.mItem.getView().getChildren()[0] instanceof UIDatatable) {
                    exportable = true;
                }
                popup.getMenu().findItem(R.id.action_item_export).setVisible(exportable);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.action_item_export) {//handle menu1 click
                            if (null != mListener) {
                                ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
                                task.execute(holder.mItem);
                            }
                        } else if (itemId == R.id.action_item_detail) {//handle menu1 click
                            if (null != mListener) {
                                // Notify the active callbacks interface (the activity, if the
                                // fragment is attached to one) that an item has been selected.
                                mListener.onListFragmentInteraction(holder.mItem);
                            }
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();


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
        public final TextView buttonViewOption;

        public FormListController mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            numEntities = view.findViewById(R.id.numEntities);
            buttonViewOption = view.findViewById(R.id.item_options);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }

    public void updateViews() {
        for (ViewHolder holder : mViews) {
            holder.numEntities.setText(holder.mItem.count() + " entities");
        }
    }

    private class ExportDatabaseCSVTask extends AsyncTask<FormListController, String, String> {

        protected String doInBackground(final FormListController... args) {
            File exportDir = new File(Config.getInstance().getCurrentProject().getBaseFolder() + "/exports","");
            File file = CSVExporter.exportCSV(args[0].getRepo(), ((UIDatatable) args[0].getView().getChildren()[0]).getFilter(), exportDir, args[0].getName());
            shareFile(file);
            return "";
        }

        private void shareFile(File file) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType(URLConnection.guessContentTypeFromName(file.getName()));
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse("file://"+file.getAbsolutePath()));
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

            context.startActivity(Intent.createChooser(shareIntent, "Share File"));

        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {
            if (success.isEmpty()) {
                Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
