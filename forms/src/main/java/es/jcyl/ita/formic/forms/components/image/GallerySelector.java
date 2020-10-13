package es.jcyl.ita.formic.forms.components.image;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import es.jcyl.ita.formic.forms.view.activities.ActivityResultCallBack;

import static android.app.Activity.RESULT_OK;

/**
 * Helper class
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class GallerySelector implements ActivityResultCallBack<Void, Uri>,
        ActivityResultCallback<Uri> {

    private ActivityResultLauncher<Void> launcher;

    public void launch(){
        this.launcher.launch(null);
    }

    @Override
    public ActivityResultContract<Void, Uri> getContract() {
        return new ActivityResultContract() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Object input) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                return intent;
            }

            @Override
            public Object parseResult(int resultCode, @Nullable Intent intent) {
                if (resultCode == RESULT_OK) {
                    return intent.getData();
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public ActivityResultCallback<Uri> getCallBack() {
        return this;
    }

    @Override
    public void setResultLauncher(Activity activity, ActivityResultLauncher<Void> launcher) {
        this.launcher = launcher;
    }

    @Override
    public void onActivityResult(Uri result) {
        // set image content
    }


}
