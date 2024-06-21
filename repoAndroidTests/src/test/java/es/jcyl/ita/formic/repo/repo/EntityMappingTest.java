package es.jcyl.ita.formic.repo.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Ignore;
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
        dbDev.withNumEntities(0).build(new StandardDatabase(db));
        SQLiteRepository relRepo = dbDev.getSQLiteRepository();

        // define an EntityMapping, dbRepo -> relRepo related through property FK_NAME
        EntityMapping mapping = new EntityMapping(relRepo, FK_NAME, PROPERTY_NAME);
        dbRepo.addMapping(mapping);

        /////////////////////////////////////
        // 1- create empty entity, and save it
        /////////////////////////////////////
        Entity relEntity = relRepo.newEntity();

        // link with main entity and save
        PropertyType fkProperty = mainEntity.getMetadata().getPropertyByName(FK_NAME);
        mainEntity.set(FK_NAME, ConvertUtils.convert(relEntity.getId(), fkProperty.type));
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

    /**
     * Create a dbEntity with a related dbEntity and save. Then replace the related entity
     * with another and check if it has been changed in the DB
     */
    @Test
    @Ignore
    public void testUpdateRelatedEntity() throws Exception {
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
        dbDev.withNumEntities(2).build(new StandardDatabase(db));
        SQLiteRepository relRepo = dbDev.getSQLiteRepository();

        List<Entity> relEntities = relRepo.listAll();
        Entity relEntity = relEntities.get(0);


        // define an EntityMapping, dbRepo -> relRepo related through property FK_NAME
        EntityMapping mapping = new EntityMapping(relRepo, FK_NAME, PROPERTY_NAME);
        dbRepo.addMapping(mapping);


        // link with main entity and save
        PropertyType fkProperty = mainEntity.getMetadata().getPropertyByName(FK_NAME);
        mainEntity.set(FK_NAME, ConvertUtils.convert(relEntity.getId(), fkProperty.type));
        mainEntity.set(PROPERTY_NAME, relEntity, true); // link related entity as transient
        dbRepo.save(mainEntity);
        dbRepo.clearCache();


        // Replace the related entity in the main entity and check if has been modified on DB
        // get another entity from relRepo and link with the mainEntity
        Entity relEntity2 = relEntities.get(1);
        mainEntity.set(FK_NAME, ConvertUtils.convert(relEntity2.getId(), fkProperty.type));
        dbRepo.save(mainEntity);
        dbRepo.clearCache();
        // get entity and check
        Entity dbActualEntity = dbRepo.findById(mainEntity.getId());
        Object expectedValue = ConvertUtils.convert(relEntity2.getId(), fkProperty.type);
        assertThat(dbActualEntity.get(FK_NAME), equalTo(expectedValue));

    }

    /**
     * Create a dbEntity with a related dbEntity, at first create the related entity empty to check
     * the new Entity insertion, get it again and update and modification has been applied.
     */
    @Test
    public void testDeleteRelatedEntity() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        Database db = DevDbBuilder.createDevSQLiteDb(ctx, "test.sqlite");

        String FK_NAME = "relId";
        String PROPERTY_NAME = "related";

        // create a database with some entities, and add a property to support the FK
        DevDbBuilder dbDev = new DevDbBuilder();

        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withBasicTypes(true).withRandomData()
                .addProperty(FK_NAME, String.class).build();
        dbDev.withNumEntities(1).withMeta(meta).build(db);

        SQLiteRepository dbRepo = dbDev.getSQLiteRepository();
        Entity mainEntity = dbRepo.listAll().get(0);

        // create relatedEntity repository
        dbDev = new DevDbBuilder();
        dbDev.withNumEntities(0).build(db);
        SQLiteRepository relRepo = dbDev.getSQLiteRepository();

        // define an EntityMapping, dbRepo -> relRepo related through property FK_NAME
        EntityMapping mapping = new EntityMapping(relRepo, FK_NAME, PROPERTY_NAME);
        dbRepo.addMapping(mapping);

        /////////////////////////////////////
        // 1- create empty entity, and save it
        /////////////////////////////////////
        Entity relEntity = Entity.newEmpty();
        PropertyType propertyRel = relRepo.getMeta().getProperties()[1];
        relEntity.set(propertyRel.name, ConvertUtils.convert(123, propertyRel.type));

        // link with main entity and save
        PropertyType fkProperty = mainEntity.getMetadata().getPropertyByName(FK_NAME);
        mainEntity.set(FK_NAME, ConvertUtils.convert(relEntity.getId(), fkProperty.type));
        mainEntity.set(PROPERTY_NAME, relEntity, true); // link related entity as transient
        dbRepo.save(mainEntity);
        dbRepo.clearCache();

        /////////////////////////////////////
        // 2- delete entity
        /////////////////////////////////////
        List<Entity> entities = relRepo.listAll();

        dbRepo.delete(mainEntity);
        dbRepo.clearCache();
        relRepo.clearCache();
        relEntity = (Entity) mainEntity.get(PROPERTY_NAME);

        entities = relRepo.listAll();
        relRepo.delete(relEntity);
        entities = relRepo.listAll();
        // check related entity has been removed
        Assert.assertFalse("The related entity should not exists!", relRepo.existsById(relEntity.getId()));
    }
}