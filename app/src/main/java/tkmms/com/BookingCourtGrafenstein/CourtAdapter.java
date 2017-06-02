package tkmms.com.BookingCourtGrafenstein;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class CourtAdapter extends BaseAdapter {

    private int numberOfCourts;
    private ArrayList<BCReservation> reservationList;
    private String open;
    private String close;
    private double duration;

    private ArrayList<String> fromTimes;
    private ArrayList<String> toTimes;

    private ArrayList<Object> reservationsCourt1 = new ArrayList<>();
    private ArrayList<Object> reservationsCourt2 = new ArrayList<>();
    private ArrayList<Object> reservationsCourt3 = new ArrayList<>();

    private ButtonClickEventListener listener;

    public CourtAdapter(ArrayList<BCReservation> reservationList, int numberOfCourts, String open, String close, double duration, ButtonClickEventListener listener) {
        this.reservationList = reservationList;
        this.numberOfCourts = numberOfCourts;
        this.open = open;
        this.close = close;
        this.duration = duration;
        this.listener = listener;

        reservationsCourt1.clear();
        reservationsCourt2.clear();
        reservationsCourt3.clear();

        for (int i = 0; i < 30; i++) {

            reservationsCourt1.add("Frei");
            reservationsCourt2.add("Frei");
            reservationsCourt3.add("Frei");
        }

        fromTimes = generateFromTimes();
        toTimes = generateToTimes();

        getReservations();
    }

    @Override
    public int getCount() {

        String[] openTime = open.split(":");
        String[] closeTime = close.split(":");

        int start = Integer.parseInt(openTime[0]);
        int close = Integer.parseInt(closeTime[0]);

        int time = close - start;
        double items = time / duration;
        return (int) items;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_item, parent, false);
        }

        TextView timeView = (TextView) convertView.findViewById(R.id.tvTime);
        timeView.setText(fromTimes.get(position) + " - " + toTimes.get(position));

        BCButton court1 = (BCButton) convertView.findViewById(R.id.btnCourt1);
        BCButton court2 = (BCButton) convertView.findViewById(R.id.btnCourt2);
        BCButton court3 = (BCButton) convertView.findViewById(R.id.btnCourt3);
        BCButton court4 = (BCButton) convertView.findViewById(R.id.btnCourt4);
        BCButton court5 = (BCButton) convertView.findViewById(R.id.btnCourt5);
        BCButton court6 = (BCButton) convertView.findViewById(R.id.btnCourt6);
        BCButton court7 = (BCButton) convertView.findViewById(R.id.btnCourt7);
        BCButton court8 = (BCButton) convertView.findViewById(R.id.btnCourt8);
        BCButton court9 = (BCButton) convertView.findViewById(R.id.btnCourt9);
        BCButton court10 = (BCButton) convertView.findViewById(R.id.btnCourt10);

        court1.setVisibility(View.GONE);
        court2.setVisibility(View.GONE);
        court3.setVisibility(View.GONE);
        court4.setVisibility(View.GONE);
        court5.setVisibility(View.GONE);
        court6.setVisibility(View.GONE);
        court7.setVisibility(View.GONE);
        court8.setVisibility(View.GONE);
        court9.setVisibility(View.GONE);
        court10.setVisibility(View.GONE);

        switch (numberOfCourts) {
            case 1:
                court1.setVisibility(View.VISIBLE);
                break;
            case 2:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                break;
            case 3:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                break;
            case 4:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                break;
            case 5:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                break;
            case 6:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                court6.setVisibility(View.VISIBLE);
                court7.setVisibility(View.INVISIBLE);
                court8.setVisibility(View.INVISIBLE);
                court9.setVisibility(View.INVISIBLE);
                court10.setVisibility(View.INVISIBLE);
                break;
            case 7:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                court6.setVisibility(View.VISIBLE);
                court7.setVisibility(View.VISIBLE);
                court8.setVisibility(View.INVISIBLE);
                court9.setVisibility(View.INVISIBLE);
                court10.setVisibility(View.INVISIBLE);
                break;
            case 8:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                court6.setVisibility(View.VISIBLE);
                court7.setVisibility(View.VISIBLE);
                court8.setVisibility(View.VISIBLE);
                court9.setVisibility(View.INVISIBLE);
                court10.setVisibility(View.INVISIBLE);
                break;
            case 9:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                court6.setVisibility(View.VISIBLE);
                court7.setVisibility(View.VISIBLE);
                court8.setVisibility(View.VISIBLE);
                court9.setVisibility(View.VISIBLE);
                court10.setVisibility(View.INVISIBLE);
                break;
            case 10:
                court1.setVisibility(View.VISIBLE);
                court2.setVisibility(View.VISIBLE);
                court3.setVisibility(View.VISIBLE);
                court4.setVisibility(View.VISIBLE);
                court5.setVisibility(View.VISIBLE);
                court6.setVisibility(View.VISIBLE);
                court7.setVisibility(View.VISIBLE);
                court8.setVisibility(View.VISIBLE);
                court9.setVisibility(View.VISIBLE);
                court10.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        if (reservationsCourt1.get(position) instanceof BCReservation) {
            BCReservation res = (BCReservation) reservationsCourt1.get(position);
            court1.setText(res.getName());
        } else {
            court1.setText((String) reservationsCourt1.get(position));
        }

        if (reservationsCourt2.get(position) instanceof BCReservation) {
            BCReservation res = (BCReservation) reservationsCourt2.get(position);
            court2.setText(res.getName());
        } else {
            court2.setText((String) reservationsCourt2.get(position));
        }

        if (reservationsCourt3.get(position) instanceof BCReservation) {
            BCReservation res = (BCReservation) reservationsCourt3.get(position);
            court3.setText(res.getName());
        } else {
            court3.setText((String) reservationsCourt3.get(position));
        }

        court1.setButtonBackgroundForState(court1.getText().toString());
        court2.setButtonBackgroundForState(court2.getText().toString());
        court3.setButtonBackgroundForState(court3.getText().toString());

        court1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reservationsCourt1.get(position) instanceof BCReservation) {
                    BCReservation reservation = (BCReservation) reservationsCourt1.get(position);
                    String userUuid = reservation.getUserUuid();

                    if (userUuid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        listener.ownReservationClicked(reservation, position);
                    } else {
                        listener.differentReservationClicked(reservation);
                    }
                } else if (reservationsCourt1.get(position) == "Frei") {
                    String selectedTime = fromTimes.get(position);
                    int validHalfHours = 1;
                    int newListSize = fromTimes.size() - position + 1;

                    for (int i = 1; i <= newListSize; i++) {

                        if (reservationsCourt1.get(position + i) == "Frei" && validHalfHours < 4) {
                            validHalfHours += 1;
                        } else {
                            break;
                        }
                    }

                    listener.validReservationClicked(1, fromTimes.get(position), validHalfHours, position);
                }
            }
        });

        court2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reservationsCourt2.get(position) instanceof BCReservation) {
                    BCReservation reservation = (BCReservation) reservationsCourt2.get(position);
                    String userUuid = reservation.getUserUuid();

                    if (userUuid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        listener.ownReservationClicked(reservation, position);
                    } else {
                        listener.differentReservationClicked(reservation);
                    }
                } else if (reservationsCourt2.get(position) == "Frei") {
                    String selectedTime = fromTimes.get(position);
                    int validHalfHours = 1;
                    int newListSize = fromTimes.size() - position + 1;

                    for (int i = 1; i <= newListSize; i++) {

                        if (reservationsCourt2.get(position + i) == "Frei" && validHalfHours < 4) {
                            validHalfHours += 1;
                        } else {
                            break;
                        }
                    }

                    listener.validReservationClicked(2, selectedTime, validHalfHours, position);
                }
            }
        });

        court3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reservationsCourt3.get(position) instanceof BCReservation) {
                    BCReservation reservation = (BCReservation) reservationsCourt3.get(position);
                    String userUuid = reservation.getUserUuid();

                    if (userUuid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        listener.ownReservationClicked(reservation, position);
                    } else {
                        listener.differentReservationClicked(reservation);
                    }
                } else if (reservationsCourt3.get(position) == "Frei") {
                    String selectedTime = fromTimes.get(position);
                    int validHalfHours = 1;
                    int newListSize = fromTimes.size() - position + 1;

                    for (int i = 1; i <= newListSize; i++) {

                        if (reservationsCourt3.get(position + i) == "Frei" && validHalfHours < 4) {
                            validHalfHours += 1;
                        } else {
                            break;
                        }
                    }

                    listener.validReservationClicked(3, selectedTime, validHalfHours, position);
                }
            }
        });

        return convertView;
    }

    public interface ButtonClickEventListener {
        void validReservationClicked(int court, String beginTime, int duration, int position);

        void ownReservationClicked(BCReservation reservation, int position);

        void differentReservationClicked(BCReservation reservation);
    }

    private void getReservations() {

        for (int pos = 0; pos < getCount(); pos++) {

            String beginningTime = fromTimes.get(pos);
            String[] beginnField = beginningTime.split(":");

            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginnField[0]));
            now.set(Calendar.MINUTE, Integer.parseInt(beginnField[1]));
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            for (int i = 0; i < reservationList.size(); i++) {

                BCReservation reservation = reservationList.get(i);
                String resBegin = reservation.getBeginTime();

                String[] reBeginField = resBegin.split(":");

                Calendar beginDate = Calendar.getInstance();
                beginDate.setTime(new Date());
                beginDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(reBeginField[0]));
                beginDate.set(Calendar.MINUTE, Integer.parseInt(reBeginField[1]));
                beginDate.set(Calendar.SECOND, 0);
                beginDate.set(Calendar.MILLISECOND, 0);

                String resEnd = reservation.getEndTime();

                String[] reEndField = resEnd.split(":");

                Calendar endDate = Calendar.getInstance();
                endDate.setTime(new Date());
                endDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(reEndField[0]));
                endDate.set(Calendar.MINUTE, Integer.parseInt(reEndField[1]));
                endDate.set(Calendar.SECOND, 0);
                endDate.set(Calendar.MILLISECOND, 0);

                Date nowMil = now.getTime();
                Date beginMil = beginDate.getTime();
                Date endMil = endDate.getTime();

                if (nowMil.equals(beginMil) || nowMil.after(beginMil)) {
                    if (nowMil.before(endMil)) {

                        if (reservation.getCourt() == 1 && reservation.getIsActive() == 1) {
                            reservationsCourt1.set(pos, reservation);
                        }

                        if (reservation.getCourt() == 2 && reservation.getIsActive() == 1) {
                            reservationsCourt2.set(pos, reservation);
                        }

                        if (reservation.getCourt() == 3 && reservation.getIsActive() == 1) {

                            reservationsCourt3.set(pos, reservation);
                        }
                    }
                }
            }

        }
    }

    private ArrayList<String> generateFromTimes() {
        String time = open;
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);

        int endHour = 21;

        ArrayList<String> timesForList = new ArrayList<String>();
        for (int i = hour; i <= endHour; i++) {

            for (int m = minutes; m < 60; m++) {
                String newTime = String.format("%02d", i) + ":" + String.format("%02d", m);
                timesForList.add(newTime);
                m = m + 29;
                minutes += 30;
            }

            if (minutes == 60) {
                minutes = 0;
            }

            hour += 1;
        }

        return timesForList;
    }

    private ArrayList<String> generateToTimes() {
        String time = open;
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);

        if (minutes == 30) {
            hour += 1;
            minutes = 0;
        } else {
            minutes = 30;
        }

        int endHour = 21;

        ArrayList<String> timesForList = new ArrayList<String>();
        for (int i = hour; i <= endHour; i++) {

            for (int m = minutes; m < 60; m++) {
                String newTime = String.format("%02d", i) + ":" + String.format("%02d", m);
                timesForList.add(newTime);
                m = m + 29;
                minutes += 30;
            }

            if (minutes == 60) {
                minutes = 0;
            }

            hour += 1;
        }

        timesForList.add("22:00");

        return timesForList;
    }
}
