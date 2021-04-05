package es.jcyl.ita.formic.repo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class Entity<S extends EntitySource, M extends EntityMeta> {

    private M metadata;
    /**
     * Persistence origin used to retrieve/STORE this entity
     */
    private S source;
    protected boolean validateProperties = true;
    private Map<String, Object> properties = new HashMap<>();
    // non persistable properties
    private Map<String, Object> transientProps = new HashMap<>();


    public Entity(S source, M meta) {
        this(source, meta, null);
    }

    public Entity(S source, M meta, Object id) {
        this.source = source;
        this.metadata = meta;
        if (meta != null) {
            properties = new HashMap<String, Object>(this.metadata.getProperties().length);
        }
        if (id != null) {
            setId(id);
        }
    }

    public void set(String prop, Object value, boolean isTransient) {
        // TODO: include watcher for reactive flow implementation
        if (isTransient) {
            if (this.transientProps.isEmpty()) {
                this.transientProps = new HashMap<>();
            }
            this.transientProps.put(prop, value);
        } else {
            if (validateProperties) {
                validate(prop, value);
            }
            this.properties.put(prop, value);
        }
    }

    public void set(String prop, Object value) {
        this.set(prop, value, false);
    }

    public Object get(String prop) {
        Object obj = this.properties.get(prop);
        return (obj != null) ? obj : this.transientProps.get(prop);
    }

    public M getMetadata() {
        return metadata;
    }

    public void setMetadata(M metadata) {
        this.metadata = metadata;
    }

    public Object getId() {
        String[] ids = this.metadata.getIdPropertiesName();
        // for the moment lets suppose we're working just with single pk's
        if (ids != null && ids.length > 0) {
            return this.properties.get(ids[0]);
        } else {
            return null;
        }
    }

    /**
     * Validate value againts the schema
     */
    private void validate(String property, Object value) {
        // TODO: the modification of the data can be restricted in different ways and levels,
        //  persistence layer imposes restrictions about data or semantic limitations like modifications
        //  of the pk. But user/project specific configuration can limit the possibility of modification
        //  in some of the fields.
        // This validation has to be broken down in different levels, pretty much like JSF-validation: presentation/persitence.

        // make sure the property to be modified exists in the metadata
        if (!this.metadata.containsProperty(property)) {
            throw new RepositoryException(String.format("The given property [%s] doesn't exists in metadata for entity [%s].",
                    property, this.metadata.getName()));
        }
        // and it has the proper type
        if (value == null) {
            return;// no need to check type
        }
        PropertyType propertyType = this.metadata.getPropertyByName(property);
        if (!propertyType.type.isInstance(value)) {
            throw new RepositoryException(String.format("Error while trying to set property [%s]. " +
                            "The given property value type [%s] is not " +
                            "compatible with the property type definition [%s].",
                    property, value.getClass().getName(), propertyType.type.getName()));
        }
    }

    public void setId(Object id) {
        String[] ids = this.metadata.getIdPropertiesName();
        // for the moment lets suppose we're working just with single pk's
        if (ids != null && ids.length > 0) {
            this.properties.put(ids[0], id);
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }


    public String getType() {
        // TODO: do we need a different attribute from the one used by the pesistence layer?
        return this.metadata.getName();
    }

    public EntitySource getSource() {
        return source;
    }

    public void setSource(S source) {
        this.source = source;
    }

    public static Entity newEmpty() {
        EntityMeta meta = new EntityMeta("empty", new PropertyType[0], new String[0]);
        Entity entity = new Entity(null, meta, null);
        entity.validateProperties = false;
        return entity;
    }

    @Override
    public String toString() {
        if (this.metadata == null) {
            return this.getClass().getName() + "@[" + this.getId() + "]";
        } else {
            String pk = (!this.metadata.hasIdProperties()) ? "NoPK" :
                    String.format("%s=%s", Arrays.toString(
                            metadata.getIdPropertiesName()).replaceAll("[\\[\\]]", ""), this.getId());
            return metadata.getName() + "@[" + pk + "]";
        }
    }

    public void clear() {
        this.properties.clear();
        this.transientProps.clear();
    }

}
