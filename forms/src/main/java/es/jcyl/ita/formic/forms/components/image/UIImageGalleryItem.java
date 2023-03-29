package es.jcyl.ita.formic.forms.components.image;

import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverterFactory;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIImageGalleryItem extends AbstractUIComponent {

    protected String label;

    protected String valueConverter;

    public UIImageGalleryItem() {
        rendererType = "imagegalleryitem";
    }

    public String getValueConverter() {
        return valueConverter;
    }

    public void setValueConverter(String valueConverter) {
        this.valueConverter = valueConverter;
    }

    public ViewValueConverter getConverter() {
        return ViewValueConverterFactory.getInstance().get(this.getValueConverter());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
