package es.jcyl.ita.frmdrd.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    public static final String NULL_VALUE = "<vacío>";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "dd/MM/yyyy");
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    protected static final Log LOGGER = LogFactory.getLog(DataUtils.class);

    /**
     * Convierte un boolean en un int.
     *
     * @param value
     * @return
     */
    public static int boolean2int(final boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Convierte un Boolean en un Integer.
     *
     * @param value
     * @return
     */
    public static Integer booleanObject2Integer(final Boolean value) {
        Integer output = null;

        if (value == null) {
            output = null;
        } else if (value) {
            output = 1;
        } else {
            output = 0;
        }

        return output;
    }

    /**
     * Convierte un int en un boolean.
     *
     * @param value
     * @return
     */
    public static boolean int2boolean(final int value) {
        return value > 0 ? true : false;
    }

    /**
     * Convierte un Integer en un Boolean.
     *
     * @param value
     * @return
     */
    public static Boolean integerObject2Boolean(final Integer value) {
        Boolean output = null;

        if (value == null) {
            output = null;
        } else if (value > 0) {
            output = true;
        } else if (value == 0) {
            output = false;
        }

        return output;
    }

    /**
     * Convierte un String en un boolean.
     *
     * @param value
     * @return
     */
    public static boolean string2boolean(final String value,
                                         final boolean defaultValue) {
        boolean output = defaultValue;

        if (value == null) {
            return output;
        }

        try {
            final int intValue = Integer.parseInt(value);
            output = int2boolean(intValue);
        } catch (final NumberFormatException nfe) {
            try {
                output = Boolean.valueOf(value);
            } catch (final Exception e) {
                LOGGER.error("Error al convertir String en boolean", e);
            }
        }

        return output;
    }

    /**
     * Convierte un String en un Boolean.
     *
     * @param value
     * @return
     */
    public static Boolean string2Boolean(final String value,
                                         final Boolean defaultValue) {
        Boolean output = defaultValue;

        if (value == null) {
            return output;
        }

        try {
            final Integer intValue = Integer.parseInt(value);
            output = integerObject2Boolean(intValue);
        } catch (final NumberFormatException nfe) {
            try {
                if (Boolean.FALSE.toString().equalsIgnoreCase(value)
                        || Boolean.TRUE.toString().equalsIgnoreCase(value)) {
                    output = Boolean.valueOf(value);
                } else {
                    output = defaultValue;
                }
            } catch (final Exception e) {
                LOGGER.error("Error al convertir String en boolean", e);
            }
        }

        return output;
    }

    /**
     * Convierte un Long en un Date.
     *
     * @param value
     * @return
     */
    public static Date long2Date(final Long value) {
        Date output = null;

        if (value == null) {
            return output;
        }

        output = new Date(value * 1000);

        return output;
    }

    /**
     * Convierte un Date en un Long.
     *
     * @param value
     * @return
     */
    public static Long date2Long(final Date value) {
        Long output = null;

        if (value == null) {
            return output;
        }

        return value.getTime() / 1000;
    }

    public static String nullFormat(final String value) {
        if (value == null || value.length() == 0) {
            return NULL_VALUE;
        }
        return value;
    }

    public static String nullFormatEmpty(final String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        return value;
    }

    public static byte[] bitmapToByteArray(final Bitmap bm) {
        final int iBytes = bm.getWidth() * bm.getHeight() * 4;
        final ByteBuffer buffer = ByteBuffer.allocate(iBytes);

        bm.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    public static String ImageView2Hex(final ImageView view) {
        Bitmap image = getBitmap(view);
        if (image != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 0,
                    byteArrayOutputStream);
            return bytes2Hex(byteArrayOutputStream.toByteArray());
        } else {
            return null;
        }
    }

    public static String bytes2Hex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hex2Bytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return null;
        }
        byte[] val = new byte[hex.length() / 2];
        try {
            for (int i = 0; i < val.length; i++) {
                int index = i * 2;
                int j = Integer.parseInt(hex.substring(index, index + 2), 16);
                val[i] = (byte) j;
            }
        } catch (NumberFormatException ex) {
            return null;
        }
        return val;
    }

    public static boolean existImage(ImageView view) {
        return (getBitmap(view) != null);
    }

    public static void insertHexIntoImageView(String hex, ImageView view) {
        Bitmap bitmap = null;
        byte[] imageBytes = null;

        if (hex != null && isPNG(hex) && (imageBytes = hex2Bytes(hex)) !=
                null) {
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
                    imageBytes.length);
            view.setImageBitmap(bitmap);
        }else{
            view.setImageDrawable(null);
        }
    }

    public static Bitmap getBitmap(ImageView view) {
        Drawable draw = view.getDrawable();
        if (draw == null) {
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    public static boolean  isPNG(String hex){
        return hex.startsWith("89504E470D0A1A0A");
    }
}
