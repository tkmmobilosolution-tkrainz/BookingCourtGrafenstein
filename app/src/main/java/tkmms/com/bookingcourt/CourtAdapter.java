package tkmms.com.bookingcourt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class CourtAdapter extends BaseAdapter {

    private Context context;

    public CourtAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.booking_list_item, parent, false);
        }

        BCButton courtOne = (BCButton)convertView.findViewById(R.id.btnCourt1);
        courtOne.setButtonBackgroundForState("Frei");

        BCButton courtTwo = (BCButton)convertView.findViewById(R.id.btnCourt2);
        courtTwo.setButtonBackgroundForState("Besetzt");

        return convertView;
    }
}
