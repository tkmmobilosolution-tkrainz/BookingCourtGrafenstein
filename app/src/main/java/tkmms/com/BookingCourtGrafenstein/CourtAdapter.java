package tkmms.com.BookingCourtGrafenstein;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

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

        BCButton court3 = (BCButton)convertView.findViewById(R.id.btnCourt3);
        court3.setButtonBackgroundForState("Training");
        BCButton court4 = (BCButton)convertView.findViewById(R.id.btnCourt4);
        BCButton court5 = (BCButton)convertView.findViewById(R.id.btnCourt5);
        BCButton court6 = (BCButton)convertView.findViewById(R.id.btnCourt6);
        BCButton court7 = (BCButton)convertView.findViewById(R.id.btnCourt7);
        BCButton court8 = (BCButton)convertView.findViewById(R.id.btnCourt8);
        BCButton court9 = (BCButton)convertView.findViewById(R.id.btnCourt9);
        BCButton court10 = (BCButton)convertView.findViewById(R.id.btnCourt10);

        court4.setVisibility(View.GONE);
        court4.setButtonBackgroundForState("Test");
        court5.setVisibility(View.GONE);
        court6.setVisibility(View.GONE);
        court7.setVisibility(View.GONE);
        court8.setVisibility(View.GONE);
        court9.setVisibility(View.GONE);
        court10.setVisibility(View.GONE);

        return convertView;
    }
}
