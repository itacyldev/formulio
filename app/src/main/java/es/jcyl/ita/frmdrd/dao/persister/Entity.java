package es.jcyl.ita.frmdrd.dao.persister;

import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class Entity {

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
