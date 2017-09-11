package tkmms.com.BookingCourtGrafenstein.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import tkmms.com.BookingCourtGrafenstein.authorization.LoginActivity;
import tkmms.com.BookingCourtGrafenstein.R;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class CalendarActivity extends AppCompatActivity {

    private long closed;
    private AlertDialog deleteAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getSupportActionBar().setTitle("Termin wählen");

        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(CalendarActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.hint, null);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        hintTitleView.setText("Achtung");

        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
        hintMessageView.setText("Die Plätze sind momentan gesperrt. Bitte versuch es später erneut.");
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        deleteAlertDialog = dialogHintBuilder.create();

        CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, final int year, final int month, final int dayOfMonth) {

                FirebaseDatabase.getInstance().getReference().child("basic_permissions").child("isCourtClosed").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        closed = ((long) dataSnapshot.getValue());

                        if (closed == 0) {
                            Intent intent = new Intent(getApplicationContext(), BookCourtActivity.class);
                            int curMonth = month + 1;
                            String date = String.format("%02d", dayOfMonth) + "." + String.format("%02d", curMonth) + "." + year;
                            intent.putExtra("date", date);
                            startActivity(intent);
                        } else {
                            deleteAlertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Calendar cal = Calendar.getInstance();
        long today = cal.getTime().getTime();
        cal.add(Calendar.DAY_OF_YEAR, 14);

        long twoWeeks = cal.getTime().getTime();

        calendarView.setMinDate(today);
        calendarView.setMaxDate(twoWeeks);
    }

    @Override
    public void onBackPressed() {
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
