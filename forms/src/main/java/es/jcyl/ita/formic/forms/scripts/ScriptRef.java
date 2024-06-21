package es.jcyl.ita.formic.forms.scripts;
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

import org.mozilla.javascript.Script;

import java.lang.ref.WeakReference;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ScriptRef {
    private final String filePath;
    private final ScriptType type;
    private final String source;
    // file that references current script
    private final String origin;
    private WeakReference<Script> script;

    public enum ScriptType {INLINE, FILE}

    private ScriptRef(ScriptType type, String source, String filePath, String origin) {
        this.type = type;
        this.source = source;
        this.filePath = filePath;
        this.origin = origin;
    }

    public boolean isSourceFile(){
        return type == ScriptType.FILE;
    }

    public static ScriptRef createInlineScriptRef(String source, String origin) {
        return new ScriptRef(ScriptType.INLINE, source, null, origin);
    }

    public static ScriptRef createSourceFileScriptRef(String path, String origin) {
        return new ScriptRef(ScriptType.FILE, null, path, origin);
    }

    public String getFilePath() {
        return filePath;
    }

    public ScriptType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getOrigin() {
        return origin;
    }

    public void bindScript(Script script) {
        this.script = new WeakReference<>(script);
    }

    public Script boundScript() {
        return (this.script == null) ? null : this.script.get();
    }

    @Override
    public String toString() {
        return "ScriptRef{" +
                "filePath='" + filePath + '\'' +
                ", type=" + type +
                ", source='" + source + '\'' +
                '}';
    }
}
