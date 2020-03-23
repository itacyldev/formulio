package es.jcyl.ita.frmdrd.reactivity;

public class DAGNode {

    public enum TYPE {SOURCE, FIELD}

    private String id;
    private TYPE type;

    public DAGNode() {
    }

    public DAGNode(String id, TYPE type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }
}
