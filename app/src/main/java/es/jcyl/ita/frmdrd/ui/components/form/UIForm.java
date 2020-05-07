package es.jcyl.ita.frmdrd.ui.components.form;

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.context.FormContextHelper;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.context.impl.ViewStateHolder;
import es.jcyl.ita.frmdrd.forms.FormException;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.scripts.ScriptEngine;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.validation.Validator;
import es.jcyl.ita.frmdrd.validation.ValidatorException;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewConfigException;


public class UIForm extends UIComponent implements FilterableComponent {

    private FormContext context;
    private final ViewStateHolder memento;
    private Repository repo;
    private String entityId = "params.entityId";
    private Entity currentEntity;
    private List<UIInputComponent> fields;
    private Filter filter;
    private String onValidate; // js function to call on validation
    private String label;
    private boolean readOnly;

    public UIForm() {
        this.setRendererType("form");
        this.setRenderChildren(true);
        this.context = new FormContext(this);
        this.memento = new ViewStateHolder();
    }

    /**
     * finds form child by its id
     *
     * @param id
     */
    public UIComponent getElement(String id) {
        return findChild(this, id);
    }

    /**
     * Recursively go over the component tree storing forms in the given array
     *
     * @param root
     */
    private UIComponent findChild(UIComponent root, String id) {
        if (root.getId().equalsIgnoreCase(id)) {
            return root;
        } else {
            if (root.hasChildren()) {
                for (UIComponent c : root.getChildren()) {
                    UIComponent found = findChild(c, id);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    public List<UIInputComponent> getFields() {
        if (this.fields == null) {
            this.fields = new ArrayList<UIInputComponent>();
            findFields(this, this.fields);
        }
        return this.fields;
    }

    /**
     * Recursively go over the component tree storing fields in the given array
     *
     * @param root
     * @param fields
     */
    private void findFields(UIComponent root, List<UIInputComponent> fields) {
        if (root instanceof UIInputComponent) {
            fields.add((UIInputComponent) root);
        } else {
            if (root.hasChildren()) {
                for (UIComponent c : root.getChildren()) {
                    findFields(c, fields);
                }
            }
        }
    }

    public FormContext getContext() {
        return context;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "Form:" + this.getId();
    }

    public void saveViewState() {
        memento.saveState(this.getContext().getViewContext());
    }

    public void restoreViewState() {
        memento.restoreState(this.getContext().getViewContext());
    }

    @Override
    public String getAbsoluteId() {
        return id;
    }

    public String getOnValidate() {
        return onValidate;
    }

    public void setOnValidate(String onValidate) {
        this.onValidate = onValidate;
    }

    /**
     * Load current context
     *
     * @param globalCtx
     */
    public void load(Context globalCtx) {
        Repository repo = this.getRepo();
        if (repo == null) {
            return;
        }
        // load current form entity
        Object entityId = getEntityIdFromContext(globalCtx);
        if (entityId == null) {
            // create empty entity
            if (isReadOnly()) {
                throw new ViewConfigException(String.format("The form [%s] is readOnly, no new " +
                        "entities can be created. Use an EditableRepository or set the readOnly " +
                        "attribute to false.", this.getId()));
            }
            currentEntity = new Entity(repo.getSource(), repo.getMeta());
        } else {
            // what if its null? throw an Exception?
            currentEntity = findEntity(globalCtx, entityId);
            if(currentEntity == null){
                throw new FormException("No entity found with the id: " + entityId);
            }
        }
        this.getContext().setEntity(currentEntity);
    }

    /**
     * Loads current entity depending on the repository type.
     *
     * @param entityId
     * @return
     */
    private Entity findEntity(Context globalContext, Object entityId) {
        if (this.repo instanceof EditableRepository) {
            // convert the entity Id if needed
            Object pk = convertIfNeeded(repo.getMeta(), entityId);
            return ((EditableRepository) repo).findById(pk);
        } else {
            // if there's a filter defined in the form, use the filter to find the entity
            if (this.filter == null) {
                throw new ViewConfigException(String.format("You are using a readOnly repository in " +
                        "form [%s] but no repofilter has been configured to define the query to " +
                        "find the entity from its id. Add a repofilter tag with an eq condition" +
                        " with the expression ${params.entityId}.", this.getId()));
            }
            Filter f = setupFilter(globalContext);
            List<Entity> list = this.repo.find(f);
            if (list.size() == 0) {
                throw new ViewConfigException(String.format("No entity found with the filter [%s], " +
                        "check the repofilter defined in the form [%s].", f.toString(), this.id));
            }
            return list.get(0);
        }
    }

    /**
     * Checks the type of the value received as entity Id against he meta
     *
     * @param meta
     * @param entityId
     * @return
     */
    private Object convertIfNeeded(EntityMeta meta, Object entityId) {
        //
        if (!meta.hasIdProperties()) {
            return entityId;
        } else {
            // TODO: multicolumn support
            Class pkType = meta.getIdProperties()[0].getType();
            if (entityId.getClass() == pkType) {
                return entityId;
            } else {
                return ConvertUtils.convert(entityId, pkType);
            }
        }
    }


    private Filter setupFilter(Context context) {
        Filter f = FilterHelper.createInstance(repo);
        FilterHelper.evaluateFilter(context, this.filter, f);
        return f;
    }

    private Object getEntityIdFromContext(Context context) {
        String entityIdProp = this.getEntityId();
        Object entityId;
        try {
            entityId = context.get(entityIdProp);
        } catch (Exception e) {
            throw new FormException(String.format("An error occurred while trying to obtain the " +
                    "entity id from params context for form [%s]." +
                    " It seems the property 'entityId' is not properly set: [%s].", this.getId(), entityIdProp));
        }
        return entityId;
    }

    public boolean isReadOnly() {
        return this.readOnly || !(this.repo instanceof EditableRepository);
    }


    public boolean isVisible(UIInputComponent field) {
        FormViewContext viewContext = context.getViewContext();

        InputFieldView fieldView = viewContext.findInputFieldViewById(field.getId());
        return fieldView.isVisible();
    }

    public boolean validate(UIInputComponent field) {
        FormViewContext viewContext = context.getViewContext();

        // get user input using view context and check all validators.
        String value = viewContext.getString(field.getId());
        boolean valid = true;
        for (Validator validator : field.getValidators()) {
            try {
                if (isVisible(field)) {
                    validator.validate(context, field, value);
                }
            } catch (ValidatorException e) {
                // get the error and put it in form context
                FormContextHelper.setMessage(context, field.getId(), e.getMessage());
                valid = false;
            }
        }
        // call validation function
        if (this.onValidate != null) {
            ScriptEngine srcEngine = ScriptEngine.getInstance();
            // TODO: we have to pass a combination of globalContext + formContext
            Map result = srcEngine.execute(this.id, getContext(), this.onValidate, null);
            if (result.containsKey("error")) {
                throw new ValidatorException((String) result.get("message"));
            }
        }
        return valid;
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return new String[0];
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}