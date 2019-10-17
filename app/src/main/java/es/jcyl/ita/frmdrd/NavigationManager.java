package es.jcyl.ita.frmdrd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class NavigationManager {

    /**
     *  @param context
     * @param dest
     * @param param
     */
    void navigate(Context context, Class<? extends Activity> dest,
                  String paramName, Serializable param) {
        final Intent intent = new Intent(context,
                dest);

        intent.putExtra(paramName, param);
        context.startActivity(intent);

    }
}
