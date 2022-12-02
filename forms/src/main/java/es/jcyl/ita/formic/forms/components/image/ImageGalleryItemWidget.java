package es.jcyl.ita.formic.forms.components.image;

import android.content.Context;
import android.util.AttributeSet;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

public class ImageGalleryItemWidget extends Widget<UIImageGalleryItem> implements WidgetContextHolder {

    private ViewValueConverter converter;

    public ImageGalleryItemWidget(Context context) {
        super(context);
    }

    public ImageGalleryItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGalleryItemWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    ImageResourceView getImageResourceView() {
        ImageResourceView resourceView = this.findViewById(R.id.galleryitem_image);
        return resourceView;
    }

    @Override
    public void setup(RenderingEnv env) {
        super.setup(env);
    }


    @Override
    public Widget getWidget() {
        return this;
    }

    public ViewValueConverter getConverter() {
        return converter;
    }

    public void setConverter(ViewValueConverter converter) {
        this.converter = converter;
    }

    @Override
    public String getHolderId() {
        return null;
    }
}
