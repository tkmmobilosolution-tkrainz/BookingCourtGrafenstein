package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.UUID;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BookCourtActivity extends AppCompatActivity {

    ArrayList<BCReservation> reservationList = new ArrayList<>();
    long numberOfCourts = 0;
    String open;
    String close;
    double duration;

    int selectedCourt;
    String selectedTime;

    CourtAdapter adapter;
    String date;

    private AlertDialog deleteAlertDialog;
    private AlertDialog resAlertDialog;
    private AlertDialog hintAlertDialog;

    private int selectedDuration;

    private ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Reserviere");

        date = getIntent().getStringExtra("date");

        getReservations();
    }

    private void getReservations() {
        if (!progressDialog.isShowing()) {
            showProgressBarWithTitle("Lade Reservierungen");
        }

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("reservations");
        reference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservationList.clear();
                HashMap<String, HashMap<String, Object>> reservationsMap = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

                if (reservationsMap != null) {
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
                        reservation.setId((String)reservationMap.get("id"));
                        reservationList.add(reservation);
                    }
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

                        setListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void setListView() {

        ListView listVie = (ListView)findViewById(R.id.bookingListView);
        adapter = new CourtAdapter(reservationList, (int) numberOfCourts, open, close, duration, new CourtAdapter.ButtonClickEventListener() {
            @Override
            public void validReservationClicked(int court, String time, int duration) {

                selectedCourt = court;
                selectedTime = time;
                initReservationHint(duration);
                resAlertDialog.show();
            }

            @Override
            public void ownReservationClicked(BCReservation reservation) {

                initDeleteHint(reservation);
                deleteAlertDialog.show();
            }

            @Override
            public void differentReservationClicked(BCReservation reservation) {

            }
        });
        listVie.setAdapter(adapter);
        progressDialog.dismiss();
    }

    private void makeReservation(String reservationId, BCReservation reservation) {
        FirebaseDatabase.getInstance().getReference()
                .child("reservations")
                .child(reservationId)
                .setValue(reservation.getHashMap());

        getReservations();
    }

    private void deleteOwnReservation(BCReservation reservation) {
        FirebaseDatabase.getInstance().getReference().child("reservations").child(reservation.getId()).removeValue();

        getReservations();
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

    private void initDeleteHint(final BCReservation res) {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(BookCourtActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.hint, null);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        hintTitleView.setText("Achtung");

        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
        hintMessageView.setText("Wilst du deine Reservierung löschen?");
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBarWithTitle("Lösche Reservierung");
                deleteOwnReservation(res);
                deleteAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        deleteAlertDialog = dialogHintBuilder.create();
    }

    private void initReservationHint(int maxDuration) {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(BookCourtActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.reservation_alert, null);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintResTitleTextView);
        hintTitleView.setText("Achtung");

        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintResMessageTextView);
        hintMessageView.setText("Wähle aus, wie lange du reservieren willst.");

        Spinner spinner = (Spinner) hintAlertView.findViewById(R.id.hintResSinner);

        int array = R.array.reservation1;
        switch (maxDuration) {
            case 1:
                array = R.array.reservation1;
                break;
            case 2:
                array = R.array.reservation2;
                break;
            case 3:
                array = R.array.reservation3;
                break;
            case 4:
                array = R.array.reservation4;
                break;
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, array, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDuration = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDuration = 1;
            }
        });

        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintResButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveButtonClicked();
                resAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        resAlertDialog = dialogHintBuilder.create();
    }

    private void reserveButtonClicked() {
        showProgressBarWithTitle("Reserviere");

        checkIfReservationIsValid();
    }

    private void checkIfReservationIsValid() {
        final ArrayList<BCReservation> oldReservations = reservationList;
        final ArrayList<BCReservation> newReservationList = new ArrayList<BCReservation>();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("reservations");
        reference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> reservationsMap = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                newReservationList.clear();
                if (reservationsMap != null) {
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
                        reservation.setId((String)reservationMap.get("id"));
                        newReservationList.add(reservation);
                    }

                    if (oldReservations.size() <= newReservationList.size()) {
                        BCUser currentUser = BCApplication.getApplication().getGlobals().getCurrentUser();
                        String reservationId = FirebaseAuth.getInstance().getCurrentUser().getUid() + "-RES-" + UUID.randomUUID().toString();
                        String beginTime = selectedTime;
                        String endTime = "";

                        String[] curTimeArray = selectedTime.split(":");
                        int hour = Integer.parseInt(curTimeArray[0]);
                        int minutes = Integer.parseInt(curTimeArray[1]);

                        for (int i = 1; i <= selectedDuration; i++) {

                            if (minutes == 30) {
                                minutes = 0;
                                hour += 1;
                            } else if (minutes == 0) {
                                minutes = 30;
                            }

                            if (i == selectedDuration) {
                                endTime = String.format("%02d", hour) + ":" + String.format("%02d", minutes);
                            }
                        }

                        BCReservation newReservation = new BCReservation(FirebaseAuth.getInstance().getCurrentUser().getUid(), beginTime, endTime, date, selectedCourt, 1, currentUser.getLastname(), reservationId);
                        makeReservation(reservationId, newReservation);
                    } else {
                        initHintAlert("In der Zwischenzeit wurde eine neue Reservierung getätigt. Bitte versuche es noch einmal.");
                        hintAlertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                initHintAlert("Ein Fehler ist aufgetretten. Bitte versuche es noch einmal.");
                hintAlertDialog.show();
                progressDialog.dismiss();
            }
        });


    }

    private void initHintAlert(String message) {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(BookCourtActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.hint, null);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        hintTitleView.setText("Achtung");

        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
        hintMessageView.setText(message);
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReservations();
                hintAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        hintAlertDialog = dialogHintBuilder.create();
    }

    private void showProgressBarWithTitle(String title) {
        progressDialog.setMessage(title);
        progressDialog.show();
    }
}
