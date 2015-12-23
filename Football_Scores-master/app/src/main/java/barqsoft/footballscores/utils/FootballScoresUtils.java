package barqsoft.footballscores.utils;

import android.content.Context;

import barqsoft.footballscores.R;

/**
 * Created by Paulina on 2015-12-10.
 */
public class FootballScoresUtils {

    public static String getScoreString(Context context, int homeGoals, int awayGoals) {
        return homeGoals +
                context.getResources().getString(R.string.match_card_score_connective) +
                awayGoals;
    }

}
