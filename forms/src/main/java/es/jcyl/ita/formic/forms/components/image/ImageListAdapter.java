package es.jcyl.ita.formic.forms.components.image;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

public class ImageListAdapter extends BaseAdapter {

    private int itemLayout;
    private RenderingEnv env;

    private List<ImageGalleryItemWidget> imageItemViews;

    public ImageListAdapter(RenderingEnv renderingEnv) {
        imageItemViews = new ArrayList<>();
        itemLayout = R.layout.widget_imagegalleryitem;
        env = renderingEnv;
    }


    @Override
    public int getCount() {
        return imageItemViews.size();
    }

    @Override
    public UIImageGalleryItem getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null && imageItemViews.size() > 0) {
            view = imageItemViews.get(position);
        }

        return view;
    }

    public List<ImageGalleryItemWidget> getImageItemViews() {
        return this.imageItemViews;
    }

    public void addView(ImageGalleryItemWidget imageItemView) {
        this.imageItemViews.add(imageItemView);
    }
}
