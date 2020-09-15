package es.jcyl.ita.frmdrd.ui.components.form;

import org.mini2Dx.beanutils.ConvertUtils;

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
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.UIGroupComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.validation.Validator;
import es.jcyl.ita.frmdrd.validation.ValidatorException;
import es.jcyl.ita.frmdrd.view.widget.InputWidget;
import es.jcyl.ita.frmdrd.view.ViewConfigException;


public class UIForm extends UIGroupComponent implements FilterableComponent {

    private FormContext context;
    private final ViewStateHolder memento;
    private String entityId = "params.entityId";
    private Entity currentEntity;
    private String onValidate; // js function to call on validation
    private boolean readOnly;
    /** filterable component **/
    private Repository repo;
    private Filter filter;
    private String[] mandatoryFilters;


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
        return UIComponentHelper.findChild(this, id);
    }

    public List<UIInputComponent> getFields() {
        return this.fields;
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
            if (currentEntity == null) {
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

        InputWidget fieldView = viewContext.findInputFieldViewById(field.getId());
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
            Map result = srcEngine.execute(this.id, getContext(), this.onValidate);
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
        return mandatoryFilters;
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFilters) {
        this.mandatoryFilters = mandatoryFilters;
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

}