package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tkrainz on 08/05/2017.
 */

public class MemberOverviewActivity extends AppCompatActivity {

    private String[] listOptions = new String[] {"Mitglied Eins", "Mitglied zwei", "Mitglied drei"};
    private ArrayList<String> listItems = new ArrayList<>(Arrays.asList(listOptions));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_overview);

        ListView adminListView = (ListView) findViewById(R.id.listView_member);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        adminListView.setAdapter(adapter);

        adminListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MemberDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
