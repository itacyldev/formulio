package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.repo.EntityRelation;
import es.jcyl.ita.frmdrd.ui.components.image.UIImage;

import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.CAMERA_ACTIVE;
import static es.jcyl.ita.frmdrd.config.meta.AttributeDef.GALLERY_ACTIVE;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIImageBuilder extends BaseUIComponentBuilder<UIImage> {

    private static final String PROJECT_IMAGES = "DEFAULT_PROJECT_IMAGES";

    public UIImageBuilder(String tagName) {
        super(tagName, UIImage.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIImage> node) {
        super.setupOnSubtreeStarts(node);
        setDefaultValues(node);
    }

    private void setDefaultValues(ConfigNode<UIImage> node) {
        if (!node.hasAttribute(CAMERA_ACTIVE.name)) {
            node.setAttribute(CAMERA_ACTIVE.name, "true");
        }
        if (!node.hasAttribute(GALLERY_ACTIVE.name)) {
            node.setAttribute(GALLERY_ACTIVE.name, "true");
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIImage> node) {
        super.setupOnSubtreeEnds(node);

        UIImage image = node.getElement();
        ValueBindingExpression valueExpr = node.getElement().getValueExpression();

        boolean usesExternalRepo;
        String valueConverter;
        if (valueExpr.isLiteral()) {
            // If the expression is a literal, the image has to be retrieved using project default
            // image repository Ej: /images/myfavoriteImage.jpeg. The component is readonly and
            // the converter is by default urlImage
            image.setReadOnly(true); // this will disable camera and gallery buttons
            valueConverter = "urlImage";
            usesExternalRepo = true;
        } else if (valueExpr.isReadOnly()) {
            // if the expression is not an entity attribute binding, by default it is interpreted as
            // and expression to define the path of the image:
            // /images/${entity.category}/${entity.id}.jpeg
            valueConverter = "urlImage";
            usesExternalRepo = true;
        } else {
            // if the expression is an entity attribute binding, the image or its url is stored
            // in the entity attribute.
            // If no converter is set, by the value we interpret the user is using the entity
            // attribute to store the image path relative to the project's image repository:
            // /categoryFolder/myentityImage.jpg in this case we have to use the external repo to
            // load the image entity:
            String convertedSet = node.getAttribute("converter");
            if (StringUtils.isBlank(convertedSet) || "urlImage".equalsIgnoreCase(convertedSet)) {
                usesExternalRepo = true;
                valueConverter = "urlImage";
            } else {
                // if a different converter is set (b64Image or byteArrayImage), no external
                // repository is needed, the image content is stored in the entity attribute
                usesExternalRepo = false;
                valueConverter = convertedSet;
            }
        }
        image.setValueConverter(valueConverter);
        if (usesExternalRepo) {
            // The component uses an external image repository to retrieve/store the related image
            EntityRelation relation = createRelation(node);
            node.getElement().setEntityRelation(relation);
        }
    }

    /**
     * Define one2One relation with related entity using image node values. The
     * valueBindingExpression defines the expression to retrieve the related entity Id from the
     * context.
     * The new entity property will be named after the UIImageComponent id, so the effective value
     * expression must be replaced from "/image/paths/${entity.img_id_property}.jpg" to ${entity
     * .img1.content}.
     * Image entities are retrieved as FileEntity, depending on the converter of the component,
     * the attribute  must store the path of the image (urlImage) of the content of the image
     * file (b64Image, byteArrayImage). The expression will refer then the absolute file path
     * (${entity.imageFieldId.fAbsPath}) or the file content ${entity.imageFieldId.content}
     *
     * @param node
     * @return
     */
    private EntityRelation createRelation(ConfigNode<UIImage> node) {
        UIImage img = node.getElement();
        Repository repo = img.getRepo();
        String converter = img.getValueConverter();
        if (repo == null) {
            // use project default image repository
            repo = this.getFactory().getRepoFactory().getRepo(PROJECT_IMAGES);
        }
        if (repo == null) {
            throw new IllegalArgumentException(DevConsole.error(String.format("The component " +
                    "[%s] has defined a relation to retrieve a related entity, but no repository " +
                    "image was found, the default project's image repo (DEFAULT_PROJECT_IMAGES) " +
                    "should be active, check the project configuration.", node.getElement().getId())));
        }
        EntityRelation relation = new EntityRelation(repo, img.getId(), img.getValueExpression());
        relation.setFilter(img.getFilter());
        relation.setInsertable(!img.isReadOnly());
        relation.setDeletable(!img.isReadOnly());
        relation.setUpdatable(!img.isReadOnly());

        // replace expression
        String bindingExpression;
        if (converter.equalsIgnoreCase("urlImage")) {
            bindingExpression = "${entity.%s.fAbsPath}";
        } else {
            bindingExpression = "${entity.%s.content}"; // byteArray or StringB64
        }
        String expression = String.format(bindingExpression, img.getId());
        ValueBindingExpression effectiveExpression = this.getFactory().getExpressionFactory().create(expression);
        img.setValueExpression(effectiveExpression);
        return relation;
    }

}
