package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tkrainz on 27/05/2017.
 */

public class BookingReservationActivity extends AppCompatActivity {

    ArrayList<BCReservation> reservations = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    Listener listener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_reservation);

        Bundle extra = getIntent().getExtras();
        ids = (ArrayList<String>) extra.get("id");

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Lade Daten");


        loadReservationList();

        listener = new Listener() {
            @Override
            public void listLoadedSuccess() {
                ListView listview = (ListView) findViewById(R.id.reservation_list);
                BookingReservationAdapter adapter = new BookingReservationAdapter(reservations, ids);
                listview.setAdapter(adapter);
                progressDialog.dismiss();
            }
        };
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

    public void loadReservationList() {
        reservations.clear();
        progressDialog.show();

        for (int i = 0; i < ids.size(); i++) {

            FirebaseDatabase.getInstance().getReference().child("reservations").child(ids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BCReservation reservation = new BCReservation();
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                    reservation.setUserUuid((String)map.get("userUuid"));
                    reservation.setBeginTime((String) map.get("beginTime"));
                    reservation.setEndTime((String) map.get("endTime"));
                    reservation.setDate((String) map.get("date"));
                    reservation.setCourt((long) map.get("court"));
                    reservation.setName((String) map.get("name"));
                    reservation.setActive((long) map.get("active"));
                    reservations.add(reservation);

                    if (reservations.size() == ids.size()) {
                        listener.listLoadedSuccess();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private interface Listener {
        void listLoadedSuccess();
    }
}


