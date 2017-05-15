package tkmms.com.BookingCourtGrafenstein;

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

    private String[] listOptions = new String[] {"Mitglied Eins", "Mitglied zwei", "Mitglied drei"};
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<BCUser> users = new ArrayList<BCUser>();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");


    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Map<String, BCUser> userMap = (Map<String, BCUser>) dataSnapshot.getValue();
            if (userMap != null) {

                TreeMap<String, BCUser> treemap = new TreeMap<String, BCUser>(userMap);

                Iterator basicIterator = treemap.entrySet().iterator();
                while (basicIterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) basicIterator.next();


                    HashMap<String, String> userDetaiMap = (HashMap<String, String>) pair.getValue();
                    String admin = String.valueOf(userDetaiMap.get("admin"));
                    String payment = String.valueOf(userDetaiMap.get("payment"));

                    BCUser currentUser = new BCUser();
                    currentUser.setLastname(userDetaiMap.get("lastname"));
                    currentUser.setAdmin(Long.parseLong(admin));
                    currentUser.setFirstname(userDetaiMap.get("firstname"));
                    currentUser.setEmail(userDetaiMap.get("email"));
                    currentUser.setPayment(Long.parseLong(payment));
                    currentUser.setId((String) pair.getKey());

                    users.add(currentUser);
                }
            } else {
                users = null;
            }

            showMemberList();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_overview);

    }

    @Override
    protected void onResume() {
        super.onResume();
        database.addValueEventListener(eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.removeEventListener(eventListener);
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

    private void showMemberList() {

        if (users != null) {
            Collections.sort(users, new CustomComparator());

            for (BCUser currentUser: users) {
                listItems.add(currentUser.getLastname() + ", " + currentUser.getFirstname());
            }

            ListView adminListView = (ListView) findViewById(R.id.listView_member);
            MemberListAdapter adapter = new MemberListAdapter(this, users);
            adminListView.setAdapter(adapter);

            adminListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BCUser clickedUser = users.get(position);

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
