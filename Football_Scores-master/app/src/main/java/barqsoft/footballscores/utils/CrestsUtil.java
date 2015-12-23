package barqsoft.footballscores.utils;

import android.content.Context;

import barqsoft.footballscores.R;

/**
 * Created by Paulina on 2015-12-10.
 */
public class CrestsUtil {

    private static final String CREST_PREFIX = "crest_";

    public static int getCrestId(Context context, int teamId) {
        int id = context
                .getResources()
                .getIdentifier(CREST_PREFIX + teamId, "drawable", context.getPackageName());

        return id == 0 ? R.drawable.crest_placeholder : id;
    }

}
