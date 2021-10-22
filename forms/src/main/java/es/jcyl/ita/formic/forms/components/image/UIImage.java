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

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

import static es.jcyl.ita.formic.forms.components.image.UIImage.ImageInputType.CAMERA_ONLY;
import static es.jcyl.ita.formic.forms.components.image.UIImage.ImageInputType.GALLERY_AND_CAMERA;
import static es.jcyl.ita.formic.forms.components.image.UIImage.ImageInputType.GALLERY_ONLY;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Image component
 */
public class UIImage extends UIInputComponent {

    private static final String IMAGE = "image";

    /**
     * filterable component
     **/
    private Repository repo;
    private Filter filter;
    private String[] mandatoryFilters;
    private Integer width;
    private Integer height;
    private boolean embedded;
    private String repoProperty;
    private boolean hasSketchButton = true;
    private boolean hasCameraButton = true;
    private boolean hasGalleryButton = true;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public boolean hasSketchButton() {
        return hasSketchButton;
    }

    public void setHasSketchButton(boolean hasSketchButton) {
        this.hasSketchButton = hasSketchButton;
    }

    public boolean hasCameraButton() {
        return hasCameraButton;
    }

    public void setHasCameraButton(boolean hasCameraButton) {
        this.hasCameraButton = hasCameraButton;
    }

    public boolean hasGalleryButton() {
        return hasGalleryButton;
    }

    public void setHasGalleryButton(boolean hasGalleryButton) {
        this.hasGalleryButton = hasGalleryButton;
    }

    @Override
    public String getRendererType() {
        return IMAGE;
    }

    /**
     * Returns the key that references the viewvalueConverter set in the component. By default,
     * the component uses a ImageViewUrlConverter.
     *
     * @return
     */
    @Override
    public String getValueConverter() {
        String converter = super.getValueConverter();
        return (converter == null) ? "urlImage" : converter;
    }

    /******* EntityRelation component interface **/
    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Repository getRepo() {
        return this.repo;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public boolean isBound() {
        return (getValueExpression() == null) ? false : !getValueExpression().isReadonly();
    }

    public boolean isCameraActive() {
        return this.getInputType() == CAMERA_ONLY.value
                || this.getInputType() == GALLERY_AND_CAMERA.value;
    }

    public boolean isGalleryActive() {
        return this.getInputType() == GALLERY_ONLY.value
                || this.getInputType() == GALLERY_AND_CAMERA.value;
    }

    public enum ImageInputType {
        NO_CONTROLS(0),
        CAMERA_ONLY(1),
        GALLERY_ONLY(2),
        GALLERY_AND_CAMERA(3);

        public final int value;

        private ImageInputType(int value) {
            this.value = value;
        }

        public ImageInputType fromValue(int value) {
            for (ImageInputType e : values()) {
                if (e.value == value) {
                    return e;
                }
            }
            throw new IllegalStateException(String.format("Invalid value for Image input type: %s" +
                    " expected one of [0,1,2,3].", value));
        }
    }

    public boolean getEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public String getRepoProperty() {
        return repoProperty;
    }

    public void setRepoProperty(String repoProperty) {
        this.repoProperty = repoProperty;
    }

}
