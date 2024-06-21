package org.mozilla.javascript;
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

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JsDevTools {

    public static String decompile(Function function) {
        if(function instanceof BaseFunction){
            return ((BaseFunction)function).decompile(3, 1);
        } else if(function instanceof NativeJavaClass){
            return ((NativeJavaClass)function).getClassName();
        } else {
            return "";
        }
    }
}
