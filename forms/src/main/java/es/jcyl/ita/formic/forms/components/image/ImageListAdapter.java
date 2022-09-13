package es.jcyl.ita.formic.forms.components.image;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.repo.Entity;

public class ImageListAdapter extends BaseAdapter {

    private List<Entity> imageItems;
    private int itemLayout;

    public ImageListAdapter(UIImageGallery component) {
        imageItems = new ArrayList<>();
        itemLayout = R.layout.widget_imagegalleryitem;
    }


    @Override
    public int getCount() {
        return imageItems.size();
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
        Entity item = imageItems.get(position);
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        return view;
    }

    public void setItems(List<Entity> items) {
        this.imageItems = items;
    }

    public void addItem(Entity item) {
        this.imageItems.add(item);
    }

    public void addItems(List<Entity> items) {
        this.imageItems.addAll(items);
    }
}
