package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class AdminOverviewActivity extends AppCompatActivity {

    DatabaseReference database;
    ArrayAdapter<String> adapter;
    private String[] listOptions = new String[3];
    long isCourtClosed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        database = FirebaseDatabase.getInstance().getReference().child("isCourtClosed");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listOptions[0] = "Mitglied anlegen";
                listOptions[1] = "Mitglieder verwalten";

                isCourtClosed = (long) dataSnapshot.getValue();
                if (isCourtClosed == 1) {
                    listOptions[2] = "Platz freigeben";
                } else {
                    listOptions[2] = "Platz sperren";
                }

                setListWithArray();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void onBackPressed() {
    }

    private void setListWithArray() {
        ArrayList<String> listItems = new ArrayList<>(Arrays.asList(listOptions));
        final ListView adminListView = (ListView) findViewById(R.id.listview_admin);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        adminListView.setAdapter(adapter);

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
                    FirebaseDatabase.getInstance().getReference().child("isCourtClosed").setValue(isCourtClosed);
                    if (isCourtClosed == 1) {
                        listOptions[2] = "Platz freigeben";
                    } else {
                        listOptions[2] = "Platz sperren";
                    }
                    ArrayList<String> listItems = new ArrayList<>(Arrays.asList(listOptions));
                    adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    adminListView.invalidateViews();

                    String toastString = isCourtClosed == 1 ? "Platz wurde gesperrt" : "Platz wurde freigegeben";
                    Toast.makeText(AdminOverviewActivity.this, toastString, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
