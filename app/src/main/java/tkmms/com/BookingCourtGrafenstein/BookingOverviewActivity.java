package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tkrainz on 24/05/2017.
 */

public class BookingOverviewActivity extends AppCompatActivity {

    private Button addTrainingButton;

    private ListView bookingsListView;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_overview);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Reserviere");

        addTrainingButton = (Button) findViewById(R.id.btn_add_training);
        addTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingOverviewActivity.this, ChooseBookingActivity.class);
                startActivity(intent);
            }
        });

        bookingsListView = (ListView) findViewById(R.id.booking_overview_listview);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadList();
    }

    public void loadList() {
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, BCBooking> bookingMap = (Map<String, BCBooking>) dataSnapshot.getValue();
                if (bookingMap != null) {

                    TreeMap<String, BCBooking> treemap = new TreeMap<String, BCBooking>(bookingMap);
                    ArrayList<BCBooking> bookingList = new ArrayList<BCBooking>();
                    ArrayList<String> ids = new ArrayList<String>();

                    Iterator basicIterator = treemap.entrySet().iterator();
                    while (basicIterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) basicIterator.next();

                        ids.add((String)pair.getKey());
                        HashMap<String, Object> bookingDetaiMap = (HashMap<String, Object>) pair.getValue();

                        BCBooking booking = new BCBooking();
                        booking.setName((String) bookingDetaiMap.get("name"));
                        booking.setBeginDate((String) bookingDetaiMap.get("beginDate"));
                        booking.setBeginTime((String) bookingDetaiMap.get("beginTime"));
                        booking.setCourt((long) bookingDetaiMap.get("court"));
                        booking.setReservationIds((ArrayList<String>)bookingDetaiMap.get("reservationIds"));
                        booking.setWeekday((long) bookingDetaiMap.get("weekday"));
                        booking.setEndDate((String) bookingDetaiMap.get("endDate"));
                        booking.setEndTime((String) bookingDetaiMap.get("endTime"));
                        booking.setIsActive((long) bookingDetaiMap.get("active"));

                        bookingList.add(booking);
                    }

                    BookingListAdapter adapter = new BookingListAdapter(bookingList, ids);
                    bookingsListView.setAdapter(adapter);

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("USER");
            editor.apply();
            Toast.makeText(this, "Du hast dich erfolgreich abgemeldet", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
