package tkmms.com.BookingCourtGrafenstein.authorization;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import tkmms.com.BookingCourtGrafenstein.R;
import tkmms.com.BookingCourtGrafenstein.base.BCUser;
import tkmms.com.BookingCourtGrafenstein.member.CalendarActivity;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private AlertDialog hintAlertDialog;
    private TextView hintTitleView, hintMessageView;
    FirebaseAuth authentication = FirebaseAuth.getInstance();
    private EditText email, pwd, checkPwd;
    ArrayList<String> emailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        getValidEmails();
        getSupportActionBar().setTitle("Passwort ändern");

        email = (EditText) findViewById(R.id.et_email);
        pwd = (EditText) findViewById(R.id.et_fist_password);
        checkPwd = (EditText) findViewById(R.id.et_second_password);

        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(RegisterActivity.this);
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

        Button loginButton = (Button)findViewById(R.id.btnChangePwd);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerAction();
            }
        });

        if (isOnline()){}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getValidEmails() {

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("validemails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> emailsMap = (Map<String, String>) dataSnapshot.getValue();

                if (emailsMap != null) {

                    TreeMap<String, String> treemap = new TreeMap<String, String>(emailsMap);

                    Iterator basicIterator = treemap.entrySet().iterator();
                    while (basicIterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) basicIterator.next();

                        String emailAddress = (String) pair.getValue();
                        emailList.add(emailAddress);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean checkValidEmail(final String email) {

        if (emailList.size() == 0) {
            return false;
        }

        if (emailList.contains(email)) {
            return true;
        }

        return false;
    }

    private boolean emailFormat(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean pwdFormat(String pwd) {
        return pwd.length() >= 8;
    }

    private void showHintAlertDialog(String title, String message) {
        hintTitleView.setText(title);
        hintMessageView.setText(message);
        hintAlertDialog.show();
    }
    private void signInFailed(Task<AuthResult> task) {
        FirebaseAuthException exception = (FirebaseAuthException) task.getException();

        if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
            showHintAlertDialog("Fehler", "Die Email Adresse wird schon verwendet. Bitte gib eine andere Adresse ein.");
        } else {
            showHintAlertDialog("Fehler", "Ein Fehler ist aufgetretten. Versuche es später noch einmal.");
        }
    }

    private void registerAction() {
        if (!isOnline()) {
            return;
        }

        String emailString = email.getText().toString();
        String password = pwd.getText().toString();
        String checkPassword = checkPwd.getText().toString();

        if (emailFormat(emailString)) {
            if (checkValidEmail(emailString)) {
                if (pwdFormat(password)) {
                    if (password.equals(checkPassword)) {

                        authentication.createUserWithEmailAndPassword(emailString, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    signInFailed(task);
                                }

                                startActivity(new Intent(RegisterActivity.this, CalendarActivity.class));
                            }
                        });
                    } else {
                        showHintAlertDialog("Hinweis", "Deine Passwörter stimmen nicht über ein, bitte korrigiere deine Eingabe.");
                    }
                }
            } else {
                showHintAlertDialog("Hinweis", "Deine Email Addresse wurde im System noch nicht freigegeben. Bitte überprüfe deine Eingbae oder Kontaktiere deinen Obmann.");
            }
        } else {
            showHintAlertDialog("Hinweis", "Die von dir angegebene Email Adresse entspricht nicht dem gewünschten Format.\n\nBeispiel: bookingcourt@beispiel.com");
        }
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
