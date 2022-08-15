package es.jcyl.ita.formic.forms.components.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

public class ImageListAdapter extends BaseAdapter {

    private List<UIImageGalleryItem> imageItems ;
    private Context mContext;
    private CompositeContext globalContext;
    private int itemLayout;

    public ImageListAdapter(RenderingEnv env, CompositeContext context, int resource, int textViewId, UIImageGallery component) {
        imageItems = new ArrayList<>();
        mContext = env.getAndroidContext();
        globalContext = context;
        itemLayout = R.layout.widget_imagegalleryitem;
    }


    @Override
    public int getCount() {
        return 0;
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
        UIImageGalleryItem item = imageItems.get(position);
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        return view;
    }
}
