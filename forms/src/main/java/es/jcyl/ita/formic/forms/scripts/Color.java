package es.jcyl.ita.formic.forms.scripts;

public class Color {
    public static int getBlack() {
        return android.graphics.Color.BLACK;
    }

    public static int getDkgray() {
        return android.graphics.Color.DKGRAY;
    }

    public static int getGray() {
        return android.graphics.Color.GRAY;
    }

    public static int getLtgray() {
        return android.graphics.Color.LTGRAY;
    }

    public static int getWhite() {
        return android.graphics.Color.WHITE;
    }

    public static int getRed() {
        return android.graphics.Color.RED;
    }

    public static int getGreen() {
        return android.graphics.Color.GREEN;
    }

    public static int getBlue() {
        return android.graphics.Color.BLUE;
    }

    public static int getYellow() {
        return android.graphics.Color.YELLOW;
    }

    public static int getCyan() {
        return android.graphics.Color.CYAN;
    }

    public static int getMagenta() {
        return android.graphics.Color.MAGENTA;
    }

    public static int getTransparent() {
        return android.graphics.Color.TRANSPARENT;
    }
    public static int rgb(int r, int g, int b, int alpha){
        return (alpha & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }
    public static int rgb(int r, int g, int b){
        return rgb(255,r,g,b);
    }

}
