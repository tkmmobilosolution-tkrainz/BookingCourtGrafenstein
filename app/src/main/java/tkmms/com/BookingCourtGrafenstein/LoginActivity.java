package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.btnLogin);

        emailET = (EditText) findViewById(R.id.et_login_email);
        passwordET = (EditText) findViewById(R.id.et_login_password);

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Anmelden");

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkUserState(user);
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });
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
        } else if (!passwordFormat(password)) {
        }
    }

    private void checkUserState(FirebaseUser user) {
        if (user != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            final String localUser = prefs.getString("USER", null);

            if (localUser == null || localUser.equals("")) {
                handleDatabaseUser(user);
            } else {
                setLocalUser(localUser);
            }
        }
    }

    private void handleDatabaseUser(FirebaseUser user) {
        final Gson gson = new Gson();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BCUser databaseUser = dataSnapshot.getValue(BCUser.class);

                if (databaseUser != null) {

                    String dbUserJson = gson.toJson(databaseUser);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER", dbUserJson);
                    editor.apply();

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
    }

    private void setLocalUser(String userJson) {
        final Gson gson = new Gson();
        BCUser savedUser = gson.fromJson(userJson, BCUser.class);

        if (savedUser == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("USER", userJson);
            editor.apply();
        }

        progressDialog.hide();
    }

    private void signInFailed(Task<AuthResult> task) {
        FirebaseAuthException exception = (FirebaseAuthException) task.getException();

        if (exception.getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
            // user not able
        } else if (exception.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
            // wrong pwd
        } else {
            // db error
        }
    }
}
