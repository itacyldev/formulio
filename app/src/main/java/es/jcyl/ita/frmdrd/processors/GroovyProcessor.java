package es.jcyl.ita.frmdrd.processors;

import android.util.Log;

import com.android.dx.dex.DexFormat;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;

import org.codehaus.groovy.control.BytecodeProcessor;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import dalvik.system.DexClassLoader;
import es.jcyl.ita.frmdrd.context.Context;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

public class GroovyProcessor {

    private static final String DEX_IN_JAR_NAME = "classes.dex";
    private static final String CONTEXT_VAR_NAME = "ctx";

    private final DexOptions dexOptions = new DexOptions();
    private final CfOptions cfOptions = new CfOptions();

    private File tmpDynamicFiles;
    private ClassLoader classLoader;

    public GroovyProcessor(File tmpDir, ClassLoader parent) {
        this.tmpDynamicFiles = tmpDir;
        this.classLoader = parent;


        dexOptions.targetApiLevel = DexFormat.API_NO_EXTENDED_OPCODES;
        cfOptions.positionInfo = PositionList.LINES;
        cfOptions.localInfo = true;
        cfOptions.strictNameCheck = true;
        cfOptions.optimize = false;
        cfOptions.optimizeListFile = null;
        cfOptions.dontOptimizeListFile = null;
        cfOptions.statistics = false;
    }


    public Object evaluate(String scriptText, String filename,
                           Context context) {
        //check if script file exists
        File scriptFile = new File(tmpDynamicFiles, filename);

        byte[] dalvikBytecode = null;

        if (scriptFile.exists()) {
            int size = (int) scriptFile.length();
            dalvikBytecode = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(scriptFile));
                buf.read(dalvikBytecode, 0, dalvikBytecode.length);
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            dalvikBytecode = loadDalvikByteCode(scriptText, filename,
                    context);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(scriptFile);
                fos.write(dalvikBytecode);
            } catch (IOException e) {

            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        Binding binding = new Binding();
        binding.setVariable(CONTEXT_VAR_NAME, context);

        Class scriptClass = loadScriptClass(dalvikBytecode, filename);
        Object result = null;

        if (Script.class.isAssignableFrom(scriptClass)) {
            Script script = null;
            try {
                script = (Script) scriptClass.newInstance();
                script.setBinding(binding);
            } catch (InstantiationException e) {
                Log.e("GroovyDroidShell", "Unable to create script", e);
            } catch (IllegalAccessException e) {
                Log.e("GroovyDroidShell", "Unable to create script", e);
            }
            result = script.run();

        }

        return result;
    }

    private byte[] loadDalvikByteCode(String scriptText, String filename,
                                      Context context) {
        byte[] dalvikBytecode = new byte[0];
        final DexFile dexFile = new DexFile(dexOptions);
        CompilerConfiguration config = new CompilerConfiguration();
        config.setBytecodePostprocessor(new BytecodeProcessor() {
            @Override
            public byte[] processBytecode(String s, byte[] bytes) {
                ClassDefItem classDefItem = CfTranslator.translate(s + ".class", bytes, cfOptions, dexOptions);
                dexFile.add(classDefItem);
                return bytes;
            }
        });

        GroovyClassLoader gcl = new GroovyClassLoader(this.classLoader, config);
        try {
            gcl.parseClass(scriptText, filename);
        } catch (Throwable e) {
            Log.e("GrooidShell", "Dynamic loading failed!", e);
        }

        try {
            dalvikBytecode = dexFile.toDex(new OutputStreamWriter(new ByteArrayOutputStream()), false);
        } catch (
                IOException e) {
            Log.e("GrooidShell", "Unable to convert to Dalvik", e);
        }

        return dalvikBytecode;
    }

    private Class loadScriptClass(byte[] dalvikBytecode, String className) {
        File tmpDex = new File(tmpDynamicFiles, UUID.randomUUID().toString() + ".jar");
        try {
            FileOutputStream fos = new FileOutputStream(tmpDex);
            JarOutputStream jar = new JarOutputStream(fos);
            JarEntry classes = new JarEntry(DEX_IN_JAR_NAME);
            classes.setSize(dalvikBytecode.length);
            jar.putNextEntry(classes);
            jar.write(dalvikBytecode);
            jar.closeEntry();
            jar.finish();
            jar.flush();
            fos.flush();
            fos.close();
            jar.close();
            DexClassLoader loader = new DexClassLoader(tmpDex.getAbsolutePath(), tmpDynamicFiles.getAbsolutePath(), null, classLoader);

            return loader.loadClass(className);
        } catch (Throwable e) {
            Log.e("DynamicLoading", "Unable to load class", e);
        } finally {
            tmpDex.delete();
        }
        return null;
    }
}
