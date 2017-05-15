package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        final EditText etEmail = (EditText) findViewById(R.id.et_add_email);
        final EditText etFirstName = (EditText) findViewById(R.id.et_add_firstname);
        final EditText etLastName = (EditText) findViewById(R.id.et_add_lastname);
        final Switch paymentSwitch = (Switch)findViewById(R.id.add_switch);

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
                email = etEmail.getText().toString();
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();

                registerMemeberWithEmial();
            }
        });
    }

    private void createUserFailed(Task<AuthResult> task) {
        FirebaseAuthException exception = (FirebaseAuthException) task.getException();
        if (exception.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
            // email in use
        } else {
            // default error
        }
    }

    private void registerMemeberWithEmial() {
        authentication.createUserWithEmailAndPassword(email, "pwd4test").addOnCompleteListener(AddMemberActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    createUserFailed(task);
                } else {

                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(email);
                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("firstname").setValue(firstName);
                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lastname").setValue(lastName);
                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("admin").setValue(admin);
                    database.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("payment").setValue(payment);

                    FirebaseAuth.getInstance().signOut();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword("bc.grafenstein@gmail.com", "pwd4admin").addOnCompleteListener(AddMemberActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                        }
                    });
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
            // TODO: shared prefs bcuser = null
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("USER");
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
