package heremapsexapmles.hrmaps;

/**
 * Created by Sachin on 2018-02-10.
 */

public class DataHolder {

    String title ;
    String time ;
    String lanes ;


    public DataHolder(String title,String lanes , String time ) {
        this.title = title;
        this.time = time;
        this.lanes = lanes;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getLanes() {
        return lanes;
    }
}
