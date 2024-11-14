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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.ByteArrayOutputStream;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.media.MediaResource;
import es.jcyl.ita.formic.forms.view.activities.ActivityResultCallBack;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ImageWidget extends InputWidget<UIImage, ImageResourceView>
        implements ActivityResultCallBack<Void, Bitmap>, ActivityResultCallback<Bitmap> {

    private ActivityResultLauncher<Void> launcher;
    private GallerySelector gallerySelector;
    private Entity mainEntity;

    public ImageWidget(Context context) {
        super(context);
    }

    public ImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setup(RenderingEnv env) {
        // check components to show
        setCameraButton(env);

        setGalleryButton(env);

        setSketchButton(env);

        this.mainEntity = env.getWidgetContext().getEntity();

        ImageView imgView = (ImageView) this.getInputView();

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(v);
            }
        });
      }

    public void showImage(View view){
        ImageView imageView = (ImageView) view;
        Drawable drawable = imageView.getDrawable();

        // Crear un AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        // Crear un nuevo ImageView para el diálogo
        ImageView alertImageView = new ImageView(this.getContext());
        alertImageView.setImageDrawable(drawable);

        // Ajustar las dimensiones del ImageView
        alertImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,   // Anchura que ocupe todo el espacio
                LinearLayout.LayoutParams.WRAP_CONTENT    // Altura que se ajuste a la imagen
        ));
        alertImageView.setAdjustViewBounds(true); // Permite que se ajuste sin deformarse

        // (Opcional) Envolver en un ScrollView para permitir el desplazamiento si la imagen es muy grande
        ScrollView scrollView = new ScrollView(this.getContext());
        scrollView.addView(alertImageView);

        // Configurar la vista del AlertDialog
        builder.setView(scrollView);

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setCameraButton(RenderingEnv env) {
        Button cameraButton = this.findViewById(R.id.btn_camera);
        if (!component.isCameraActive()) {// TODO: or device has no camera (check throw context.device)
            cameraButton.setVisibility(View.INVISIBLE);
        }else if (Boolean.TRUE.equals(ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class))) {
            cameraButton.setEnabled(false);
        } else {
            cameraButton.setOnClickListener(v -> launcher.launch(null));
        }
    }

    private void setSketchButton(RenderingEnv env) {
        Button sketchButton = this.findViewById(R.id.btn_sketch);
        ImageResourceView inputView = this.getInputView();
        if (!component.isSketchActive()) {
            sketchButton.setVisibility(View.INVISIBLE);
        }else if (Boolean.TRUE.equals(ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class))) {
            sketchButton.setEnabled(false);
        } else {
            sketchButton.setOnClickListener((View v) -> {
                SketchDialog sketchDialog = new
                        SketchDialog(env.getFormActivity().getActivity(), inputView);
                sketchDialog.getWindow().setBackgroundDrawable(new ColorDrawable
                        (Color.TRANSPARENT));
                sketchDialog.show();

                sketchDialog.setOnDismissListener((DialogInterface dialog) -> {
                    Bitmap imageData = sketchDialog.getBitmap();
                    if (imageData != null) {
                        setImageBitmap(imageData);
                    }
                });
            });
        }
    }

    private void setGalleryButton(RenderingEnv env) {
        Button galleryButton = this.findViewById(R.id.btn_gallery);
        // TODO::
        if (!component.isGalleryActive()) {
            galleryButton.setVisibility(View.INVISIBLE);
        }else if (Boolean.TRUE.equals(ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class))) {
            galleryButton.setEnabled(false);
        } else {
            galleryButton.setOnClickListener((View v) -> gallerySelector.launch());
        }
    }

    public GallerySelector getGallerySelector() {
        if (this.gallerySelector == null) {
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
        setImageBitmap(imageData);
    }

    private void setImageBitmap(Bitmap imageData){
        // extract image content
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageData.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] byteArray = stream.toByteArray();

        if (component.isNestedProperty()) {
            updateRelatedEntity(byteArray);
        }

        MediaResource imgResource = MediaResource.fromByteArray(byteArray);
        getInputView().setResource(imgResource);
        getInputView().setImageBitmap(imageData);
    }

    /**
     * When the image content is stored in a related Image Entity (fileEntity), this related
     * entity has to be created/updated.
     *
     * @param byteArray
     */
    public void updateRelatedEntity(byte[] byteArray) {
        // the related entity is stored in the main entity using current component Id as property name
        Entity entity = (Entity) mainEntity.get(component.getId());
        if (entity == null) {
            // create new entity
            entity = ((EditableRepository) component.getRepo()).newEntity();
            mainEntity.set(component.getId(), entity, true);
        }
        entity.set(component.getRepoProperty(), new ByteArray(byteArray));
    }

    @Override
    public Object getState() {
        return this.getInputView()!=null?this.getInputView().getResource():null;
    }

    @Override
    public void setState(Object value) {
        if(value == null){
            return;
        }
        MediaResource resource = (MediaResource) value;
        getInputView().setResource(resource);
        Bitmap bitmap = resource.toBitMap();
        if (bitmap != null) {
            this.getInputView().setImageBitmap(bitmap);
        }
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
