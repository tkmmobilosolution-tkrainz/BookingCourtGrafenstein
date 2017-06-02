package tkmms.com.BookingCourtGrafenstein;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class MemberOverviewActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<BCUser> usersList = new ArrayList<BCUser>();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_overview);

        getSupportActionBar().setTitle("Mitglieder Übersicht");
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = new ProgressDialog(this, R.style.SpinnerTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Daten werden geladen");
        progressDialog.show();

        usersList = BCApplication.getApplication().getUserList();
        showMemberList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    private void showMemberList() {

        if (usersList != null) {
            Collections.sort(usersList, new CustomComparator());

            for (BCUser currentUser: usersList) {
                listItems.add(currentUser.getLastname() + ", " + currentUser.getFirstname());
            }

            ListView adminListView = (ListView) findViewById(R.id.listView_member);
            MemberListAdapter adapter = new MemberListAdapter(this, usersList);
            adminListView.setAdapter(adapter);
            progressDialog.hide();

            adminListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BCUser clickedUser = usersList.get(position);

                    Intent intent = new Intent(getApplicationContext(), MemberDetailActivity.class);
                    intent.putExtra("user", clickedUser);
                    startActivity(intent);
                }
            });
        } else {
            showHint();
        }
    }

    private void showHint() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View hintAlertView = inflater.inflate(R.layout.hint, null);

        final AlertDialog.Builder dialogHintBuilder = new AlertDialog.Builder(this);
        TextView hintTitleView = (TextView) hintAlertView.findViewById(R.id.hintTitleTextView);
        TextView hintMessageView = (TextView) hintAlertView.findViewById(R.id.hintMessageTextView);

        hintTitleView.setText("Hinweis");
        hintMessageView.setText("Im Verein sind noch keine Mitglieder registriert.");
        final Button hintButton = (Button) hintAlertView.findViewById(R.id.hintButton);
        dialogHintBuilder.setView(hintAlertView);
        final AlertDialog hintAlertDialog = dialogHintBuilder.create();

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintAlertDialog.dismiss();
                onBackPressed();
            }
        });

        hintAlertDialog.show();
    }


}
