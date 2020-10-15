package es.jcyl.ita.formic.repo.media;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.jcyl.ita.formic.repo.builders.DevFileBuilder;
import es.jcyl.ita.formic.repo.media.query.FileEntityExpression;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.query.BaseFilter;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class FileRepositoryTest {


    @Test
    public void testInsertEntity() throws IOException {
        DevFileBuilder dev = new DevFileBuilder();
        dev.build();
        FileRepository repo = dev.getRepository();

        FileEntity fe = DevFileBuilder.buildEntity();
        repo.save(fe);

        // check the image is where expected
        MatcherAssert.assertThat(fe.get("id"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(fe.get("absolutePath"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(fe.get("name"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(fe.get("size"), CoreMatchers.notNullValue());
        File f = (File) fe.get("file");
        MatcherAssert.assertThat(f, FileMatchers.anExistingFile());
        Assert.assertNotNull(FileUtils.readFileToByteArray(f));
    }


    /**
     * Gets and existing entity from the repository, update its content and check no new entities
     * has been created and the reference entity has been updated.
     *
     * @throws IOException
     */
    @Test
    public void testUpdateEntity() throws IOException, InterruptedException {
        // create a one-entity repo
        DevFileBuilder dev = new DevFileBuilder();
        dev.withNumEntities(1).build();
        FileRepository repo = dev.getRepository();

        FileEntity entity = repo.listAll().get(0);

        File fEntity = (File) entity.get("file");
        long lastModified = fEntity.lastModified();
        // get the existing entity and modify it
        byte[] newContent = FileUtils.readFileToByteArray(RandomUtils.createRandomImageFile());
        entity.set("content", new ByteArray(newContent));
        Thread.sleep(300);
        repo.save(entity);
        // make sure no new entity has been created
        Assert.assertEquals(1, repo.listAll().size());
        // and current entity file has been modified
        File nFile = new File(fEntity.getParent(), fEntity.getName());
        MatcherAssert.assertThat(nFile.lastModified(), Matchers.greaterThan(lastModified));
    }

    @Test
    public void testListAll() {
        DevFileBuilder dev = new DevFileBuilder();
        int numExpected = RandomUtils.randomInt(0, 15);
        dev.withNumEntities(numExpected).build();

        FileRepository repo = dev.getRepository();
        List<FileEntity> fileEntities = repo.listAll();

        Assert.assertEquals(numExpected, fileEntities.size());
    }

    /**
     * Creates a repository with some entities starting with a prefix and tries to retrive then
     * from the repo using a regular expression filter.
     */
    @Test
    public void testListWithFilter() {
        DevFileBuilder dev = new DevFileBuilder();
        String[] randomNames = RandomUtils.randomObjectArray(15, String.class);
        // get 5 of then and put a prefix on the name
        int[] pos = new int[]{1, 4, 6, 8, 11};
        int numExpected = pos.length;
        for (int p : pos) {
            randomNames[p] = "AAAR-" + randomNames[p];
        }
        dev.withNames(randomNames).build();

        String expression = "AAAR-.*";
        FileEntityExpression expr = new FileEntityExpression(expression);
        BaseFilter<FileEntityExpression> filter = new BaseFilter<FileEntityExpression>();
        filter.setExpression(expr);

        FileRepository repo = dev.getRepository();
        List<FileEntity> fileEntities = repo.find(filter);

        Assert.assertEquals(numExpected, fileEntities.size());
    }

    @Test
    public void testDelete() {
        DevFileBuilder dev = new DevFileBuilder();
        int numExpected = RandomUtils.randomInt(0, 15);
        dev.withNumEntities(numExpected).build();

        FileRepository repo = dev.getRepository();
        List<FileEntity> fileEntities = repo.listAll();

        // delete one
        FileEntity entity = fileEntities.get(0);
        repo.delete(entity);
        numExpected -= 1;
        // check numEntites
        fileEntities = repo.listAll();
        Assert.assertEquals(numExpected, fileEntities.size());
    }

}