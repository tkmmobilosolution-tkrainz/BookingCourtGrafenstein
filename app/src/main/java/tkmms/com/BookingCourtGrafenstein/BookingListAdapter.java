package tkmms.com.BookingCourtGrafenstein;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by tkrainz on 24/05/2017.
 */

public class BookingListAdapter extends BaseAdapter {

    private ArrayList<BCBooking> bookings;

    private ArrayList<String> ids;

    public BookingListAdapter(ArrayList<BCBooking> bookings, ArrayList<String> ids) {
        this.bookings = bookings;
        this.ids = ids;
    }

    @Override
    public int getCount() {
        return bookings.size();
    }

    @Override
    public Object getItem(int position) {
        return bookings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_overview_list_item, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_booking_name);
        TextView weekdayTextView = (TextView) convertView.findViewById(R.id.tv_booking_day);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.tv_booking_time);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.tv_booking_date);
        TextView courtTextView = (TextView) convertView.findViewById(R.id.tv_booking_court);

        final BCBooking currentBooking = bookings.get(position);

        String weekDay = null;
        switch((int) currentBooking.getWeekday()) {
            case 1:
                weekDay = "Sonntag";
                break;
            case 2:
                weekDay = "Montag";
                break;
            case 3:
                weekDay = "Dienstag";
                break;
            case 4:
                weekDay = "Mittwoch";
                break;
            case 5:
                weekDay = "Donnerstag";
                break;
            case 6:
                weekDay = "Freitag";
                break;
            case 7:
                weekDay = "Samstag";
                break;
        }

        String bookingTime = currentBooking.getBeginTime() + " - " + currentBooking.getEndTime();
        String bookingDate = currentBooking.getBeginDate() + " - " + currentBooking.getEndDate();
        String bookingCourt = "Platz " + currentBooking.getCourt();

        nameTextView.setText(currentBooking.getName());
        weekdayTextView.setText(weekDay);
        timeTextView.setText(bookingTime);
        dateTextView.setText(bookingDate);
        courtTextView.setText(bookingCourt);

        Button overviewButton = (Button) convertView.findViewById(R.id.btn_booking_overview);
        overviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), BookingReservationActivity.class);
                intent.putExtra("id", currentBooking.getReservationIds());
                parent.getContext().startActivity(intent);
            }
        });

        final long isActive = currentBooking.getIsActive();

        Button deleteButton = (Button) convertView.findViewById(R.id.btn_booking_delete);
        deleteButton.setText(isActive == 1 ? "Aktiv" : "Storniert");
        deleteButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parent.getContext(), isActive == 1 ? R.color.bc_green : R.color.bc_red)));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("bookings").child(ids.get(position)).child("active").setValue(isActive == 0 ? 1 : 0);

                for (int i = 0; i < currentBooking.getReservationIds().size(); i ++) {
                    FirebaseDatabase.getInstance().getReference().child("reservations").child(currentBooking.getReservationIds().get(i)).child("active").setValue(isActive == 0 ? 1 : 0);
                }

                if (parent.getContext() instanceof BookingOverviewActivity) {

                    ((BookingOverviewActivity) parent.getContext()).loadList();
                }
            }
        });

        return convertView;
    }
}
