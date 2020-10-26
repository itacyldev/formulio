package es.jcyl.ita.formic.forms.components.card;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading1;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading2;
import es.jcyl.ita.formic.forms.components.image.UIImage;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICard extends UIComponent {

    String template;
    UIHeading1 title;
    UIHeading2 subtitle;
    UIImage image;

    public UIHeading1 getTitle() {
        return title;
    }

    public void setTitle(UIHeading1 title) {
        this.title = title;
    }


    public UIImage getImage() {
        return image;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }


    public UIHeading2 getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(UIHeading2 subtitle) {
        this.subtitle = subtitle;
    }


}
