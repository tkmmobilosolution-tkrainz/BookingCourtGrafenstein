package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by tkrainz on 29/05/2017.
 */

public class AddSingleEventActivity extends AppCompatActivity {

    private Spinner courtSpinner;

    private EditText trainingName;

    private Button addTrainingButton;
    private Button dateButton;
    private Button beginTimeButton;
    private Button endTimeButton;

    private int weekdayInt;
    private int court;

    private String name;
    private String beginDateString;
    private String endDateString;
    private String beginTimeString;
    private String endTimeString;

    private AlertDialog calendarBeginAlertDialog;
    private AlertDialog beginTimeAlertDialog;
    private AlertDialog endTimeAlertDialog;
    private AlertDialog hintAlertDialog;

    private ArrayList<BCReservation> reservationList;

    private ProgressDialog progressDialog = null;

    private static final String START_DATUM_BUTTON_TITLE = "Datum wählen";
    private static final String END_DATE_BUTTON_TITLE = "End Datum wählen";
    private static final String END_TIME_BUTTON_TITLE = "Ende wählen";
    private static final String BEGIN_TIME_BUTTON_TITLE = "Begin wählen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single_event);

        initCourtSpinner();

        trainingName = (EditText) findViewById(R.id.et_single_training_name);
        trainingName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        dateButton = (Button) findViewById(R.id.btn_single_date);
        dateButton.setText(END_DATE_BUTTON_TITLE);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBeginDateAlertDialog();
                calendarBeginAlertDialog.show();
            }
        });

        beginTimeButton = (Button) findViewById(R.id.btn_single_beginTime);
        beginTimeButton.setText(BEGIN_TIME_BUTTON_TITLE);
        beginTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBeginTimeAlertDialog();
                beginTimeAlertDialog.show();
            }
        });

        endTimeButton = (Button) findViewById(R.id.btn_single_endTime);
        endTimeButton.setText(END_TIME_BUTTON_TITLE);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEndTimeAlertDialog();
                endTimeAlertDialog.show();
            }
        });

        addTrainingButton = (Button) findViewById(R.id.btn_add_single_training);
        addTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservationList = new ArrayList<BCReservation>();

                if (checkInputData()) {
                    sendReservationsToFirebase();
                }
            }
        });

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Reserviere");

    }

    private void initBeginDateAlertDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(AddSingleEventActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.calendar_alert_view, null);

        long startDate = 0;

        if (beginDateString != null) {
            SimpleDateFormat endDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try {
                startDate = endDateFormat.parse(beginDateString).getTime();
            } catch (ParseException e) {

            }
        } else {
            startDate = Calendar.getInstance().getTime().getTime();
        }
        CalendarView calendarView = (CalendarView) hintAlertView.findViewById(R.id.calendarView2);
        calendarView.setMinDate(Calendar.getInstance().getTime().getTime());
        calendarView.setDate(startDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                int correctMonth = month + 1;
                beginDateString = String.format("%02d", dayOfMonth) + "." + String.format("%02d", correctMonth) + "." + year;
                dateButton.setText(beginDateString);
                SimpleDateFormat endDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date startDate;
                try {
                    startDate = endDateFormat.parse(beginDateString);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);

                    weekdayInt = cal.get(Calendar.DAY_OF_WEEK);

                } catch (ParseException e) {

                }

                calendarBeginAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        calendarBeginAlertDialog = dialogHintBuilder.create();
    }

    private void initBeginTimeAlertDialog() {
        final ArrayList<String> beginTimes = generateTimes("6:00", true);

        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(AddSingleEventActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.time_alert, null);
        ListView listView = (ListView) hintAlertView.findViewById(R.id.time_list_view);
        ArrayAdapter beginTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, beginTimes);
        listView.setAdapter(beginTimeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beginTimeButton.setText(beginTimes.get(position));
                beginTimeString = beginTimes.get(position);
                endTimeButton.setText(END_TIME_BUTTON_TITLE);
                beginTimeAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        beginTimeAlertDialog = dialogHintBuilder.create();
    }

    private void initEndTimeAlertDialog() {
        final ArrayList<String> endTimes = generateTimes(beginTimeButton.getText().toString(), false);

        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(AddSingleEventActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.time_alert, null);
        ListView listView = (ListView) hintAlertView.findViewById(R.id.time_list_view);
        ArrayAdapter endTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, endTimes);
        listView.setAdapter(endTimeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                endTimeButton.setText(endTimes.get(position));
                endTimeString = endTimes.get(position);
                endTimeAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        endTimeAlertDialog = dialogHintBuilder.create();
    }

    private void initCourtSpinner() {
        courtSpinner = (Spinner) findViewById(R.id.courtSpinner_single);
        ArrayAdapter<CharSequence> courtAdapter = ArrayAdapter.createFromResource(this, R.array.courts, android.R.layout.simple_spinner_item);
        courtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courtSpinner.setAdapter(courtAdapter);
        courtSpinner.setSelection(0);
        courtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        court = 1;
                        break;
                    case 1:
                        court = 2;
                        break;
                    case 2:
                        court = 3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                court = 1;
            }
        });
    }

    private void sendReservationsToFirebase() {
        BCReservation reservation = new BCReservation(FirebaseAuth.getInstance().getCurrentUser().getUid(), beginTimeString, endTimeString, beginDateString, court, 1, name);
        reservationList.add(reservation);

        final ArrayList<String> reservationUuids = new ArrayList<>();

        for (int i = 0; i < reservationList.size(); i++) {
            String reservationsUuid = FirebaseAuth.getInstance().getCurrentUser().getUid() + "-RES-" + UUID.randomUUID().toString();
            reservationUuids.add(reservationsUuid);

            FirebaseDatabase.getInstance().getReference()
                    .child("reservations")
                    .child(reservationsUuid)
                    .setValue(reservationList.get(i).getHashMap());
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBooking(reservationUuids);
            }
        }, 500);

    }

    private void sendBooking(ArrayList<String> reservationList) {

        String bookingUuid = FirebaseAuth.getInstance().getCurrentUser().getUid() + "-BOOK-" + UUID.randomUUID().toString();

        HashMap<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("weekday", weekdayInt);
        bookingMap.put("beginDate", beginDateString);
        bookingMap.put("endDate", beginDateString);
        bookingMap.put("beginTime", beginTimeString);
        bookingMap.put("endTime", endTimeString);
        bookingMap.put("court", court);
        bookingMap.put("reservationIds", reservationList);
        bookingMap.put("name", name);
        bookingMap.put("active", 1);

        FirebaseDatabase.getInstance().getReference()
                .child("bookings")
                .child(bookingUuid)
                .setValue(bookingMap);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Platz wurde reserviert", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AddSingleEventActivity.this, BookingOverviewActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }

    private ArrayList<String> generateTimes(String time, boolean beginTime) {
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);

        int endHour = beginTime ? 20 : 21;
        hour = !beginTime ? hour + 1 : hour;

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

        if (beginTime) {
            timesForList.add("21:00");
        } else {
            timesForList.add("22:00");
        }
        return timesForList;
    }

    private boolean checkInputData() {

        String beginDateButtonTitle = dateButton.getText().toString();
        String beginTimeButtonTitle = beginTimeButton.getText().toString();
        String endTimeButtonTitle = endTimeButton.getText().toString();
        name = trainingName.getText().toString();

        // TODO: check booking names, if == name -> error "names equal, please rename"
        if (name == "Training") {
            initHintAlert("Bitte gib der Einheit einen Namen!");
            hintAlertDialog.show();
            return false;
        } else if (beginDateButtonTitle == START_DATUM_BUTTON_TITLE) {
            initHintAlert("Bitte wähle ein Start Datum aus!");
            hintAlertDialog.show();
            return false;
        } else if (beginTimeButtonTitle == BEGIN_TIME_BUTTON_TITLE) {
            initHintAlert("Bitte wähle eine Start Zeit aus!");
            hintAlertDialog.show();
            return false;
        } else if (endTimeButtonTitle == END_TIME_BUTTON_TITLE) {
            initHintAlert("Bitte wähle eine End Zeit aus!");
            hintAlertDialog.show();
            return false;
        } else {
            progressDialog.show();
            return true;
        }
    }

    private void initHintAlert(String message) {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(AddSingleEventActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.hint, null);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        hintTitleView.setText("Warnung");

        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
        hintMessageView.setText(message);
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        hintAlertDialog = dialogHintBuilder.create();
    }
}
