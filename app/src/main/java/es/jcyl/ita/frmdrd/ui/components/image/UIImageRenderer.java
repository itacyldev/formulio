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
import es.jcyl.ita.frmdrd.view.widget.InputWidget;
import es.jcyl.ita.frmdrd.view.render.InputRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders uiImage componentes using Android ImageView
 */
public class UIImageRenderer extends InputRenderer<UIImage, ImageResourceView> {

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.widget_image;
    }

    @Override
    protected int getInputViewId() {
        return R.layout.widget_image;
    }

    @Override
    protected InputWidget<UIImage, ImageResourceView> createWidget(RenderingEnv env, UIImage component) {
        ImageWidget imageWidget = (ImageWidget) super.createWidget(env, component);
        env.getFormActivity().registerCallBackForActivity(imageWidget);
        return imageWidget;
    }



    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {
        ImageResourceView inputView = widget.getInputView();
        UIImage component = widget.getComponent();
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
    protected void setValueInView(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {
        ImageResourceView inputView = widget.getInputView();
        UIImage component = widget.getComponent();
        Object value = getComponentValue(env, component, null);
        widget.getConverter().setViewValue(inputView, value);
//        imageWidget.setMediaResource();
    }

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {

    }

}
