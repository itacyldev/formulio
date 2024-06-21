package es.jcyl.ita.formic.forms.components.radio;
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
import android.widget.RadioButton;

import es.jcyl.ita.formic.forms.components.option.UIOption;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RadioButtonWidget extends androidx.appcompat.widget.AppCompatRadioButton {
    private UIOption option;
    public RadioButtonWidget(Context context, UIOption option) {
        super(context);
        this.option = option;
    }

    public UIOption getOption() {
        return option;
    }

    public void setOption(UIOption option) {
        this.option = option;
    }
}
