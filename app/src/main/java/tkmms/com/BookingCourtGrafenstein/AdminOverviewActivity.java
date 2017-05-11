package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class AdminOverviewActivity extends AppCompatActivity {

    private String[] listOptions = new String[] {"Mitglied anlegen", "Mitglieder verwalten", "Platz sperren"};
    private ArrayList<String> listItems = new ArrayList<>(Arrays.asList(listOptions));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overview);

        ListView adminListView = (ListView) findViewById(R.id.listview_admin);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
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
                    Intent intent = new Intent(getApplicationContext(), ChooseCloseActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
