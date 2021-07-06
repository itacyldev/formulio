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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraph;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICard extends AbstractUIComponent {

    String template;
    UIHeading title;
    UIHeading subtitle;
    UIParagraph description;
    UIImage image;

    boolean showHeader = true;
    boolean expandable;
    boolean expanded = true;

    String imagePosition = ImagePosition.TOP.getPosition();

    public UICard() {
        rendererType ="card";
        renderChildren = true;
    }

    public UIHeading getTitle() {
        return title;
    }

    public void setTitle(UIHeading title) {
        this.title = title;
    }

    public UIImage getImage() {
        return image;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }


    public UIHeading getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(UIHeading subtitle) {
        this.subtitle = subtitle;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(String imagePositionStr) {
        this.imagePosition = imagePositionStr;

    }

    public UIParagraph getDescription() {
        return description;
    }

    public void setDescription(UIParagraph description) {
        this.description = description;
    }

    public enum ImagePosition {
        TOP("top"), BOTTOM("bottom"), RIGHT("right"), LEFT("left"), NONE("none");

        String position;

        ImagePosition(String position) {
            this.position = position;
        }

        public String getPosition() {
            return position;
        }
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public String getLabelValue(Context context) {
        String labelValue = null;
        if (StringUtils.isNotEmpty(label)) {
            Object value = JexlFormUtils.eval(context, label);
            labelValue = (value == null) ? "" : value.toString();
        }
        return labelValue;
    }
}
