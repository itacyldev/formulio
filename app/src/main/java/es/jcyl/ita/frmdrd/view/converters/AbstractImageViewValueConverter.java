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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.ui.components.image.ImageResourceView;
import es.jcyl.ita.frmdrd.ui.components.media.MediaResource;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class AbstractImageViewValueConverter<T> implements ViewValueConverter<ImageResourceView> {
    @Override
    public String getValueFromViewAsString(ImageResourceView view) {
        return null;

    }

    @Override
    public <C> C getValueFromView(ImageResourceView view, Class<C> expectedType) {
        if (view.isEmpty()) {
            return null;
        } else {
            MediaResource resource = view.getResource();
            try {
                return (C) readObjectFromImageResource(resource);
            }catch(Exception e) {
                throw new ViewConfigException(DevConsole.error("An error occurred while trying to read image from view." +resource.toString() ,e));
            }
        }

    }


    @Override
    public void setViewValue(ImageResourceView view, Object value) {
        if (value == null) {
            return;
        }
        T typedValue;
        try {
            typedValue = (T) value;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Invalid value type. The converter " +
                            "[%s] found an unexpected type for given image value as [%s].", this.getClass().getName(),
                    value.getClass().getName()));
        }
        if (isMissingOrErrorImage(typedValue)) {
            DevConsole.error(String.format("The given url for the image doesn't exists: [%s]", value));
            view.setImageResource(R.drawable.image_not_found);
        }
        try {
            byte[] imgBytes = readImageBytesFromObject(typedValue);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            view.setImageBitmap(bitmap);
        } catch (Exception e) {
            DevConsole.error(String.format("Couldn't render the image with value: [%s]", value));
            view.setImageResource(R.drawable.image_not_found);
        }
    }

    @Override
    public void setViewValueAsString(ImageResourceView view, String value) {

    }

    /*** EXTENSION POINTS FOR SUBCLASSES **/

    protected abstract boolean isMissingOrErrorImage(T value);

    protected abstract byte[] readImageBytesFromObject(T value) throws IOException;

    protected abstract T readObjectFromImageResource(MediaResource resource) throws IOException;

}
