package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class MemberDetailActivity extends AppCompatActivity {

    BCUser user = new BCUser();
    private ProgressDialog progressDialog = null;
    private AlertDialog hintAlertDialog;
    private TextView hintTitleView, hintMessageView;
    long payment = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        user = (BCUser) getIntent().getSerializableExtra("user");
        final TextView firstNameTV = (TextView) findViewById(R.id.et_detail_first_name);
        final TextView lastNameTV = (TextView) findViewById(R.id.et_detail_last_name);
        final Switch switchDetail = (Switch) findViewById(R.id.switch_detail);
        Button updateMemberButton = (Button) findViewById(R.id.btn_update_member);

        firstNameTV.setText(user.getFirstname());
        lastNameTV.setText(user.getLastname());

        payment = user.getPayment();
        switchDetail.setChecked(payment == 1);
        switchDetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                payment = isChecked ? 1 : 0;
            }
        });

        updateMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                String firstName = firstNameTV.getText().toString();
                String lastName = lastNameTV.getText().toString();

                if (firstName.length() <= 0 || lastName.length() <= 0) {
                    showHintAlertDialog("Hinweis", "Die Eingabe ist nicht vollständig. Bitte überprüfe alle Felder und versuche es dann erneut.");
                } else {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("users").child(user.getId()).child("email").setValue(user.getEmail());
                    database.child("users").child(user.getId()).child("firstname").setValue(firstName);
                    database.child("users").child(user.getId()).child("lastname").setValue(lastName);
                    database.child("users").child(user.getId()).child("admin").setValue(user.getAdmin());
                    database.child("users").child(user.getId()).child("payment").setValue(payment);

                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Mitglied wurde bearbeitet", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }
        });

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Mitglied wird bearbeitet");


        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(MemberDetailActivity.this);
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
            Toast.makeText(this, "Du hast dich erfolgreich abgemeldet", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHintAlertDialog(String title, String message) {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
        hintTitleView.setText(title);
        hintMessageView.setText(message);
        hintAlertDialog.show();
    }
}
