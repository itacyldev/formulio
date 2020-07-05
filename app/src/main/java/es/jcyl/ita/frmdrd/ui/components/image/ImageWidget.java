package es.jcyl.ita.frmdrd.ui.components.image;
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
 * without warranties or conditions of any kind, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ImageWidget extends LinearLayout {
    private static final int CAMERA_REQUEST = 1888;
    private ImageResourceView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Activity activity;
    private FragmentManager fragManager;

    private Context context;

    public ImageWidget(Context context) {
        super(context);
    }

    public ImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void setup() {
        Button cameraButton = this.findViewById(R.id.btn_camera);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

//
//
//    class TakePicture extends ActivityResultContract<Void, Bitmap> {
//
//        @NonNull
//        @Override
//        public Intent createIntent(@NonNull Context context, Void input) {
//            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            return cameraIntent;
//        }
//
//        @Nullable
//        @Override
//        public Bitmap parseResult(int resultCode, @Nullable Intent intent) {
//            if (resultCode != Activity.RESULT_OK) return null;
//            if (intent == null) return null;
//            return intent.getParcelableExtra("data");
//        }
//    }

}
