package groovy;
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

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.TemplateEngine;
import org.junit.Test;

import java.io.File;

import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TestGroovyScript {



//    private void renderTestButtons(final Context context, ViewGroup parent) {
//        Button groovyButton = new Button(context);
//        groovyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GroovyProcessor processor =
//                        new GroovyProcessor(context.getDir(
//                                "dynclasses", 0), context.getClassLoader());
//
//                BufferedReader reader = null;
//                StringBuffer sb = new StringBuffer();
//                try {
//                    reader = new BufferedReader(
//                            new InputStreamReader(context.getAssets().open(
//                                    "fibonacci.groovy")));
//
//                    // do reading, usually loop until end of file reading
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line);
//                        sb.append("\n");
//                    }
//                } catch (IOException e) {
//                    //log the exception
//                } finally {
//                    if (reader != null) {
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            //log the exception
//                        }
//                    }
//                }
//
//
//                GroovyProcessor.EvalResult result =
//                        (GroovyProcessor.EvalResult) processor.evaluate(sb.toString(), "fibonacci_groovy",
//                                lifecycle.getMainContext());
//
//                long init = System.nanoTime();
//                fib(100);
//                String execTime = (System.nanoTime() - init) / 1000000 + " ms";
//
//
//                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//        groovyButton.setText("test groovy");
//        parent.addView(groovyButton);

}
