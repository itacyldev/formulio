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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders uiImage componentes using Android ImageView
 */
public class UIImageRenderer extends InputRenderer<ImageResourceView, UIImage> {

//        ImageView image = (ImageView) findViewById(R.id.test_image);
//        Bitmap bMap = BitmapFactory.decodeFile("/sdcard/test2.png");
//        image.setImageBitmap(bMap);

    @Override
    protected int getComponentLayoutId() {
        return R.layout.component_image_widget;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<ImageResourceView> baseView, UIImage component) {

    }

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<ImageResourceView> baseView, UIImage component) {
//        Button button = ViewHelper.findViewAndSetId(baseView, R.id.btn_camera,
//                Button.class);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//                startActivityForResult(intent, 7);
//
//            }
//        });
    }


    protected void setupInputView(RenderingEnv env, InputFieldView<ImageResourceView> baseView, ImageResourceView inputView, UIImage component) {
        super.setupInputView(env, baseView, inputView, component);

        inputView.requestLayout();
        Integer height = component.getHeight();
        if (height != null) {
            inputView.getLayoutParams().height = height;
        }
        Integer width = component.getWidth();
        if (width != null) {
            inputView.getLayoutParams().width = width;
        }
    }

    @Override
    protected void setValue(RenderingEnv env, InputFieldView<ImageResourceView> baseView, ImageResourceView inputView, UIImage component) {
        Object value = getValue(component, env, null);
        baseView.getConverter().setViewValue(inputView, value);
    }

}
