package es.jcyl.ita.formic.forms.wizard;

public class MenuItem {
    private final int id;
    private final String title;
    private final int iconResId;

    public MenuItem(int id, String title, int iconResId) {
        this.id = id;
        this.title = title;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
