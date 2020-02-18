package es.jcyl.ita.frmdrd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.Map;

public class NavigationManager {

    /**
     *
     * @param context
     * @param dest
     * @param params
     */
    public void navigate(Context context, Class<? extends Activity> dest,
                     Map<String,
            Serializable> params) {
        final Intent intent = new Intent(context,
                dest);

        for (String paramName : params.keySet()) {
            intent.putExtra(paramName, params.get(paramName));
        }

        context.startActivity(intent);

    }
}
