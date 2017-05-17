package tkmms.com.BookingCourtGrafenstein;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tkrainz on 16/05/2017.
 */

public class AddTrainingActivity extends AppCompatActivity {

    private Spinner weekdaySpinner, courtSpinner;
    private TextView beginTime, endTime, endDate;
    private Button addTrainingButton;

    private int weekdayInt;
    private String endDateString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        weekdaySpinner = (Spinner) findViewById(R.id.weekdaySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.weekdays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(adapter);
        weekdaySpinner.setSelection(0);
        weekdaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        weekdayInt = 2;
                        break;
                    case 1:
                        weekdayInt = 3;
                        break;
                    case 2:
                        weekdayInt = 4;
                        break;
                    case 3:
                        weekdayInt = 5;
                        break;
                    case 4:
                        weekdayInt = 6;
                        break;
                    case 5:
                        weekdayInt = 7;
                        break;
                    case 6:
                        weekdayInt = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                weekdayInt = 2;
            }
        });

        endDate = (TextView) findViewById(R.id.endDate);
        endDateString = endDate.getText().toString();

        addTrainingButton = (Button) findViewById(R.id.btn_add_training);
        addTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListForWeekday(weekdayInt, endDateString);
            }
        });
    }

    private void getListForWeekday(int day, String lastDayString) {

        Calendar cal = Calendar.getInstance();

        String todayString = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        String currentDayString = todayString;

        ArrayList<String> dateArray = new ArrayList<>();
        while (!currentDayString.equals(lastDayString)) {
            if (cal.get(cal.DAY_OF_WEEK) == day) {
                dateArray.add(new SimpleDateFormat("dd.MM.yyyy").format(new Date(cal.getTime().getTime())));
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            currentDayString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(cal.getTime().getTime()));
        }

        Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
    }
}
