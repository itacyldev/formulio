package es.jcyl.ita.formic.forms.view.converters;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewValueConverterFactory {

    private static ViewValueConverterFactory _instance = null;

    // TODO: we have to improve this
    private static Map<String, ViewValueConverter> map = new HashMap<String, ViewValueConverter>();

    private ViewValueConverterFactory() {
        map.put("text", new TextViewConverter());
        map.put("date", new TextViewConverter());
        map.put("datetime", new TextViewConverter());
        map.put("switcher", new SwitcherFieldViewConverter());
        map.put("select", new SpinnerValueConverter());
        map.put("radio", new RadioValueConverter());
        map.put("autocomplete", new AutoCompleteStaticValueConverter());
        map.put("dynamicAutocomplete", new AutoCompleteDynamicValueConverter());
        map.put("urlImage", new ImageViewUrlConverter());
        map.put("b64Image", new ImageView64Converter());
        map.put("byteArrayImage", new ImageViewByteArrayConverter());
        map.put("integer", new TextViewIntegerConverter());
    }

    public static ViewValueConverterFactory getInstance() {
        if (_instance == null) {
            _instance = new ViewValueConverterFactory();
        }
        return _instance;
    }


    public ViewValueConverter get(String type) {
        return map.get(type);
    }
}
