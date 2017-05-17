package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Lade Daten");
        progressDialog.show();

        loadParametters();
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
        adminListView.setAdapter(adapter);
        progressDialog.dismiss();
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

                    isCourtClosed = isCourtClosed == 1 ? 0 : 1;
                    BCApplication.getApplication().getDatabaseHelper().setCourtClosed(isCourtClosed);

                    progressDialog.show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loadParametters();
                        }
                    }, 1000);

                    String toastString = isCourtClosed == 1 ? "Platz wurde gesperrt" : "Platz wurde freigegeben";
                    Toast.makeText(AdminOverviewActivity.this, toastString, Toast.LENGTH_LONG).show();
                } else if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), AddTrainingActivity.class);
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
            listOptions[2] = "Platz freigeben";
        } else {
            listOptions[2] = "Platz sperren";
        }

        listOptions[3] = "Training hinzufügen";

        setListWithArray();
    }
}
