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

import android.widget.ImageView;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders uiImage componentes using Android ImageView
 */
public class UIImageRenderer extends InputRenderer<ImageView, UIImage> {

//        ImageView image = (ImageView) findViewById(R.id.test_image);
//        Bitmap bMap = BitmapFactory.decodeFile("/sdcard/test2.png");
//        image.setImageBitmap(bMap);

    @Override
    protected int getComponentLayout() {
        return R.layout.component_image;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputFieldView<ImageView> baseView, UIImage component) {

    }

    @Override
    protected void composeView(RenderingEnv env, InputFieldView<ImageView> baseView, UIImage component) {

    }

    protected void setupInputView(RenderingEnv env, InputFieldView<ImageView> baseView, ImageView inputView, UIImage component) {
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

    protected void setValue(RenderingEnv env, InputFieldView baseView, UIImage component, ImageView inputView) {
        Object value = getValue(component, env, null);
        baseView.getConverter().setViewValue(inputView, value);
    }
}
