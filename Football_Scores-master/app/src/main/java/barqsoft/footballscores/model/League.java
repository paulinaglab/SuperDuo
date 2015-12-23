package barqsoft.footballscores.model;


/**
 * Created by Paulina on 2015-11-23.
 */
public class League implements BaseFootballData {

    public int id;
    public int year;
    public String league;
    public String caption;
    public boolean selected;

    public League(int id, int year, String league, String caption, boolean selected) {
        this.id = id;
        this.year = year;
        this.league = league;
        this.caption = caption;
        this.selected = selected;
    }

    @Override
    public int getType() {
        return TYPE_LEAGUE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof League))
            return false;

        if (((League) o).id == id)
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
