package es.jcyl.ita.formic.forms.view.converters;
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

import android.util.Base64;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import es.jcyl.ita.formic.forms.components.media.MediaResource;

/**
 * Receives the image as a B64 string, converts it to bitmap and renders
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ImageView64Converter extends AbstractImageViewValueConverter<String> {

    @Override
    protected boolean isMissingOrErrorImage(String value) {
        return StringUtils.isBlank(value);
    }

    @Override
    protected MediaResource readImageResourceFromObject(String value) throws IOException {
        byte[] decodedString = Base64.decode(value, Base64.NO_WRAP);
        return MediaResource.fromByteArray(decodedString);
    }

    @Override
    protected String readObjectFromImageResource(MediaResource resource) throws IOException {
        if (!resource.hasContent()) {
            return null;
        } else {
            return Base64.encodeToString(resource.getContent(), Base64.NO_WRAP);
        }
    }

    @Override
    protected String readObjectFromImageResourceAsString(MediaResource resource) throws IOException {
        return readObjectFromImageResource(resource);
    }
}
