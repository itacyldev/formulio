package es.jcyl.ita.formic.forms.wizard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.jcyl.ita.formic.forms.R;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final List<MenuItem> menuItems;
    private final OnMenuItemClickListener listener;

    public MenuAdapter(List<MenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_component_layout, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.icon.setImageResource(menuItem.getIconResId());
        holder.text.setText(menuItem.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onMenuItemClick(menuItem));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView text;

        public MenuViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.menu_icon);
            text = itemView.findViewById(R.id.menu_text);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem item);
    }
}
