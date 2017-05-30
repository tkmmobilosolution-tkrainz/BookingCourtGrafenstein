package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BookCourtActivity extends AppCompatActivity {

    ArrayList<BCReservation> reservationList = new ArrayList<>();
    long numberOfCourts = 0;
    String open;
    String close;
    double duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        String date = getIntent().getStringExtra("date");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("reservations");
        reference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> reservationsMap = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

                for (Map.Entry<String, HashMap<String, Object>> entry : reservationsMap.entrySet()) {

                    HashMap<String, Object> reservationMap = entry.getValue();
                    BCReservation reservation = new BCReservation();
                    reservation.setActive((long)reservationMap.get("active"));
                    reservation.setDate((String)reservationMap.get("date"));
                    reservation.setName((String)reservationMap.get("name"));
                    reservation.setBeginTime((String)reservationMap.get("beginTime"));
                    reservation.setEndTime((String)reservationMap.get("endTime"));
                    reservation.setUserUuid((String)reservationMap.get("userUuid"));
                    reservation.setCourt((long)reservationMap.get("court"));
                    reservationList.add(reservation);
                }

                FirebaseDatabase.getInstance().getReference().child("basic_permissions").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> basicPermissions = new HashMap<String, Object>();

                        basicPermissions = (HashMap<String, Object>) dataSnapshot.getValue();
                        numberOfCourts = (long) basicPermissions.get("court_number");
                        open = (String) basicPermissions.get("open");
                        close = (String) basicPermissions.get("close");
                        duration = (double) basicPermissions.get("booking_duration");

                        ListView listVie = (ListView)findViewById(R.id.bookingListView);
                        CourtAdapter adapter = new CourtAdapter(reservationList, (int) numberOfCourts, open, close, duration);
                        listVie.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            // TODO: shared prefs bcuser = null
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
