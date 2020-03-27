package es.jcyl.ita.frmdrd.ui.components.form;

import java.util.LinkedHashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.Context;
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
        this.context = new FormContext(this);
    }

    public Map<String, UITab> getTabs() {
        return tabs;
    }

    public void addTab(final UITab tab) {
        tab.setParent(this);
        this.addChild(tab);
        tabs.put(tab.getId(), tab);
    }

    /**
     * finds form child by its id
     *
     * @param id
     */
    public UIComponent getElement(String id) {
        return findChild(this.root, id);
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
        if (this.repo == null) {
            return;
        }
        Object entityId = this.getEntityId(globalCtx);
        if (entityId == null) {
            return;
        }
        Entity entity = this.repo.findById(entityId);
        // what if its null? throw an Exception?
        this.context.setEntity(entity);
    }

    public FormContext getContext() {
        return context;
    }

    public void save() {
        this.repo.save(this.context.getEntity());
    }

}