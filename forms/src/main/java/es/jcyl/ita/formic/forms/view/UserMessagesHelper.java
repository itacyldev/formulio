package es.jcyl.ita.formic.forms.view;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import es.jcyl.ita.formic.forms.router.Router;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Basic implementation to show messages in view.
 * TODO: use the global context to store the messages instead of the router
 */
public class UserMessagesHelper {

    public static void showGlobalMessages(Context viewContext, Router router) {
        // TODO: use the global context to store the messages instead of the router
        String[] messages = router.getGlobalMessages();
        if (ArrayUtils.isNotEmpty(messages)) {
            toast(viewContext, messages);
            router.clearGlobalMessages();
        }
    }

    public static void toast(Context viewContext, String msg) {
        if (msg != null) {
            Toast.makeText(viewContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void toast(Context viewContext, String[] msg) {
        // TODO: special toast: https://stackoverflow.com/questions/22594376/display-two-toast-messages-at-once
        // for now, show just first one
        if (msg[0] != null) {
            toast(viewContext, msg[0]);
        }
    }

    public static void toast(Context viewContext, String msg, int duration) {
        Toast toast = Toast.makeText(viewContext, msg, duration);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            View view = toast.getView();
            TextView text = (TextView) view.findViewById(android.R.id.message);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(viewContext);
            String currentTheme = sharedPreferences.getString("current_theme", "light");
        }
        toast.show();
    }
}


