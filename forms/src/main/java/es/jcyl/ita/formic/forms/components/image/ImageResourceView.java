package es.jcyl.ita.formic.forms.components.image;
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import es.jcyl.ita.formic.forms.components.media.MediaResource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@SuppressLint("AppCompatCustomView")
public class ImageResourceView extends ImageView {

    private MediaResource resource;

    public ImageResourceView(Context context) {
        super(context);
    }

    public ImageResourceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageResourceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageResourceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MediaResource getResource() {
        return this.resource;
    }

    public void setResource(MediaResource resource) {
        this.resource = resource;
    }

    public boolean isEmpty() {
        return resource == null;
    }
}
