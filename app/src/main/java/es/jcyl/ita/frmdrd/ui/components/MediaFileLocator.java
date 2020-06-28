package es.jcyl.ita.frmdrd.ui.components;
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

import java.io.File;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.el.JexlBindingExpression;
import es.jcyl.ita.frmdrd.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MediaFileLocator {

    public File locate(JexlBindingExpression expr, Context context){
//        if (expr == null) {
//            return null;
//        } else {
//            Class expected;
//            if(expr.isLiteral() || expr.isReadOnly()){
//                expected = String.class;
//            } else {
//                expected = expr.getExpectedType();
//            }
//            // evaluate expression against context
//            return JexlUtils.eval(context, expr);
//        }

        return null;

    }
}
