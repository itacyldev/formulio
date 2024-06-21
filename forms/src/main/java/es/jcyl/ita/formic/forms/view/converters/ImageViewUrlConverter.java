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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.forms.components.media.MediaResource;
import es.jcyl.ita.formic.forms.components.media.MediaResourceLocator;
import es.jcyl.ita.formic.forms.config.DevConsole;

/**
 * Recives the image path as url and sets it to the image view
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ImageViewUrlConverter extends AbstractImageViewValueConverter<String> {

    /**
     * Checks if given image exists.
     *
     * @param absolutePath: An absolute path must be provided. The ImageBuilder is responsible for
     *                      treating the value attribute and retrieve a FileEntity which will provide the
     *                      absolutePath.
     * @return
     */
    @Override
    protected boolean isMissingOrErrorImage(String absolutePath) {
        File photo = new File(absolutePath);
        return photo == null || !photo.exists();
    }


    /**
     * @param absolutePath: Absolute path of the imagen
     * @return
     * @throws IOException
     */
    @Override
    protected MediaResource readImageResourceFromObject(String absolutePath) throws IOException {
        try {
            File photo = new File(absolutePath);
            MediaResource imgResource = MediaResource.fromFile(photo);
            return imgResource;
        } catch (IOException e) {
            throw new IOException(DevConsole.error(String.format("An error occurred " +
                    "while trying to read the image: [%s]", absolutePath), e));
        }
    }

    /**
     * If MediaResource encodes a location, converts this location to a project-relative path and
     * returns it.
     * If the MediaResource encodes the image in the content attribute, and this image has changed,
     * the converter stores the image in the original location.
     *
     * @param resource
     * @return
     * @throws IOException
     */
    @Override
    protected String readObjectFromImageResource(MediaResource resource) throws IOException {
        if (resource.hasContent() && resource.hasChanged()) {
            // write image in the expected location
            File imageDest = resource.getLocation();
            if (imageDest == null) {
                return null;
            }
            FileUtils.writeByteArrayToFile(imageDest, resource.getContent());
        }
        return MediaResourceLocator.relativeImagePath(resource.getLocation().getAbsolutePath());
    }

    @Override
    protected String readObjectFromImageResourceAsString(MediaResource resource) throws IOException {
        return null;
    }

    @Override
    public void setPattern(String pattern) {

    }

    @Override
    public void setType(String type) {

    }
}