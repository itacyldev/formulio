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

    public void setWidget(Widget widget) {
        super.setWidget(widget);
        if(actions!=null){
            for(UserAction nestedAction: actions){
                nestedAction.setWidget(widget);
            }
        }
    }
}
