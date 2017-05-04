package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

import java.util.Calendar;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(getApplicationContext(), BookCourtActivity.class);
                startActivity(intent);
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
}
