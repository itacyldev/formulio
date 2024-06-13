package es.jcyl.ita.formic.forms.view.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.handlers.JobActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.export.CSVExporter;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.activities.FormListFragment.OnListFragmentInteractionListener;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;

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
        Collections.sort(mValues, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getDescription());
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
                //if (holder.mItem.getView().getChildren()[0] instanceof UIDatatable || holder.mItem.getView().getChildren()[0] instanceof UIDatalist){
                if (holder.mItem.count() > 0) {
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
                                ExportDatabaseCSVTask task = new ExportDatabaseCSVTask();
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
        //public final TextView buttonViewOption;
        public final ImageButton buttonViewOption;

        public FormListController mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            numEntities = view.findViewById(R.id.numEntities);
            //buttonViewOption = view.findViewById(R.id.item_options);
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

    protected void export(){
        MainController mc = MainController.getInstance();

        UserAction userAction = UserActionHelper.newAction(ActionType.JOB);
        Map<String, Object> params = new HashMap<>();
        String JOB_ID = "job_generar_acta_toma_muestras";
        params.put("jobId", JOB_ID);
        params.put("expediente_id", 1);
        userAction.setParams(params);

        // act - execute action
        JobActionHandler handler = new JobActionHandler(mc, mc.getRouter());
        handler.handle(new ActionContext(mc.getViewController(), context), userAction);


        // create facade and related repositories
        JobFacade facade = new JobFacade();
        JobConfigRepo repo = new JobConfigRepo();
        facade.setJobConfigRepo(repo);

    }

    private class ExportDatabaseCSVTask extends AsyncTask<FormListController, String, String> {


        protected String doInBackground(final FormListController... controllers) {
            File exportDir = new File(App.getInstance().getCurrentProject().getBaseFolder() + "/exports", "");

            CSVExporter csvExporter = CSVExporter.getInstance();
            File file = csvExporter.export(controllers[0].getEntityList().getRepo(),
                    controllers[0].getEntityList().getFilter(), exportDir,
                    controllers[0].getName(), "csv");

            shareFile(file);
            return "";
        }

        private void shareFile(File file) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(URLConnection.guessContentTypeFromName(file.getName()));
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file));
            shareIntent.setType("text/csv");

            context.startActivity(Intent.createChooser(shareIntent, "Share File"));
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {
            if (success.isEmpty()) {
                UserMessagesHelper.toast(context, "Export successful!", Toast.LENGTH_SHORT);
            } else {
                UserMessagesHelper.toast(context, "Export failed!", Toast.LENGTH_SHORT);
            }
        }
    }

}
