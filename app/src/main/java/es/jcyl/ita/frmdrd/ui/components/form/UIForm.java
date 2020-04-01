package es.jcyl.ita.frmdrd.ui.components.form;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.context.impl.ViewStateHolder;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;


public class UIForm extends UIComponent {

    private FormContext context;
    private final ViewStateHolder memento;
    private EditableRepository repo;
    private String entityId = "params.entityId";
    private List<UIField> fields;


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

    public List<UIField> getFields() {
        if (this.fields == null) {
            this.fields = new ArrayList<UIField>();
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
    private void findFields(UIComponent root, List<UIField> fields) {
        if (root instanceof UIField) {
            fields.add((UIField) root);
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

    public EditableRepository getRepo() {
        return repo;
    }

    public void setRepo(EditableRepository repo) {
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
    public String getCompleteId() {
        return id;
    }
}