package es.jcyl.ita.formic.forms.actions;
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

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 * <p>
 * Bean class to hold UserAction sequence
 */
public class UserCompositeAction extends UserAction {

    private UserAction[] actions;

    UserCompositeAction() {
        super(ActionType.COMPOSITE.name());
    }

    public UserCompositeAction(UserAction[] actions) {
        super(ActionType.COMPOSITE.name());
        this.actions = actions;
        if (this.actions == null) {
            this.actions = new UserAction[]{};
        }
    }

    public UserAction[] getActions() {
        return actions;
    }

    public void setActions(UserAction[] actions) {
        this.actions = actions;
    }

    /**
     * Setters for shareable atributes across all nested actions
     * @param param
     * @param value
     */

    public void addParam(String param, Object value) {
        if (actions != null) {
            for (UserAction nestedAction : actions) {
                nestedAction.addParam(param, value);
            }
        }
    }

    public void setWidget(Widget widget) {
        super.setWidget(widget);
        if (actions != null) {
            for (UserAction nestedAction : actions) {
                nestedAction.setWidget(widget);
            }
        }
    }

    public void setOrigin(ViewController origin) {
        if (actions != null) {
            for (UserAction nestedAction : actions) {
                nestedAction.setOrigin(origin);
            }
        }
    }

    @Override
    public void setController(String controller) {
        if (actions != null) {
            for (UserAction nestedAction : actions) {
                nestedAction.setController(controller);
            }
        }
    }

    @Override
    public void setComponent(UIComponent component) {
        if (actions != null) {
            for (UserAction nestedAction : actions) {
                nestedAction.setComponent(component);
            }
        }
    }
}
