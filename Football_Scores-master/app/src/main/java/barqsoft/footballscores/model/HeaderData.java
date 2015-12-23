package barqsoft.footballscores.model;


/**
 * Created by Paulina on 2015-11-23.
 */
public class HeaderData implements BaseFootballData {

    public String header;


    public HeaderData(String header) {
        this.header = header;
    }

    @Override
    public int getType() {
        return BaseFootballData.TYPE_HEADER;
    }
}
