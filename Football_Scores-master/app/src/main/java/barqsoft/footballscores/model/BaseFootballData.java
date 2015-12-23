package barqsoft.footballscores.model;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Paulina on 2015-11-23.
 */
public interface BaseFootballData extends Serializable{

    @Type
    int getType();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TYPE_HEADER,
            TYPE_TEAM_FIXTURE_COMING,
            TYPE_TEAM_FIXTURE_SCORE,
            TYPE_TEAM_TABLE,
            TYPE_LEAGUE_FIXTURES_COMING,
            TYPE_LEAGUE_FIXTURES_SCORES,
            TYPE_LEAGUE_TABLE,
            TYPE_TEAM,
            TYPE_LEAGUE})
    @interface Type {
    }

    int TYPE_HEADER = 0;
    int TYPE_TEAM_FIXTURE_COMING = 1;
    int TYPE_TEAM_FIXTURE_SCORE = 2;
    int TYPE_TEAM_TABLE = 3;
    int TYPE_LEAGUE_FIXTURES_COMING = 4;
    int TYPE_LEAGUE_FIXTURES_SCORES = 5;
    int TYPE_LEAGUE_TABLE = 6;
    int TYPE_TEAM = 7;
    int TYPE_LEAGUE = 8;

}
