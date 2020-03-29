package es.jcyl.ita.frmdrd.ui.components.view;
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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIView extends UIComponent {

    List<UIForm> forms;

    public UIView(String id) {
        setId(id);
        setRendererType("view");
    }

    @Override
    public boolean isRenderChildren() {
        return true;
    }

    @Override
    public void validate(Context context) {

    }

    public UIForm getForm(String formId) {
        for (UIForm f : this.getForms()) {
            if (f.getId().equalsIgnoreCase(formId)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Give and identifier with the expression formId.elementId, returns the component.
     *
     * @param id
     * @return
     */
    public UIComponent findFormElement(String id) {
        String[] splits = id.split("\\.");
        if (splits.length != 2) {
            throw new IllegalArgumentException(String.format("Unexpected id expression. The id must " +
                    "follow the rule: formId.elementId: [%s].", id));
        }
        UIForm f = this.getForm(splits[0]);
        if (f == null) {
            throw new IllegalArgumentException(String.format("Illegal form element expression. " +
                    "No form found with the id [%s].", splits[0]));
        }
        UIComponent c = f.getElement(splits[1]);
        if (c == null) {
            throw new IllegalArgumentException(String.format("Illegal form element expression. " +
                    "No element found with the id [%s] in the form[%s].", splits[1], splits[0]));
        }
        return c;
    }

    public List<UIForm> getForms() {
        if (this.forms == null) {
            this.forms = new ArrayList<UIForm>();
            findForms(this, this.forms);
        }
        return this.forms;
    }

    /**
     * Recursively go over the component tree storing forms in the given array
     *
     * @param root
     * @param forms
     */
    private void findForms(UIComponent root, List<UIForm> forms) {
        if (root instanceof UIForm) {
            forms.add((UIForm) root);
        } else {
            if (root.hasChildren()) {
                for (UIComponent c : root.getChildren()) {
                    findForms(c, forms);
                }
            }
        }

    }
}
