package es.jcyl.ita.formic.forms.components.media;
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
import android.media.Image;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;

/**
 * data interchange
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MediaResource {
    File location;
    byte[] content;
    boolean hasChanged = false;

    MediaResource(File f, byte[] content) {
        this.location = f;
        this.content = content;
    }

    public void setContent(byte[] content) {
        this.content = content;
        this.hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public File getLocation() {
        return location;
    }

    public byte[] getContent() {
        return content;
    }

    public boolean hasContent() {
        return content != null && ArrayUtils.isNotEmpty(content);
    }

    public boolean hasLocation() {
        return location != null;
    }

    public static MediaResource fromFile(File f) throws IOException {
        if (!f.exists()) {
            throw new IllegalArgumentException("The file doesn't exists!: " + f.getAbsolutePath());
        }
        return new MediaResource(f, FileUtils.readFileToByteArray(f));
    }

    public static MediaResource fromByteArray(byte[] data) {
        return new MediaResource(null, data);
    }

    public Bitmap toBitMap(){
        Bitmap bmp = null;
        if(this.hasContent()){
            bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
        } else if (this.location !=null){
            bmp = BitmapFactory.decodeFile(this.location.getAbsolutePath());
        }
        return bmp;
    }

}
