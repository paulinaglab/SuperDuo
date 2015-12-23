package it.jaschke.alexandria.utils;

import android.graphics.Color;

/**
 * Created by Paulina on 2015-10-30.
 */
public class ColorUtils {

    /**
     * Method returning given color modified to lighter color (positive translation) or darker
     * (negative translation).
     *
     * @param primaryColor basic color
     * @param translation  positive or negative value of color translation
     * @return lighter/darker color
     */
    public static int getColorWithTranslateBrightness(int primaryColor, int translation) {
        if (translation >= 0) {
            return Color.argb(Color.alpha(primaryColor),
                    Math.min(Color.red(primaryColor) + translation, 255),
                    Math.min(Color.green(primaryColor) + translation, 255),
                    Math.min(Color.blue(primaryColor) + translation, 255));
        } else {
            return Color.argb(Color.alpha(primaryColor),
                    Math.max(Color.red(primaryColor) + translation, 0),
                    Math.max(Color.green(primaryColor) + translation, 0),
                    Math.max(Color.blue(primaryColor) + translation, 0));
        }
    }

}
