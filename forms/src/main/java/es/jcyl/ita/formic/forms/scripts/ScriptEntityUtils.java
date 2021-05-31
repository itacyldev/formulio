package es.jcyl.ita.formic.forms.scripts;
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

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ScriptEntityUtils {

    public static void set(Entity entity, String property, Object value){
        if(entity == null){
            return;
        }
        PropertyType propertyMeta = entity.getMetadata().getPropertyByName(property);
        if(propertyMeta == null){
            return;
        }
        Object castedValue = ConvertUtils.convert(value, propertyMeta.getType() );
        entity.set(property, castedValue);
    }

    public static String test(String value){
        return value;
    }
}
