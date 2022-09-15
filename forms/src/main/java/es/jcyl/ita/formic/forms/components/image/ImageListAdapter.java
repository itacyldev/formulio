package es.jcyl.ita.formic.forms.components.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.Renderer;
import es.jcyl.ita.formic.forms.view.render.RendererFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.media.FileEntity;

public class ImageListAdapter extends BaseAdapter {

    private List<Entity> imageItems;
    private int itemLayout;
    private UIImageGallery component;
    private RenderingEnv env;

    private ImageWidget[] imageItemViews;

    public ImageListAdapter(UIImageGallery component, RenderingEnv renderingEnv) {
        this.component = component;
        imageItems = new ArrayList<>();
        itemLayout = R.layout.widget_imagegalleryitem;
        env = renderingEnv;
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
//        if (view == null) {
//            view = LayoutInflater.from(parent.getContext())
//                    .inflate(itemLayout, parent, false);
//        }
//
//        Entity entity = imageItems.get(position);
//
//        if (entity instanceof FileEntity) {
//            ImageView imageView = view.findViewById(R.id.galleryitem_image);
//            File imgFile = ((FileEntity) entity).getFile();
//            if (imgFile.exists()) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                imageView.setImageBitmap(myBitmap);
//            }
//
//        } else {
//            if (component.getChildren().length > 0) {
//                UIImage item = (UIImage) component.getChildren()[0];
//                env.setEntity(entity);
//                Renderer renderer = RendererFactory.getInstance().getRenderer(item.getRendererType());
//                view = renderer.render(env, item);
//            }
//        }

        if (view == null) {
            view = imageItemViews[position];
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

    public void setImageItemViews(ImageWidget[] imageItemViews) {
        this.imageItemViews = imageItemViews;
    }
}
