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

import java.io.ByteArrayOutputStream;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.repo.EntityRelation;
import es.jcyl.ita.formic.forms.components.media.MediaResource;
import es.jcyl.ita.formic.forms.view.activities.ActivityResultCallBack;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ImageWidget extends InputWidget<UIImage, ImageResourceView>
        implements ActivityResultCallBack<Void, Bitmap>, ActivityResultCallback<Bitmap> {

    private ActivityResultLauncher<Void> launcher;
    private GallerySelector gallerySelector;

    public ImageWidget(Context context) {
        super(context);
    }

    public ImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RenderingEnv env) {
        // check components to show
        Button cameraButton = this.findViewById(R.id.btn_camera);
        if (!component.isCameraActive()) { // TODO: or device has no camera (check throw context.device)
            cameraButton.setVisibility(View.INVISIBLE);
        } else {
            if (this.component.isReadOnly()) {
                cameraButton.setEnabled(false);
            } else {
                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launcher.launch(null);
                    }
                });
            }
        }
        Button galleryButton = this.findViewById(R.id.btn_gallery);
        if (!component.isGalleryActive()) { // TODO: or device has no camera (check throw context.device)
            galleryButton.setVisibility(View.INVISIBLE);
        } else {
            if (this.component.isReadOnly()) {
                cameraButton.setEnabled(false);
            } else {
                galleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gallerySelector.launch();
                    }
                });
            }
        }
    }

    public GallerySelector getGallerySelector(){
        if (this.gallerySelector == null){
            this.gallerySelector = new GallerySelector();
        }
        return this.gallerySelector;
    }

    @Override
    public void setResultLauncher(Activity activity, ActivityResultLauncher<Void> launcher) {
        this.launcher = launcher;
    }

    /****************************************************************/
    /********* ActivityResultCallback interface implementation   ****/
    /****************************************************************/
    @Override
    public void onActivityResult(Bitmap imageData) {
        if (imageData == null) {
            return;// no image captured
        }
        // extract image content
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageData.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] byteArray = stream.toByteArray();

        if (component.isEntityRelation()) {
            updateRelatedEntity(byteArray);
        }

        MediaResource imgResource = getInputView().getResource();
        if (imgResource == null) {
            // no previous photo
            imgResource = MediaResource.fromByteArray(byteArray);
            getInputView().setResource(imgResource);
        } else {
            imgResource.setContent(byteArray);
        }
        getInputView().setImageBitmap(imageData);
    }

    /**
     * When the image content is stored in a related Image Entity (fileEntity), this related
     * entity has to be created/updated.
     *
     * @param byteArray
     */
    private void updateRelatedEntity(byte[] byteArray) {
        Entity entity = component.getEntity();
        if (entity == null) {
            EntityRelation rel = component.getEntityRelation();
            // no related entity, create entity and media resource
            EditableRepository repo = (EditableRepository) rel.getRepo();
            entity = repo.newEntity();
            component.setEntity(entity);

            // set new entity to parent form entity
            String mainPropertyName = component.getEntityRelation().getName();
            Entity mainEntity = component.getParentForm().getEntity();
            mainEntity.set(mainPropertyName, entity, true);

            MediaResource imgResource = MediaResource.fromByteArray(byteArray);
            getInputView().setResource(imgResource);
        }
        entity.set("content", new ByteArray(byteArray));
    }

    @Override
    public Object getState() {
        return this.getInputView().getResource();
    }

    @Override
    public void setState(Object value) {
        getInputView().setResource((MediaResource) value);
    }

    @Override
    public ActivityResultContract<Void, Bitmap> getContract() {
        return new ActivityResultContracts.TakePicturePreview();
    }

    @Override
    public ActivityResultCallback<Bitmap> getCallBack() {
        return this;
    }

}
