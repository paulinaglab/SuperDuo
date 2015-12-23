package barqsoft.footballscores.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.model.BaseFootballData;

/**
 * Created by Paulina on 2015-12-11.
 */
public class LeaguesLoader extends CustomizationBaseLoader {
    public LeaguesLoader(Context context) {
        super(context);
    }

    @Override
    public List<BaseFootballData> loadInBackground() {
        List<BaseFootballData> data = new ArrayList<>();
        data.addAll(loadLeagues());
        return data;
    }
}
