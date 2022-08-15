package es.jcyl.ita.formic.forms.components.image;

import android.content.Context;
import android.widget.GridView;
import android.widget.ImageView;

import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

public class ImageGalleryWidget extends Widget<UIImageGallery> {

    GridView gridView;


    public ImageGalleryWidget(Context context) {
        super(context);
    }

    public void setGridView(GridView gridView){
        this.gridView = gridView;
    }

    public void initialize(RenderingEnv env, Widget<UIAutoComplete> widget, ImageView arrowDropDown) {

    }
}
