package tkmms.com.BookingCourtGrafenstein.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import tkmms.com.BookingCourtGrafenstein.R;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class FirstLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        getSupportActionBar().setTitle("Passwort ändern");

        final EditText pwd = (EditText) findViewById(R.id.et_fist_password);
        final EditText checkPwd = (EditText) findViewById(R.id.et_second_password);

        Button loginButton = (Button)findViewById(R.id.btnChangePwd);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = pwd.getText().toString();
                String checkPassword = checkPwd.getText().toString();

                if (password.equals(checkPassword)) {

                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {

                            } else {
                                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
    }
}
