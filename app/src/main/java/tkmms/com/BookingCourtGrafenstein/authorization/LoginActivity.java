package tkmms.com.BookingCourtGrafenstein.authorization;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tkmms.com.BookingCourtGrafenstein.R;
import tkmms.com.BookingCourtGrafenstein.admin.overview.AdminOverviewActivity;
import tkmms.com.BookingCourtGrafenstein.base.BCGlobals;
import tkmms.com.BookingCourtGrafenstein.base.BCUser;
import tkmms.com.BookingCourtGrafenstein.member.CalendarActivity;
import tkmms.com.BookingCourtGrafenstein.member.FirstLoginActivity;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private boolean isFirstUserLogin = false;

    FirebaseAuth authentication = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authListener = null;
    private AlertDialog hintAlertDialog;
    private ProgressDialog progressDialog = null;
    private EditText emailET, passwordET;
    private TextView hintTitleView, hintMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Anmelden");

        Button loginButton = (Button)findViewById(R.id.btnLogin);

        emailET = (EditText) findViewById(R.id.et_login_email);
        passwordET = (EditText) findViewById(R.id.et_login_password);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Anmelden");

        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(LoginActivity.this);
        final View hintAlertView = inflater.inflate(R.layout.hint, null);
        hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintAlertDialog.dismiss();
            }
        });

        dialogHintBuilder.setView(hintAlertView);
        hintAlertDialog = dialogHintBuilder.create();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    handleDatabaseUser(user);
                }
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

        if (isOnline()) {}
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authListener != null) {
            authentication.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (authListener != null) {
            authentication.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authListener != null) {
            authentication.removeAuthStateListener(authListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
        authentication.addAuthStateListener(authListener);
    }

    private boolean passwordFormat(String password) {
        return password.length() >= 8;
    }

    private boolean emailFormat(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void loginAction() {

        if (!isOnline()) {
            return;
        }

        progressDialog.show();

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        isFirstUserLogin = password.equals("pwd4test");

        if (emailFormat(email) && passwordFormat(password)) {
            authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        signInFailed(task);
                    }
                }
            });
        } else if (!emailFormat(email)) {
            showHintAlertDialog("Hinweis", "Die von dir angegebene Email Adresse entspricht nicht dem gewünschten Format.\n\nBeispiel: bookingcourt@beispiel.com");
        } else if (!passwordFormat(password)) {
            showHintAlertDialog("Hinweis", "Das von dir angegebene Passwort entspricht nicht dem gewünschten Format.\n\nDas Passwort muss mindestens acht Zeichen lang sein.");
        }
    }

    private void handleDatabaseUser(FirebaseUser user) {

        if (!isOnline()) {
            return;
        }

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BCUser databaseUser = dataSnapshot.getValue(BCUser.class);

                if (databaseUser != null) {
                    BCGlobals.getInstance().setCurrentUser(databaseUser);

                    if (databaseUser.getAdmin() == 1) {
                        Intent intent = new Intent(getApplicationContext(), AdminOverviewActivity.class);
                        startActivity(intent);
                    } else {

                        if (isFirstUserLogin) {
                            Intent intent = new Intent(getApplicationContext(), FirstLoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                            startActivity(intent);
                        }
                    }

                    progressDialog.hide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (authListener != null) {
            authentication.removeAuthStateListener(authListener);
        }
    }

    private void signInFailed(Task<AuthResult> task) {
        FirebaseAuthException exception = (FirebaseAuthException) task.getException();

        if (exception.getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
            showHintAlertDialog("Fehler", "Es wurde kein Konto zu der von dir gewählten Email Adresse gefunden. Bitte kontaktiere deinen Obmann um mehr dazu zu erfahren.");
        } else if (exception.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
            showHintAlertDialog("Fehler", "Das Passwort für diese Email Adresse ist falsch. Bitte überprüfe deine Eingabe und probier es erneut.");
        } else {
            showHintAlertDialog("Fehler", "Ein Fehler ist aufgetretten. Versuche es später noch einmal.");
        }
    }

    private void showHintAlertDialog(String title, String message) {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
        hintTitleView.setText(title);
        hintMessageView.setText(message);
        hintAlertDialog.show();
    }

    private boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {

            authentication.removeAuthStateListener(authListener);

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
