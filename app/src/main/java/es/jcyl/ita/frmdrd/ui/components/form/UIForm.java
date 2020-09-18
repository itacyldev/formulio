package es.jcyl.ita.frmdrd.ui.components.form;

import java.util.List;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.context.FormContextHelper;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.context.impl.ViewStateHolder;
import es.jcyl.ita.frmdrd.repo.EntityRelation;
import es.jcyl.ita.frmdrd.scripts.ScriptEngine;
import es.jcyl.ita.frmdrd.ui.components.FilterableComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.UIGroupComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.validation.Validator;
import es.jcyl.ita.frmdrd.validation.ValidatorException;
import es.jcyl.ita.frmdrd.view.widget.InputWidget;


public class UIForm extends UIGroupComponent implements FilterableComponent {

    private FormContext context;
    private final ViewStateHolder memento;
    private String entityId = "params.entityId";
    private Entity currentEntity;
    private String onValidate; // js function to call on validation
    private boolean readOnly;
    /**
     * filterable component
     **/
    private Repository repo;
    private Filter filter;
    private String[] mandatoryFilters;

    /**
     * Entities loaded by inner components that create new entities that are related to
     * current form entity.
     */
    private List<EntityRelation> entityRelations;

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


    public void setCurrentEntity(Entity currentEntity) {
        this.currentEntity = currentEntity;
        this.getContext().setEntity(currentEntity);
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }

    public List<EntityRelation> getEntityRelations() {
        return entityRelations;
    }

    public void setEntityRelations(List<EntityRelation> entityRelations) {
        this.entityRelations = entityRelations;
    }
}