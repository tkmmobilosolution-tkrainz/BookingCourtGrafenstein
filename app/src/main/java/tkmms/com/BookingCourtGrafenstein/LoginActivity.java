package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private boolean isUserAdmin = true;

    private boolean isFirstUserLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUserAdmin) {
                    Intent intent = new Intent(getApplicationContext(), AdminOverviewActivity.class);
                    startActivity(intent);
                } else {

                    if (isFirstUserLogin) {
                        Intent intent = new Intent(getApplicationContext(), FirstLoginActivity.class);
                        startActivity(intent);
                    } else  {
                        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
