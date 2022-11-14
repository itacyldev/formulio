package es.jcyl.ita.formic.forms.components.link;
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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;

import static es.jcyl.ita.formic.forms.components.link.UILinkBase.TYPE.LINK;
import static es.jcyl.ita.formic.forms.config.DevConsole.error;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * User navigation component
 */
public class UILinkBase extends AbstractUIComponent {

    public enum TYPE {
        LINK, BUTTON
    }

    private TYPE type = LINK;

    private String route;

    protected ValueBindingExpression confirmation;
    protected String labelConfirmation;

    @Override
    public String getRendererType() {return type.name().toLowerCase();}

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setConfirmation(ValueBindingExpression confirmation) {
        this.confirmation = confirmation;
    }

    public String getLabelConfirmation() {
        return labelConfirmation;
    }

    public void setLabelConfirmation(String labelConfirmation) {
        this.labelConfirmation = labelConfirmation;
    }

    public boolean isConfirmation(Context context) {
        if (this.confirmation == null) {
            return false;
        } else {
            try {
                return (Boolean) ConvertUtils.convert(JexlFormUtils.eval(context, this.confirmation), Boolean.class);
            } catch (Exception e) {
                error("Error while trying to evaluate JEXL expression: " + this.confirmation.toString(), e);
                return false;
            }
        }
    }

}
