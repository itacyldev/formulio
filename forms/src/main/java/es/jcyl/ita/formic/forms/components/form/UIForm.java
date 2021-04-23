package es.jcyl.ita.formic.forms.components.form;

import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.ExpressionHelper;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.context.impl.ViewStateHolder;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;


public class UIForm extends UIGroupComponent implements FilterableComponent, ContextHolder {

    private ComponentContext context;
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

    public UIForm() {
        this.setRendererType("form");
        this.setRenderChildren(true);
        this.context = new ComponentContext(this);
        this.memento = new ViewStateHolder();
    }

    public List<UIInputComponent> getFields() {
        return this.fields;
    }

    public ComponentContext getContext() {
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

    public void setEntity(Entity currentEntity) {
        this.currentEntity = currentEntity;
        this.getContext().setEntity(currentEntity);
    }

    public Entity getEntity() {
        return currentEntity;
    }

    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        Set<ValueBindingExpression> expressions = super.getValueBindingExpressions();
        // If repo filter is defined, add binding expressions to establish dependencies
        if (this.filter != null) {
            expressions.addAll(ExpressionHelper.getExpressions(this.filter.getExpression()));
        }
        return expressions;
    }
    /**
     * ContextHolder interface
     */
    @Override
    public ViewContext getViewContext() {
        return null;
    }

    @Override
    public EntityContext getEntityContext() {
        return null;
    }


}