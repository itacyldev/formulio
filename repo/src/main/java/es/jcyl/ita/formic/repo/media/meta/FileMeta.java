package es.jcyl.ita.formic.repo.media.meta;
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

import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * Support to file based entities
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FileMeta extends EntityMeta {

    private static PropertyType[] props = new PropertyType[]{
            new PropertyType("id", String.class, "", true),
            new PropertyType("name", String.class, "", false),
            new PropertyType("content", ByteArray.class, "", false),
            new PropertyType("parentPath", String.class, "", false),
            new PropertyType("absolutePath", String.class, "", false),
            new PropertyType("size", Long.class, "", false),
            new PropertyType("file", File.class, "", false)
    };

    public FileMeta() {
        super("file", props, new String[]{"id"});
    }
}
