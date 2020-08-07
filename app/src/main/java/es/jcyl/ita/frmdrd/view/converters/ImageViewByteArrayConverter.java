package es.jcyl.ita.frmdrd.view.converters;
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

import java.io.IOException;

import es.jcyl.ita.crtrepo.meta.types.ByteArray;
import es.jcyl.ita.frmdrd.ui.components.media.MediaResource;

/**
 * Receives the image as a B64 string, converts it to bitmap and renders
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ImageViewByteArrayConverter extends AbstractImageViewValueConverter<ByteArray> {

    @Override
    protected boolean isMissingOrErrorImage(ByteArray bArray) {
        return bArray == null || bArray.getValue().length == 0;
    }

    @Override
    protected MediaResource readImageResourceFromObject(ByteArray bArray) throws IOException {
        return MediaResource.fromByteArray(bArray.getValue()) ;
    }

    @Override
    protected ByteArray readObjectFromImageResource(MediaResource resource) throws IOException {
        if(!resource.hasContent()){
            return null;
        } else {
            return new ByteArray(resource.getContent());
        }
    }

    @Override
    protected String readObjectFromImageResourceAsString(MediaResource resource) throws IOException {
        return new String(resource.getContent());
    }
}
