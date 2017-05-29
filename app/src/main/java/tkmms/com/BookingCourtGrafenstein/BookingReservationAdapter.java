package tkmms.com.BookingCourtGrafenstein;

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
 * Created by tkrainz on 29/05/2017.
 */

public class BookingReservationAdapter extends BaseAdapter {

    private ArrayList<BCReservation> reservations;
    private ArrayList<String> ids;

    public BookingReservationAdapter(ArrayList<BCReservation> reservations, ArrayList<String> ids) {
        this.reservations = reservations;
        this.ids = ids;

    }

    @Override
    public int getCount() {
        return reservations.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_reservation_item, parent, false);
        }

        TextView timeTextView = (TextView) convertView.findViewById(R.id.tv_reservation_time);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.tv_reservation_date);
        TextView courtTextView = (TextView) convertView.findViewById(R.id.tv_reservation_court);

        final BCReservation currentReservation = reservations.get(position);

        String bookingTime = currentReservation.getBeginTime() + " - " + currentReservation.getEndTime();
        String bookingDate = currentReservation.getDate();
        String bookingCourt = "Platz " + currentReservation.getCourt();

        timeTextView.setText(bookingTime);
        dateTextView.setText(bookingDate);
        courtTextView.setText(bookingCourt);

        final long isActive = currentReservation.getIsActive();

        final Button modifyButton = (Button) convertView.findViewById(R.id.btn_reservation_inactive);
        modifyButton.setText(isActive == 1 ? "Aktiv" : "Storniert");
        modifyButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parent.getContext(), isActive == 1 ? R.color.bc_green : R.color.bc_red)));
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentId = ids.get(position);
                FirebaseDatabase.getInstance().getReference().child("reservations").child(currentId).child("active").setValue(isActive == 0 ? 1 : 0);

                if (parent.getContext() instanceof BookingReservationActivity) {

                    ((BookingReservationActivity) parent.getContext()).loadReservationList();
                }
            }
        });

        return convertView;
    }
}
