package barqsoft.footballscores.utils;

import android.content.Context;

import barqsoft.footballscores.R;
import barqsoft.footballscores.model.TeamFixtureComing;
import barqsoft.footballscores.model.TeamFixtureScore;

/**
 * Created by Paulina on 2015-12-03.
 */
public class ModelParser {

    private static final int APP_HASHTAG_ID = R.string.app_hashtag;


    public static String toString(TeamFixtureScore data, Context context) {
        String string;

        string = context.getResources().getString(R.string.sharing_fixture_score_prefix) + "\n\n" +
                CustomDateFormatter.getDayNameAndTime(context, data.fixtureScore.date) + "\n" +
                data.fixtureScore.homeTeam.getName() + " " + data.fixtureScore.homeGoals + "\n" +
                data.fixtureScore.awayTeam.getName() + " " + data.fixtureScore.awayGoals + "\n\n" +
                context.getResources().getString(APP_HASHTAG_ID);

        return string;
    }

    public static String toString(TeamFixtureComing data, Context context) {
        String string;

        string = CustomDateFormatter.getDayNameAndTime(context, data.fixtureComing.date) + "\n" +
                data.fixtureComing.homeTeam.getName() + "\n" +
                data.fixtureComing.awayTeam.getName() + "\n\n" +
                context.getResources().getString(R.string.sharing_coming_fixture_suffix) + "\n\n" +
                context.getResources().getString(APP_HASHTAG_ID);

        return string;
    }
}
