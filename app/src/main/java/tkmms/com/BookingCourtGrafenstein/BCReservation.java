package tkmms.com.BookingCourtGrafenstein;

import java.util.HashMap;

/**
 * Created by tkrainz on 24/05/2017.
 */

public class BCReservation {

    private String userUuid = null;

    private String beginTime = null;

    private String endTime = null;

    private String date = null;

    private int court = 0;

    public BCReservation(String userUuid, String beginTime, String endTime, String date, int court) {

        this.userUuid = userUuid;
        this.beginTime  = beginTime;
        this.endTime = endTime;
        this.date = date;
        this.court = court;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public int getCourt() {
        return court;
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userUuid", userUuid);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("date", date);
        map.put("court", court);

        return map;
    }
}
