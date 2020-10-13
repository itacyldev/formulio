package es.jcyl.ita.formic.forms.util;

public class DataUtils {
    public static final String NULL_VALUE = "<vacío>";

    public static String nullFormat(final String value) {
        if (value == null || value.length() == 0) {
            return NULL_VALUE;
        }
        return value;
    }

//
//    public static String nullFormatEmpty(final String value) {
//        if (value == null || value.length() == 0) {
//            return "";
//        }
//        return value;
//    }
//
//    public static byte[] bitmapToByteArray(final Bitmap bm) {
//        final int iBytes = bm.getWidth() * bm.getHeight() * 4;
//        final ByteBuffer buffer = ByteBuffer.allocate(iBytes);
//
//        bm.copyPixelsToBuffer(buffer);
//        return buffer.array();
//    }
//
//    public static String ImageView2Hex(final ImageView view) {
//        Bitmap image = getBitmap(view);
//        if (image != null) {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.PNG, 0,
//                    byteArrayOutputStream);
//            return bytes2Hex(byteArrayOutputStream.toByteArray());
//        } else {
//            return null;
//        }
//    }
//
//    public static String bytes2Hex(byte[] bytes) {
//        char[] hexChars = new char[bytes.length * 2];
//        int v;
//        for (int j = 0; j < bytes.length; j++) {
//            v = bytes[j] & 0xFF;
//            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
//            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
//        }
//        return new String(hexChars);
//    }
//
//    public static byte[] hex2Bytes(String hex) {
//        if (hex == null || hex.length() % 2 != 0) {
//            return null;
//        }
//        byte[] val = new byte[hex.length() / 2];
//        try {
//            for (int i = 0; i < val.length; i++) {
//                int index = i * 2;
//                int j = Integer.parseInt(hex.substring(index, index + 2), 16);
//                val[i] = (byte) j;
//            }
//        } catch (NumberFormatException ex) {
//            return null;
//        }
//        return val;
//    }
//
//    public static boolean existImage(ImageView view) {
//        return (getBitmap(view) != null);
//    }
//
//    public static void insertHexIntoImageView(String hex, ImageView view) {
//        Bitmap bitmap = null;
//        byte[] imageBytes = null;
//
//        if (hex != null && isPNG(hex) && (imageBytes = hex2Bytes(hex)) !=
//                null) {
//            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
//                    imageBytes.length);
//            view.setImageBitmap(bitmap);
//        }else{
//            view.setImageDrawable(null);
//        }
//    }
//
//    public static Bitmap getBitmap(ImageView view) {
//        Drawable draw = view.getDrawable();
//        if (draw == null) {
//            return null;
//        }
//        Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
//        if (bitmap == null) {
//            return null;
//        }
//        return bitmap;
//    }
//
//    public static boolean  isPNG(String hex){
//        return hex.startsWith("89504E470D0A1A0A");
//    }
}
