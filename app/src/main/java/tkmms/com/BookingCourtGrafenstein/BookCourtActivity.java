package tkmms.com.BookingCourtGrafenstein;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BookCourtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ListView listVie = (ListView)findViewById(R.id.bookingListView);
        CourtAdapter adapter = new CourtAdapter(getApplicationContext());
        listVie.setAdapter(adapter);
    }
}
