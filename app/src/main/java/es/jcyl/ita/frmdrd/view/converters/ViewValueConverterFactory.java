package es.jcyl.ita.frmdrd.view.converters;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewValueConverterFactory {

    private static ViewValueConverterFactory _instance = null;

    // TODO: we have to improve this
    private static Map<String, ViewValueConverter> map = new HashMap<String, ViewValueConverter>();

    private ViewValueConverterFactory() {
        map.put(UIField.TYPE.TEXT.name(), new TextFieldViewConverter());
        map.put(UIField.TYPE.DATE.name(), new DateFieldViewConverter());
        map.put(UIField.TYPE.BOOLEAN.name(), new SwitcherFieldViewConverter());
    }

    public static ViewValueConverterFactory getInstance() {
        if (_instance == null) {
            _instance = new ViewValueConverterFactory();
        }
        return _instance;
    }

    public ViewValueConverter get(UIComponent component) {
        return map.get(((UIField) component).getType());
    }


}
