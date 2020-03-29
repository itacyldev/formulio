package es.jcyl.ita.frmdrd;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import es.jcyl.ita.frmdrd.view.FormEditViewHandlerActivity;

public class NavigationManager {

    public void navigate(Context context, String formId) {
        navigate(context, formId, new HashMap<>());
    }

//    public void navigate(Context context, String viewId, Serializable entityId) {
//        Map<String, Serializable> params = new HashMap<>();
//        params.put("entityId", entityId);
//        navigate(context, viewId, params);
//    }

    /**
     * @param context
     * @param formId
     * @param params
     */
    public void navigate(Context context, String formId, @Nullable Map<String, Serializable> params) {
        // TODO: the activity to use to render the view might depend on the viewId
        final Intent intent = new Intent(context, FormEditViewHandlerActivity.class);
        intent.putExtra("formId", formId);
        if (params != null) {
            for (String paramName : params.keySet()) {
                intent.putExtra(paramName, params.get(paramName));
            }
        }
        context.startActivity(intent);
    }
}
