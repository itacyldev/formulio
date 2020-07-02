package es.jcyl.ita.frmdrd.ui.components.media;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MediaResource {
    File location;
    byte[] content;

    MediaResource(File f, byte[] content) {
        this.location = f;
        this.content = content;
    }

    public File getLocation() {
        return location;
    }

    public byte[] getContent() {
        return content;
    }

    public static MediaResource fromFile(File f) {
        if (!f.exists()) {
            throw new IllegalArgumentException("The file doesn't exists!: " + f.getAbsolutePath());
        }
        return new MediaResource(f, null);
    }

    public static MediaResource fromByteArray(byte[] data) {
        return new MediaResource(null, data);
    }
}