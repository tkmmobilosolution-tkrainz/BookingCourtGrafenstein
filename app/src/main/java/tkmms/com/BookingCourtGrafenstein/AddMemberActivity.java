package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class AddMemberActivity extends AppCompatActivity {

    private FirebaseAuth authentication = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener = null;
    private String email, firstName, lastName;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private long payment = 0;
    private long admin = 0;
    private ProgressDialog progressDialog = null;
    private TextView hintTitleView, hintMessageView;
    private AlertDialog hintAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        final EditText etEmail = (EditText) findViewById(R.id.et_add_email);
        final EditText etFirstName = (EditText) findViewById(R.id.et_add_firstname);
        final EditText etLastName = (EditText) findViewById(R.id.et_add_lastname);
        final Switch paymentSwitch = (Switch)findViewById(R.id.add_switch);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Mitglied wird angelegt");


        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(AddMemberActivity.this);
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

        paymentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                payment = isChecked ? 1 : 0;
            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                }
            }
        };

        Button addMemeberButton = (Button)findViewById(R.id.btn_add_member);
        addMemeberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                email = etEmail.getText().toString();
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();

                if (!emailFormat(email)) {
                    showHintAlertDialog("Hinweis", "Die von dir angegebene Email Adresse entspricht nicht dem gewünschten Format.\n\nBeispiel: bookingcourt@beispiel.com");
                } else if (firstName.length() <= 0 || lastName.length() <= 0) {
                    showHintAlertDialog("Hinweis", "Die Eingabe ist nicht vollständig. Bitte überprüfe alle Felder und versuche es dann erneut.");
                } else {
                    registerMemeberWithEmial();
                }
            }
        });
    }

    private void createUserFailed(Task<AuthResult> task) {
        progressDialog.dismiss();
        FirebaseAuthException exception = (FirebaseAuthException) task.getException();
        if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
            showHintAlertDialog("Fehler", "Die Email Adresse wird schon verwendet. Bitte gib eine andere Adresse ein.");
        } else {
            showHintAlertDialog("Fehler", "Ein Fehler ist aufgetretten. Versuche es später noch einmal.");
        }
    }

    private void registerMemeberWithEmial() {
        authentication.createUserWithEmailAndPassword(email, "pwd4test").addOnCompleteListener(AddMemberActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    createUserFailed(task);
                } else {

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("email", email);
                    map.put("firstname", firstName);
                    map.put("lastname", lastName);
                    map.put("admin", admin);
                    map.put("payment", payment);
                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);

                    FirebaseAuth.getInstance().signOut();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword("bc.grafenstein@gmail.com", "pwd4admin").addOnCompleteListener(AddMemberActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                        }
                    });

                    Toast.makeText(getApplicationContext(), "Mitglied wurde angelegt", Toast.LENGTH_LONG).show();
                    onBackPressed();

                }
            }
        });
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

    private boolean emailFormat(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }


    private void showHintAlertDialog(String title, String message) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        hintTitleView.setText(title);
        hintMessageView.setText(message);
        hintAlertDialog.show();
    }
}
