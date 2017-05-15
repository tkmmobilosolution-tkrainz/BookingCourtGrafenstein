package tkmms.com.BookingCourtGrafenstein;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tkrainz on 15/05/2017.
 */

public class MemberListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BCUser> users;

    public MemberListAdapter(Context context, ArrayList<BCUser> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.member_list_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.memberName);
        String name = users.get(position).getLastname() + ", " + users.get(position).getFirstname();
        tv.setText(name);

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkbox.setChecked(users.get(position).getPayment() == 1);

        return convertView;
    }
}
