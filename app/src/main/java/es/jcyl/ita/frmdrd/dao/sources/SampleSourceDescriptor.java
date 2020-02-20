package es.jcyl.ita.frmdrd.dao.sources;

import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.dao.persister.Entity;
import es.jcyl.ita.frmdrd.dao.persister.EntityMeta;

public class SampleSourceDescriptor implements SourceDescriptor {
    @Override
    public List<Entity> getEntities() {

        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Entity newEntity = newEntity("entidad_" + i);
            entities.add(newEntity);
        }

        return entities;
    }

    private Entity newEntity(String name) {
        Entity entity = new Entity();
        entity.setId(name);

        EntityMeta metadata = new EntityMeta();
        metadata.setColumnNames(new String[]{"campo1", "campo2", "campo3",
                "campo4"});
        metadata.setColumnTypes(new String[]{"TEXT", "BOOLEAN", "TEXT", "DATE"});
        metadata.setIdColumns(new String[]{"campo1", "campo2", "campo3",
                "campo4"});
        entity.setMetadata(metadata);

        Map<String, Object> properties = new HashMap<>();
        properties.put("campo1", RandomStringUtils.randomAlphanumeric(8));
        properties.put("campo2", Boolean.TRUE);

        properties.put("campo3", RandomStringUtils.randomAlphanumeric(8));
        properties.put("campo4", new Date());

        entity.setProperties(properties);


        return entity;
    }
}
