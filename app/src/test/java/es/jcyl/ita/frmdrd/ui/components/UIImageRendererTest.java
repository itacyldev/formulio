package es.jcyl.ita.frmdrd.ui.components;
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.ImageView;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.image.UIImage;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
//@RunWith(RobolectricTestRunner.class)
@RunWith(AndroidJUnit4.class)
public class UIImageRendererTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @UiThreadTest
    @Test
    public void testStaticImage() throws IOException {
        File imgFile = RandomUtils.createRandomImageFile();

        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidDark);

        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);
        byte[] expected = FileUtils.readFileToByteArray(imgFile);
        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueExpression(exprFactory.create(imgFile.getAbsolutePath()));
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputFieldView) view).getInputView());
        ImageView imgView = (ImageView) ((InputFieldView) view).getInputView();
//        Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
//
//        Assert.assertNotNull(bitmap);
        System.out.println(imgView.isLayoutRequested());
        byte[] bytes = getByteArrayFromImageView(imgView);
        Assert.assertNotNull(bytes);
        writeImageToFile(imgView, "d:/tmp/image2.jpg");
        Assert.assertEquals(expected.length, bytes.length);
    }

    public static byte[] getByteArrayFromImageView(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static void writeImageToFile(ImageView imageView, String path) throws FileNotFoundException {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));
    }

}
