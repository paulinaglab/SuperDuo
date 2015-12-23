package barqsoft.footballscores.model;


import android.text.TextUtils;

/**
 * Created by Paulina on 2015-11-23.
 */
public class Team implements BaseFootballData {

    public int id;
    public String name;
    public String code;
    public String shortName;
    public boolean selected;


    public Team(int id, String name, String code, String shortName, boolean selected) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.shortName = shortName;
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Team))
            return false;

        return id == ((Team) o).id;
    }

    //Should override when equals is overriden
    @Override
    public int hashCode() {
        return id;
    }

    public String getName() {
        if (TextUtils.isEmpty(shortName))
            return name;
        return shortName;
    }

    @Override
    public int getType() {
        return TYPE_TEAM;
    }
}
