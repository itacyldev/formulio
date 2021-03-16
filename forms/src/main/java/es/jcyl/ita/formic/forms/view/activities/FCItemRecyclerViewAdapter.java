package es.jcyl.ita.formic.forms.view.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.opencsv.CSVWriter;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.el.JexlUtils;
import es.jcyl.ita.formic.forms.export.CVSExporter;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment.OnListFragmentInteractionListener;
import es.jcyl.ita.formic.repo.Entity;

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
                                //ExportDatabaseCSVTask task=new ExportDatabaseCSVTask();
                                //task.execute(holder.mItem);
                                CVSExporter.init(context, holder.mItem.getRepo(), ((UIDatatable) holder.mItem.getView().getChildren()[0]).getFilter(), holder.mItem.getName()).exportCSV();
                                //((MainActivity) mListener).exportToCSV(holder.mItem);
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
        private final ProgressDialog dialog = new ProgressDialog(context);

        protected String doInBackground(final FormListController... args) {
            File exportDir = new File(Config.getInstance().getCurrentProject().getBaseFolder() + "/exports", "");

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, args[0].getName().concat(".csv"));

            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                UIComponent component = args[0].getView().getChildren()[0];
                UIColumn[] columns = ((UIDatatable) component).getColumns();
                int count = columns.length;

                ArrayList<Entity> listData = (ArrayList<Entity>) args[0].getRepo().find(((UIDatatable) component).getFilter());

                //Headers
                String strHeaders[] = listData.get(0).getMetadata().getPropertyNames();
                /*String strHeaders[] = new String[count];
                for (int i = 0; i< count; i++) {
                    strHeaders[i] = ((UIDatatable) component).getColumns()[i].getHeaderText();
                }*/
                csvWrite.writeNext(strHeaders);

                /*EntityMeta meta = args[0].getRepo().getMeta();
                String[] fieldFilter = meta.getPropertyNames();
                UIDatatable table = createDataTableFromRepo(args[0].getRepo(), fieldFilter);*/

                //Data
                for (int i = 0; i< listData.size(); i++) {
                    //strData[i] = listData
                    Object[] values = JexlUtils.bulkEval(listData.get(i), columns);
                    String strData[] = new String[values.length];
                    for (int j = 0; j< values.length; j++) {
                        strData[j] = (String) ConvertUtils.convert(values[j], String.class);
                    }
                    csvWrite.writeNext(strData);
                }

                csvWrite.close();

                shareFile(exportDir);

                return "";
            } catch (IOException e) {
                DevConsole.error(e.getMessage(), e);
                return "";
            }
        }

        private void shareFile(File file) {

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
            intentShareFile.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse("file://"+file.getAbsolutePath()));

            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

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
