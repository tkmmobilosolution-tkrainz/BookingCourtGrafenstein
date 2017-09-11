package tkmms.com.BookingCourtGrafenstein.admin.overview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;

import tkmms.com.BookingCourtGrafenstein.base.BCApplication;
import tkmms.com.BookingCourtGrafenstein.admin.bookings.BookingOverviewActivity;
import tkmms.com.BookingCourtGrafenstein.authorization.LoginActivity;
import tkmms.com.BookingCourtGrafenstein.R;
import tkmms.com.BookingCourtGrafenstein.admin.member.AddMemberActivity;
import tkmms.com.BookingCourtGrafenstein.admin.member.MemberOverviewActivity;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class AdminOverviewActivity extends AppCompatActivity {

    DatabaseReference database;
    ArrayAdapter<String> adapter;
    private String[] listOptions = new String[4];
    long isCourtClosed;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        getSupportActionBar().setTitle("Admin Übersicht");
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        showProgressBarWithMessage("Lade Daten");

        if (isOnline()) {
            loadParametters();
        }
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

    @Override
    public void onBackPressed() {
    }

    private void setListWithArray() {
        ArrayList<String> listItems = new ArrayList<>(Arrays.asList(listOptions));
        final ListView adminListView = (ListView) findViewById(R.id.listview_admin);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);

        if (!this.isDestroyed()) {
            adminListView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        adminListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), MemberOverviewActivity.class);
                    startActivity(intent);
                } else if (position == 2) {

                    if (!isOnline()) {
                        return;
                    }

                    isCourtClosed = isCourtClosed == 1 ? 0 : 1;
                    BCApplication.getApplication().getDatabaseHelper().setCourtClosed(isCourtClosed);

                    showProgressBarWithMessage(isCourtClosed == 1 ? "Platz wird gesperrt" : "Platz wird freigegeben");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String toastString = isCourtClosed == 1 ? "Platz wurde gesperrt" : "Platz wurde freigegeben";
                            Toast.makeText(AdminOverviewActivity.this, toastString, Toast.LENGTH_LONG).show();
                            loadParametters();
                        }
                    }, 1000);

                } else if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), BookingOverviewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadParametters() {
        listOptions[0] = "Mitglied anlegen";
        listOptions[1] = "Mitglieder verwalten";

        isCourtClosed = BCApplication.getApplication().getCourtClosed();
        if (isCourtClosed == 1) {
            listOptions[2] = "Platz ist gesperrt";
        } else {
            listOptions[2] = "Platz ist freigegeben";
        }

        listOptions[3] = "Einheiten";

        setListWithArray();
    }

    private void showProgressBarWithMessage(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
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
