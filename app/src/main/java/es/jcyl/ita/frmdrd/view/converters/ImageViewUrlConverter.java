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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.frmdrd.config.DevConsole;

/**
 * Recives the image path as url and sets it to the image view
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ImageViewUrlConverter extends AbstractViewValueConverter<String> {

    @Override
    protected boolean isMissingOrErrorImage(String path) {
        File fImage = new File(path);
        return !fImage.exists();
    }

    @Override
    protected byte[] readImageBytesFromObject(String path) throws IOException {
        try {
            return FileUtils.readFileToByteArray(new File(path));
        } catch (IOException e) {
            throw new IOException(DevConsole.error(String.format("An error occurred " +
                    "while trying to read the image: [%s]", path), e));
        }
    }

}
// b64 encoding
//    ImageView carView = (ImageView) v.findViewById(R.id.car_icon);
//
//    byte[] decodedString = Base64.decode(picture, Base64.NO_WRAP);
//    InputStream input=new ByteArrayInputStream(decodedString);
//    Bitmap ext_pic = BitmapFactory.decodeStream(input);
//                            carView.setImageBitmap(ext_pic);