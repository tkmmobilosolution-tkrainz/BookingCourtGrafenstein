package tkmms.com.BookingCourtGrafenstein.member;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import tkmms.com.BookingCourtGrafenstein.base.BCApplication;
import tkmms.com.BookingCourtGrafenstein.base.BCReservation;
import tkmms.com.BookingCourtGrafenstein.base.BCUser;
import tkmms.com.BookingCourtGrafenstein.authorization.LoginActivity;
import tkmms.com.BookingCourtGrafenstein.R;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BookCourtActivity extends AppCompatActivity {

    private ArrayList<BCReservation> reservationList = new ArrayList<>();
    private long numberOfCourts = 0;
    private String open;
    private String close;
    private double duration;

    private int selectedCourt;
    private String selectedTime;

    private CourtAdapter adapter;
    private String date;

    private AlertDialog deleteAlertDialog;
    private AlertDialog resAlertDialog;
    private AlertDialog hintAlertDialog;

    private int selectedDuration;

    private ProgressDialog progressDialog = null;

    private int refreshPosition = 0;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Reserviere");

        date = getIntent().getStringExtra("date");

        getSupportActionBar().setTitle(date);

        getReservations();
    }

    private void getReservations() {

        if (!isOnline()) {
            return;
        }

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
                        reservation.setActive((long) reservationMap.get("active"));
                        reservation.setDate((String) reservationMap.get("date"));
                        reservation.setName((String) reservationMap.get("name"));
                        reservation.setBeginTime((String) reservationMap.get("beginTime"));
                        reservation.setEndTime((String) reservationMap.get("endTime"));
                        reservation.setUserUuid((String) reservationMap.get("userUuid"));
                        reservation.setCourt((long) reservationMap.get("court"));
                        reservation.setId((String) reservationMap.get("id"));
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

        if (!isOnline()) {
            return;
        }

        ListView listVie = (ListView) findViewById(R.id.bookingListView);
        adapter = new CourtAdapter(reservationList, (int) numberOfCourts, open, close, duration, new CourtAdapter.ButtonClickEventListener() {
            @Override
            public void validReservationClicked(int court, String time, int duration, int position) {

                refreshPosition = position;
                selectedCourt = court;
                selectedTime = time;
                initReservationHint(duration);
                resAlertDialog.show();
            }

            @Override
            public void ownReservationClicked(BCReservation reservation, int position) {

                refreshPosition = position;
                initDeleteHint(reservation);
                deleteAlertDialog.show();
            }

            @Override
            public void differentReservationClicked(BCReservation reservation) {

            }
        });

        if (!this.isDestroyed()) {
            listVie.setAdapter(adapter);
            listVie.setSelection(refreshPosition);
            progressDialog.dismiss();
        }
    }

    private void makeReservation(String reservationId, BCReservation reservation) {

        if (!isOnline()) {
            return;
        }

        FirebaseDatabase.getInstance().getReference()
                .child("reservations")
                .child(reservationId)
                .setValue(reservation.getHashMap());

        getReservations();
    }

    private void deleteOwnReservation(BCReservation reservation) {

        if (!isOnline()) {
            return;
        }

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
        hintButton.setText("Reservierung löschen");

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
        hintTitleView.setText("Reservierung");

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
            default:
                break;
        }
        ArrayAdapter<CharSequence> spinerAdapter = ArrayAdapter.createFromResource(this, array, android.R.layout.simple_list_item_1);
        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinerAdapter);
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
        hintButton.setText("Reservieren");
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

        if (!isOnline()) {
            return;
        }

        final ArrayList<BCReservation> oldReservations = reservationList;
        final ArrayList<BCReservation> newReservationList = new ArrayList<>();

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
                        reservation.setActive((long) reservationMap.get("active"));
                        reservation.setDate((String) reservationMap.get("date"));
                        reservation.setName((String) reservationMap.get("name"));
                        reservation.setBeginTime((String) reservationMap.get("beginTime"));
                        reservation.setEndTime((String) reservationMap.get("endTime"));
                        reservation.setUserUuid((String) reservationMap.get("userUuid"));
                        reservation.setCourt((long) reservationMap.get("court"));
                        reservation.setId((String) reservationMap.get("id"));
                        newReservationList.add(reservation);
                    }

                    if (oldReservations.size() <= newReservationList.size()) {
                        if (checkUserReservation()) {
                            reserve();
                        } else {
                            progressDialog.dismiss();
                        }
                    } else {
                        initHintAlert("In der Zwischenzeit wurde eine neue Reservierung getätigt. Bitte versuche es noch einmal.", true);
                        hintAlertDialog.show();
                        progressDialog.dismiss();
                    }
                } else {
                    reserve();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                initHintAlert("Ein Fehler ist aufgetretten. Bitte versuche es noch einmal.", true);
                hintAlertDialog.show();
                progressDialog.dismiss();
            }
        });


    }

    private boolean checkUserReservation() {

        for (int i = 0; i < reservationList.size(); i ++) {

            if (reservationList.get(i).getUserUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                initHintAlert("Du hast für heute bereits eine Reservierung getätigt. Storniere diese um den gewünschten Termin reservieren zu können.", false);
                hintAlertDialog.show();
                return false;
            }
        }

        return true;
    }

    private void reserve() {
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
    }

    private void initHintAlert(String message, final boolean loadreservations) {
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
                if (loadreservations) {
                    getReservations();
                }
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

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            final AlertDialog hintAlertDialog;

            LayoutInflater inflater = this.getLayoutInflater();
            final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(this);
            final View hintAlertView = inflater.inflate(R.layout.hint, null);
            TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
            hintTitleView.setText("Achtung");

            TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
            hintMessageView.setText("Keine Verbindung zum Internet. Bitte überprüfe deine Internetverbindung und versuche es erneut.");
            final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

            dialogHintBuilder.setView(hintAlertView);
            hintAlertDialog = dialogHintBuilder.create();

            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(getIntent());
                    hintAlertDialog.dismiss();
                }
            });

            hintAlertDialog.show();

            return false;
        }
    }
}
