package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by tkrainz on 11/05/2017.
 */

public class ChooseCloseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_close);

        Button btnChoose = (Button)findViewById(R.id.btn_choose_time);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CloseCourtActivity.class);
                startActivity(intent);
            }
        });
    }

}
