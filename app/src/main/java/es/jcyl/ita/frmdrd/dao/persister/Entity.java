package es.jcyl.ita.frmdrd.dao.persister;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class Entity implements Serializable {

    private EntityMeta metadata;
    private Object id;
    private Map<String, Object> properties;

    public EntityMeta getMetadata() {
        return metadata;
    }

    public void setMetadata(EntityMeta metadata) {
        this.metadata = metadata;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
