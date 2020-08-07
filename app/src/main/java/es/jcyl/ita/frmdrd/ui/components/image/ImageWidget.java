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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.ui.components.media.MediaResource;
import es.jcyl.ita.frmdrd.view.widget.InputWidget;
import es.jcyl.ita.frmdrd.view.activities.ActivityResultCallBack;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ImageWidget extends InputWidget<UIImage, ImageResourceView>
        implements ActivityResultCallBack<Void, Bitmap>, ActivityResultCallback<Bitmap> {

    private ActivityResultLauncher<Void> launcher;
    private MediaResource mediaResource;
    private Repository repo;

    public ImageWidget(Context context) {
        super(context);
    }

    public ImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup() {
        Button cameraButton = this.findViewById(R.id.btn_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(null);
            }
        });
        this.repo = getComponent().getRepo();
    }

    @Override
    public ActivityResultContract<Void, Bitmap> getContract() {
        return new ActivityResultContracts.TakePicturePreview();
    }

    @Override
    public ActivityResultCallback<Bitmap> getCallBack() {
        return this;
    }

    @Override
    public void setResultLauncher(Activity activity, ActivityResultLauncher<Void> launcher) {
        this.launcher = launcher;
    }

    @Override
    public void onActivityResult(Bitmap imageData) {
        getInputView().setImageBitmap(imageData);
    }


    public MediaResource getMediaResource() {
        return mediaResource;
    }

    public void setMediaResource(MediaResource mediaResource) {
        this.mediaResource = mediaResource;
    }
}
