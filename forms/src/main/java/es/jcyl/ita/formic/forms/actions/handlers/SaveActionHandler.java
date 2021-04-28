package es.jcyl.ita.formic.forms.actions.handlers;
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

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.validation.ValidatorException;

/**
 * Predefined save action to persist changes in form's main entity.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class SaveActionHandler extends AbstractActionHandler {

    public SaveActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        FormEditController formController = (FormEditController) actionContext.getFc();
        formController.save(this.mc.getGlobalContext());
    }

    @Override
    public String getSuccessMessage(ActionContext actionContext, UserAction action) {
        return Config.getInstance().getStringResource(R.string.action_save_success);
    }

    @Override
    protected String getErrorMessage(UserAction action, Exception e) {
        if (e instanceof ValidatorException) {
            return Config.getInstance().getStringResource(R.string.action_generic_invalid_form);
        } else {
            return super.getErrorMessage(action, e);
        }
    }

}
