package es.jcyl.ita.formic.forms.config.builders.ui;
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

import java.util.Collections;

import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.CalculatedProperty;
import es.jcyl.ita.formic.repo.EntityMapping;
import es.jcyl.ita.formic.repo.Repository;

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
        if (!node.hasAttribute(AttributeDef.INPUT_TYPE.name)) {
            node.getElement().setInputType(UIImage.ImageInputType.GALLERY_AND_CAMERA.value); // show camera and gallery buttons
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIImage> node) {
        super.setupOnSubtreeEnds(node);

        UIImage image = node.getElement();
        ValueBindingExpression valueExpr = node.getElement().getValueExpression();

        boolean usesExternalRepo;
        if (valueExpr.isLiteral()) {
            // If the expression is a literal, the image has to be retrieved using project default
            // image repository Ej: /images/myfavoriteImage.jpeg. The component is readonly
            image.setReadOnly(true); // this will disable camera and gallery buttons
            image.setInputType(UIImage.ImageInputType.NO_CONTROLS.value);
            usesExternalRepo = true;
        } else if (valueExpr.isReadOnly()) {
            // if the expression is not an entity attribute binding, by default it is interpreted as
            // and expression to define the Id of the entity in the repository. For example in a
            // FileEntity repository: ${entity.category}/${entity.id}.jpeg
            usesExternalRepo = true;
        } else {
            // If the expression defines an entity attribute binding, the entity property
            // referenced by the expression can contain the Id of the related entity in an external
            // repository or the ImageEntity itself. The attribute EMBEDDED is used to determine it.
            if (node.hasAttribute(AttributeDef.EMBEDDED.name)) {
                // the entity is stored as an entity property
                usesExternalRepo = !image.getEmbedded();
                if (image.getEmbedded() && image.getRepo() != null) {
                    throw new ConfigurationException(DevConsole.error(String.format("Cannot use " +
                                    "embedded='true' and 'repo' attribute. With embedded='true' you mean " +
                                    "you're retrieving your image as an entity property, so no repository " +
                                    "is needed. Remove repo attribute from the <image id=[%s]/>.",
                            node.getId())));
                }
            } else {
                usesExternalRepo = true;
            }
        }
        if (!node.hasAttribute(AttributeDef.CONVERTER.name)) { // by default treat image content as raw-bytes
            image.setValueConverter("byteArrayImage");
        }
        if (usesExternalRepo) {
            // The component uses an external image repository to retrieve/store the related image,
            // find the first parent that can hold a repository and set the mapping
            ConfigNode parentRepo = BuilderHelper.findParentRepo(node);
            if (parentRepo == null || parentRepo.getElement() == null) {
                throw new ConfigurationException(DevConsole.error(String.format("Error defining the " +
                        "mapping to retrieve the  element <image id=[%s]/>. No parent repository " +
                        "found, check the image is included in a <form/> and it has the 'repo' " +
                        "attribute set. [${file}].", node.getId())));
            }
            EntityMapping relation = createRelation(node);
            FilterableComponent ancestor = (FilterableComponent) parentRepo.getElement();
            ancestor.getRepo().addMapping(relation);
            image.setEntityMapping(true);
        }
    }

    /**
     * Define one2one relation with related entity using image node values. The
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
    private EntityMapping createRelation(ConfigNode<UIImage> node) {
        UIImage img = node.getElement();
        Repository repo = img.getRepo();
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
        ValueBindingExpression imgBndExpr = img.getValueExpression();
        String fkExpression = imgBndExpr.toString();
        if (!fkExpression.contains("$")) {
            // its a literal string, convert to literal JEXL expression
            fkExpression = "${'%s'}".format(fkExpression);
        }
        EntityMapping relation = new EntityMapping(repo, fkExpression, img.getId());
        relation.setFilter(img.getFilter());
        relation.setInsertable(!img.isReadOnly());
        relation.setDeletable(!img.isReadOnly());
        relation.setUpdatable(!img.isReadOnly());

        if (imgBndExpr.isReadOnly() && !imgBndExpr.isLiteral()) {
            // if the expression is a readonly expression that uses entity attributes to
            // calculate the ID, we need to define a calculated property for the entity that will
            // be interpreted before the entity is saved: ID = valueExpression
            CalculatedProperty cp = new CalculatedProperty(AttributeDef.ID.name, imgBndExpr.toString());
            relation.setCalcProps(Collections.singletonList(cp));
        }

        // use content property of related entity, it will contain the image as the byteArray or StringB64
        String imageContentProperty = node.hasAttribute("repoProperty") ?
                node.getAttribute("repoProperty") : "content";
        String expression = String.format("${entity.%s.%s}", img.getId(), imageContentProperty);
        ValueBindingExpression effectiveExpression = this.getFactory().getExpressionFactory().create(expression);
        img.setValueExpression(effectiveExpression);

        return relation;
    }

}
