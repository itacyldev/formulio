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

import es.jcyl.ita.formic.forms.el.ValueBindingExpression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIParam {

    private String name;
    private ValueBindingExpression value;

    public UIParam(){

    };

    public UIParam (String name, ValueBindingExpression value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueBindingExpression getValue() {
        return value;
    }

    public void setValue(ValueBindingExpression value) {
        this.value = value;
    }
}
