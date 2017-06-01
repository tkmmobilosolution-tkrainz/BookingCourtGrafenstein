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

    private long court = 0;

    private long isActive = 0;

    private String name = null;

    private String id;

    public BCReservation() {

    }

    public BCReservation(String userUuid, String beginTime, String endTime, String date, long court, long isActive, String name, String id) {

        this.userUuid = userUuid;
        this.beginTime  = beginTime;
        this.endTime = endTime;
        this.date = date;
        this.court = court;
        this.isActive = isActive;
        this.name = name;
        this.id = id;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCourt(long court) {
        this.court = court;
    }

    public void setActive(long active) {
        isActive = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public long getCourt() {
        return court;
    }

    public long getIsActive() { return isActive; }

    public void setIsActive(long isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userUuid", userUuid);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("date", date);
        map.put("court", court);
        map.put("name", name);
        map.put("active", isActive);
        map.put("id", id);
        return map;
    }
}
