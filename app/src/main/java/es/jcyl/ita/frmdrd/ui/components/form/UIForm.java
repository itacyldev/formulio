package es.jcyl.ita.frmdrd.ui.components.form;

import java.util.LinkedHashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.tab.UITab;


public class UIForm extends UIComponent {

    private EditableRepository repo;
    private String entityIdProperty = "params.entityId";
    private FormContext context;

    private Map<String, UITab> tabs = new LinkedHashMap<>();


    public UIForm() {
        this.setRendererType("form");
        this.setRenderChildren(true);
    }

    public Map<String, UITab> getTabs() {
        return tabs;
    }

    public void addTab(final UITab tab) {
        tab.setParent(this);
        this.addChild(tab);
        tabs.put(tab.getId(), tab);
    }

    public EditableRepository getRepo() {
        return repo;
    }

    public void setRepo(EditableRepository repo) {
        this.repo = repo;
    }


    public Object getEntityId(Context ctx) {
        return ctx.get(this.entityIdProperty);
    }

    @Override
    public void validate(Context context) {

    }

    public void applyChanges() {
        // copy data from input fields to the entity
    }

    /**
     * Loads related entity and sets it in the form context
     */
    public void loadEntity(Context globalCtx) {
        if (this.repo == null ) {
            return;
        }
        Object entityId = this.getEntityId(globalCtx);
        if(entityId == null){
            return;
        }
        Entity entity = this.repo.findById(entityId);
        // what if its null? throw an Exception?
        this.context.setEntity(entity);
    }

    public void initContext() {
        if (this.context == null) {
            this.context = new FormContext(this.getId(), this);
        }
    }

    public FormContext getContext() {
        return context;
    }

    public void save() {
        this.repo.save(this.context.getEntity());
    }

}