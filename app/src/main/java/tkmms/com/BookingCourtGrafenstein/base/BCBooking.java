package tkmms.com.BookingCourtGrafenstein.base;

import java.util.ArrayList;

/**
 * Created by tkrainz on 24/05/2017.
 */

public class BCBooking {

    private String beginDate;

    private String beginTime;

    private long court;

    private String endDate;

    private String endTime;

    private String name;

    private ArrayList<String> reservationIds;

    private long weekday;

    private long isActive;

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public long getCourt() {
        return court;
    }

    public void setCourt(long court) {
        this.court = court;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getReservationIds() {
        return reservationIds;
    }

    public void setReservationIds(ArrayList<String> reservationIds) {
        this.reservationIds = reservationIds;
    }

    public long getWeekday() {
        return weekday;
    }

    public void setWeekday(long weekday) {
        this.weekday = weekday;
    }

    public long getIsActive() {
        return isActive;
    }

    public void setIsActive(long isActive) {
        this.isActive = isActive;
    }
}
