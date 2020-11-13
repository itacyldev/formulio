package es.jcyl.ita.formic.repo.repo;

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.EntityMapping;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.DevFileBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.media.FileEntity;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class EntityMappingTest {


    /**
     * Creates some db entities and relates then with file Entities, the test checks the
     * listAll method will retrieve the related entities.
     */
    @Test
    public void testListAllRelatedEntities() throws Exception {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create access to existing database
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        String FK_NAME = "fileId";
        String PROPERTY_NAME = "file";

        // create a database with some entities, and add a propoerty to support the FK
        DevDbBuilder dbDev = new DevDbBuilder();

        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withBasicTypes(true).withRandomData()
                .addProperty(FK_NAME, String.class).build();

        dbDev.withNumEntities(10).withMeta(meta).build(new StandardDatabase(db));

        SQLiteRepository dbRepo = dbDev.getSQLiteRepository();

        // create a file repository
        DevFileBuilder fileDev = new DevFileBuilder();
        fileDev.withNumEntities(10).build();
        EditableRepository fileRepo = fileDev.getRepository();

        List<Entity> dbList = dbRepo.listAll();
        List<FileEntity> fileEntities = fileRepo.listAll();

        // define an EntityMapping, dbRepo -> fileRepo related with the property fileName
        EntityMapping mapping = new EntityMapping(fileDev.getRepository(), FK_NAME, PROPERTY_NAME);
        dbRepo.addMapping(mapping);

        // lets assign a File entity to each DBEntity
        int i = 0;
        for (Entity entity : dbList) {
            // link the entities by id
            entity.set(FK_NAME, fileEntities.get(i).getId());
            i++;
            dbRepo.save(entity);
        }
        // clear cache
        dbRepo.clearCache();

        // now retrieve all entities and make sure each entity has a related entity
        dbList = dbRepo.listAll();
        for (Entity entity : dbList) {
            assertThat(entity.get(FK_NAME), is(notNullValue()));
            assertThat(entity.get(PROPERTY_NAME), is(notNullValue()));
        }
    }

    /**
     * Create a dbEntity with a related dbEntity, at first create the related entity empty to check
     * the new Entity insertion, get it again and update and modification has been applied.
     */
    @Test
    public void testInsertAndUpdateRelatedEntity() throws Exception {
        // create access to existing database
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        String FK_NAME = "relId";
        String PROPERTY_NAME = "related";

        // create a database with some entities, and add a property to support the FK
        DevDbBuilder dbDev = new DevDbBuilder();

        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withBasicTypes(true).withRandomData()
                .addProperty(FK_NAME, String.class).build();
        dbDev.withNumEntities(1).withMeta(meta).build(new StandardDatabase(db));

        SQLiteRepository dbRepo = dbDev.getSQLiteRepository();
        Entity mainEntity = dbRepo.listAll().get(0);

        // create relatedEntity repository
        dbDev = new DevDbBuilder();
        dbDev.withNumEntities(1).build(new StandardDatabase(db));
        SQLiteRepository relRepo = dbDev.getSQLiteRepository();

        // define an EntityMapping, dbRepo -> relRepo related through property FK_NAME
        EntityMapping mapping = new EntityMapping(relRepo, FK_NAME, PROPERTY_NAME);
        dbRepo.addMapping(mapping);

        /////////////////////////////////////
        // 1- create empty entity, and save it
        /////////////////////////////////////
        Entity relEntity = Entity.newEmpty();

        // link with main entity and save
        mainEntity.set(PROPERTY_NAME, relEntity, true); // link related entity as transient
        dbRepo.save(mainEntity);
        dbRepo.clearCache();
        // get entity and check
        Entity dbActualEntity = dbRepo.findById(mainEntity.getId());

        assertThat(dbActualEntity.get(FK_NAME), is(notNullValue()));
        assertThat(dbActualEntity.get(PROPERTY_NAME), is(notNullValue()));
        Entity dbActualRelEntity = (Entity) dbActualEntity.get(PROPERTY_NAME);
        // the relEntity PK column and mainEntity FK column may have different data types,
        // convert before compare
        PropertyType fkProperty = mainEntity.getMetadata().getPropertyByName(FK_NAME);
        Object expectedValue = ConvertUtils.convert(dbActualRelEntity.getId(), fkProperty.type);
        assertThat(dbActualEntity.get(FK_NAME), equalTo(expectedValue));

        /////////////////////////////////////
        // 2- Update related entity, save it again and check if has been modified on cascade
        /////////////////////////////////////
        // get an entity from relRepo, modify it, then link with the mainEntity
        relEntity = relRepo.listAll().get(0);

        // modify related entity
        PropertyType[] props = relEntity.getMetadata().getProperties();
        PropertyType propToModify = props[props.length - 1];// last property
        expectedValue = ConvertUtils.convert("1", propToModify.type);
        relEntity.set(propToModify.name, expectedValue);

        // link with main entity and save
        mainEntity.set(FK_NAME, ConvertUtils.convert(relEntity.getId(), String.class));
        mainEntity.set(PROPERTY_NAME, relEntity, true); // link related entity as transient
        dbRepo.save(mainEntity);

        // now clear caches and get the related entity and check the value has changed
        relRepo.clearCache();
        dbActualRelEntity = relRepo.findById(relEntity.getId());
        Object actualValue = dbActualRelEntity.get(propToModify.name);
        assertThat(actualValue, equalTo(expectedValue));
    }

}