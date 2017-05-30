package tkmms.com.BookingCourtGrafenstein;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class CourtAdapter extends BaseAdapter {

    private int numberOfCourts;
    private ArrayList<BCReservation> reservationList;
    private String open;
    private String close;
    private double duration;

    ArrayList<String> fromTimes;
    ArrayList<String> toTimes;

    public CourtAdapter(ArrayList<BCReservation> reservationList, int numberOfCourts, String open, String close, double duration) {
        this.reservationList = reservationList;
        this.numberOfCourts = numberOfCourts;
        this.open = open;
        this.close = close;
        this.duration = duration;

        fromTimes = generateFromTimes();
        toTimes = generateToTimes();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_item, parent, false);
        }

        TextView timeView = (TextView) convertView.findViewById(R.id.tvTime);
        timeView.setText(fromTimes.get(position) + " - " + toTimes.get(position));

        BCButton court1 = (BCButton)convertView.findViewById(R.id.btnCourt1);
        BCButton court2 = (BCButton)convertView.findViewById(R.id.btnCourt2);
        BCButton court3 = (BCButton)convertView.findViewById(R.id.btnCourt3);
        BCButton court4 = (BCButton)convertView.findViewById(R.id.btnCourt4);
        BCButton court5 = (BCButton)convertView.findViewById(R.id.btnCourt5);
        BCButton court6 = (BCButton)convertView.findViewById(R.id.btnCourt6);
        BCButton court7 = (BCButton)convertView.findViewById(R.id.btnCourt7);
        BCButton court8 = (BCButton)convertView.findViewById(R.id.btnCourt8);
        BCButton court9 = (BCButton)convertView.findViewById(R.id.btnCourt9);
        BCButton court10 = (BCButton)convertView.findViewById(R.id.btnCourt10);

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
        }

        court1.setButtonBackgroundForState("Frei");
        court2.setButtonBackgroundForState("Besetzt");
        court3.setButtonBackgroundForState("Training");
        court4.setButtonBackgroundForState("Test");

        return convertView;
    }

    private ArrayList<String> generateFromTimes() {
        String time = open;
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);

        int endHour = 21;

        ArrayList<String> timesForList = new ArrayList<String>();
        for (int i = hour; i <= endHour; i++) {

            for (int m = minutes; m < 60; m ++) {
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

            for (int m = minutes; m < 60; m ++) {
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
