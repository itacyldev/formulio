package es.jcyl.ita.formic.repo.test.utils;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.RandomStringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.db.sqlite.meta.types.SQLiteType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RandomUtils {


    public static float randomFloat(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }


    public static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static long randomLong(int min, int max) {
        return new Long(randomInt(min, max));
    }

    public static boolean randomBoolean() {
        return randomInt(0, 1) == 0;
    }


    public static Date randomDate() {
        Date now = new Date();
        long startMillis = now.getTime();
        long aDay = TimeUnit.DAYS.toMillis(30);
        long endMillis = (new Date(startMillis + aDay)).getTime();
        return randomDate(now, new Date(endMillis));
    }

    public static Date randomDate(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom.current().nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }

    public static String randomString(int length) {
        return RandomStringUtils.randomAlphabetic(length).toUpperCase();
    }


    public static String[] randomNouns(int size) {
        String[] nouns = new String[size];
        for (int i = 0; i < size; i++) {
            int pos = randomInt(0, _nouns.length - 1);
            nouns[i] = _nouns[pos];
        }
        return nouns;
    }

    public static Geometry randomGeometry(long srid) {
        // first attempt to tackle geo problem
        String wkt = "MULTIPOLYGON(((384088.71 4609310.54, 384079.16 4609312.93, 384074.39 4609314.92, 384073.34 4609312.8, 384063.48 4609317.86, 383997.240002 4609487.11, 383996.040002 4609490.17, 383991.920002 4609500.61, 383949.040001 4609609.410003, 383968.130002 4609616.080003, 384052.110005 4609647.800002, 384074.040005 4609656.540002, 384165.550007 4609690.540002, 384197.450007 4609702.770001, 384228.400008 4609714.870002, 384241.910008 4609719.000002, 384251.160008 4609684.720003, 384262.490008 4609642.740004, 384274.430007 4609601.890005, 384279.390007 4609585.920005, 384284.410007 4609569.720004, 384286.420007 4609562.210004, 384293.990008 4609533.920003, 384297.150008 4609523.600003, 384295.210008 4609522.740003, 384294.620008 4609519.760003, 384297.000008 4609513.600003, 384300.580008 4609503.670003, 384300.580008 4609496.910003, 384300.390008 4609488.170003, 384293.820008 4609477.240003, 384283.480007 4609467.300003, 384252.640007 4609436.310003, 384236.920007 4609420.420003, 384229.160007 4609415.450003, 384227.370007 4609412.270003, 384224.390007 4609407.510003, 384209.270007 4609392.800003, 384184.400006 4609370.350002, 384165.900006 4609354.650002, 384157.150006 4609349.490003, 384105.420006 4609319.290002, 384088.71 4609310.54)))";
        return new Geometry(wkt, GeometryType.MULTIPOLYGON, srid);

    }

    public static <T> T[] randomObjectArray(int size, Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            array[i] = (T) randomObject(clazz);
        }
        return array;
    }

    public static Object randomObject(Class clazz) {
        switch (clazz.getName()) {
            case "java.lang.String":
                return randomString(10);
            case "java.lang.Boolean":
                return randomBoolean();
            case "java.lang.Integer":
                return randomInt(0, 99999);
            case "java.lang.Long":
                return randomLong(0, 99999);
            case "java.lang.Double":
                return randomDouble(0, 99999);
            case "java.lang.Float":
                return randomFloat(0, 99999);
            case "java.util.Date":
                return randomDate();
            case "es.jcyl.ita.crtrepo.meta.types.ByteArray":
                String str = randomString(10);
                byte[] bs = str.getBytes();
                return new ByteArray(bs);
            case "es.jcyl.ita.crtrepo.meta.types.Geometry":
                return randomGeometry(25830);
            default:
                throw new RuntimeException("Unsupported clazzzzzzz!: " + clazz);
        }
    }


    public static Object randomDouble(int min, int max) {
        return new Double(randomFloat(min, max));
    }

    public static Class randomType() {
        int type = randomInt(0, 6);
        switch (type) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Boolean.class;
            case 3:
                return Date.class;
            case 4:
                return Double.class;
            case 5:
                return Float.class;
            case 6:
                return ByteArray.class;
        }
        return String.class;
    }

    public static Class randomBasicType() {
        int type = randomInt(0, 2);
        switch (type) {
            case 0:
                return String.class;
            case 1:
                return Long.class;
            case 2:
                return Double.class;
        }
        return String.class;
    }

    public static SQLiteType randomBasicSQLiteType() {
        int type = randomInt(0, 3); // BLOB not included
        return SQLiteType.getType(type);
    }

    public static SQLiteType randomSQLiteType() {
        int type = randomInt(0, 4);
        return SQLiteType.getType(type);
    }

    private static final String[] _nouns = new String[]{"belt", "blouse", "boots", "cap", "cardigan", "coat", "dress", "gloves", "hat", "jacket", "jeans", "jumper", "mini", "skirt", "overalls", "overcoat", "pijamas", "pants", "pantyhose", "raincoat", "scarf", "shirt", "shoes", "shorts", "skirt", "slacks", "slippers", "socks", "stockings", "suit", "sweat", "shirt", "sweater", "sweatshirt", "tie", "trousers", "underclothes", "underpants", "undershirt", "vest", "ankle", "arm", "back", "beard", "blood", "body", "bone", "brain", "cheek", "chest", "chin", "ear", "ears", "elbow", "eye", "eyes", "face", "feet", "finger", "fingers", "flesh", "foot", "hair", "hand", "hands", "head", "heart", "hip", "knee", "knees", "leg", "legs", "lip", "moustache", "mouth", "muscle", "nail", "neck", "nose", "shoulder", "shoulders", "skin", "stomach", "teeth", "throat", "thumb", "thumbs", "toe", "toes", "tongue", "tooth", "wrist", "alligator", "ant", "bear", "bee", "bird", "camel", "cat", "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck", "eagle", "elephant", "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster", "hippopotamus", "horse", "kangaroo", "kitten", "leopard", "lion", "lizard", "lobster", "monkey", "octopus", "ostrich", "otter", "owl", "oyster", "panda", "parrot", "pelican", "pig", "pigeon", "porcupine", "puppy", "rabbit", "rat", "reindeer", "rhinoceros", "rooster", "scorpion", "seal", "shark", "sheep", "shrimp", "snail", "snake", "sparrow", "spider", "squid", "squirrel", "swallow", "swan", "tiger", "toad", "tortoise", "turtle", "vulture", "walrus", "weasel", "whale", "wolf", "zebra"};


    public static File createRandomImageFile() {
        // Image file dimensions
        int width = 640, height = 320;
        File baseFolder = new File(System.getProperty("java.io.tmpdir"));
        String name = randomString(20);
        return createRandomImageFile(baseFolder, name, width, height);
    }

    public static File createRandomImageFile(File baseFolder, String name) {
        // Image file dimensions
        int width = 640, height = 320;
        return createRandomImageFile(baseFolder, name, width, height);
    }

    /**
     * Creates a random pixel image in the given folder
     *
     * @param baseFolder
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static File createRandomImageFile(File baseFolder, String name, int width, int height) {
        //https://www.geeksforgeeks.org/image-processing-java-set-7-creating-random-pixel-image/
        // Create buffered image object
        BufferedImage img = null;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // file object
        File f = null;

        // create random values pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (int) (Math.random() * 256); //generating
                int r = (int) (Math.random() * 256); //values
                int g = (int) (Math.random() * 256); //less than
                int b = (int) (Math.random() * 256); //256
                int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel
                img.setRGB(x, y, p);
            }
        }
        File outputFile = new File(baseFolder, name + ".png");
        // write image
        try {
            ImageIO.write(img, "png", outputFile);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to write the random image", e);
        }
        return outputFile;
    }

}
