package es.jcyl.ita.formic.forms.components;
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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.WidgetTestUtils;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
//@RunWith(AndroidJUnit4.class)
@Ignore("Mayor refactoring pending")
public class UIImageRendererTest {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderer renderHelper = new ViewRenderer();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
//    @UiThreadTest
    @Test
    public void testStaticImageRender() throws IOException {
        File imgFile = RandomUtils.createRandomImageFile();
        byte[] imgBytes = FileUtils.readFileToByteArray(imgFile);
        Bitmap expectedBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

        RenderingEnv env = createRenderingEnv();
        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueExpression(exprFactory.create(imgFile.getAbsolutePath()));
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputWidget) view).getInputView());
        ImageView imgView = (ImageView) ((InputWidget) view).getInputView();

        // assert bitmap content
        Drawable imageViewDrawable = imgView.getDrawable();
        BitmapDrawable imageViewBitmap = (BitmapDrawable) imageViewDrawable;
        WidgetTestUtils.assertEquals(expectedBitmap, imageViewBitmap.getBitmap());
    }

    @Test
    public void testNotExistingImage() throws IOException {
        Bitmap notFoundImg = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), R.drawable.image_not_found);

        RenderingEnv env = createRenderingEnv();
        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueExpression(exprFactory.create("not_existing_image_path"));
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputWidget) view).getInputView());
        ImageView imgView = (ImageView) ((InputWidget) view).getInputView();

        // assert bitmap content
        Drawable imageViewDrawable = imgView.getDrawable();
        BitmapDrawable imageViewBitmap = (BitmapDrawable) imageViewDrawable;
        WidgetTestUtils.assertEquals(notFoundImg, imageViewBitmap.getBitmap());
    }

    @Test
    public void testNoImage() throws IOException {
        Bitmap noImage = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), R.drawable.no_image);

        RenderingEnv env = createRenderingEnv();
        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueExpression(null); // no image
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputWidget) view).getInputView());
        ImageView imgView = (ImageView) ((InputWidget) view).getInputView();

        // assert bitmap content
        Drawable imageViewDrawable = imgView.getDrawable();
        BitmapDrawable imageViewBitmap = (BitmapDrawable) imageViewDrawable;
        WidgetTestUtils.assertEquals(noImage, imageViewBitmap.getBitmap());
    }

    private RenderingEnv createRenderingEnv() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidDark);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = RenderingEnvFactory.getInstance().create(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        FormActivity mockFormActivity = mock(FormActivity.class);
        env.setFormActivity(mockFormActivity);
        return env;
    }

    //    @UiThreadTest
    @Test
    public void testStaticB64ImageRender() throws IOException {
        File imgFile = RandomUtils.createRandomImageFile();
        byte[] imgBytes = FileUtils.readFileToByteArray(imgFile);
        Bitmap expectedBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        String encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);

        RenderingEnv env = createRenderingEnv();

        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueConverter("b64Image");
        uiImg.setValueExpression(exprFactory.create(encodedImage));
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputWidget) view).getInputView());
        ImageView imgView = (ImageView) ((InputWidget) view).getInputView();

        // assert bitmap content
        Drawable imageViewDrawable = imgView.getDrawable();
        BitmapDrawable imageViewBitmap = (BitmapDrawable) imageViewDrawable;
        WidgetTestUtils.assertEquals(expectedBitmap, imageViewBitmap.getBitmap());
    }

    /**
     * Test the image rendering from a byteArray object.
     * Creates a random image and stores it in a context simulating entityContext. then the image
     * is configure with a binding expression entity.imageProperty
     *
     * @throws IOException
     */
    @Test
    public void testStaticByteArrayImageRender() throws IOException {
        File imgFile = RandomUtils.createRandomImageFile();
        byte[] imgBytes = FileUtils.readFileToByteArray(imgFile);
        Bitmap expectedBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        ByteArray bArray = new ByteArray(imgBytes);

        RenderingEnv env = createRenderingEnv();

        BasicContext entityContext = new BasicContext("entity");
        entityContext.put("imageProperty", bArray);
        env.getWidgetContext().addContext(entityContext);

        // create
        UIImage uiImg = new UIImage();
        uiImg.setValueConverter("byteArrayImage");
        uiImg.setValueExpression(exprFactory.create("${entity.imageProperty}"));
        View view = renderHelper.render(env, uiImg);

        // check there's an ImgView
        Assert.assertNotNull(((InputWidget) view).getInputView());
        ImageView imgView = (ImageView) ((InputWidget) view).getInputView();

        // assert bitmap content
        Drawable imageViewDrawable = imgView.getDrawable();
        BitmapDrawable imageViewBitmap = (BitmapDrawable) imageViewDrawable;
        WidgetTestUtils.assertEquals(expectedBitmap, imageViewBitmap.getBitmap());
    }
}
