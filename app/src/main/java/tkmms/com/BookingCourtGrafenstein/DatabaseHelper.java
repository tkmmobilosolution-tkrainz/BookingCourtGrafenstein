package tkmms.com.BookingCourtGrafenstein;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tkrainz on 16/05/2017.
 */

public class DatabaseHelper {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void getUsers(final DatabaseUserListListener listener) {
        final ArrayList<BCUser> users = new ArrayList<>();
        final DatabaseReference ref = database.child("users");
        ref.addValueEventListener(new ValueEventListener() {
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

                        if (currentUser.getAdmin() != 1) {
                            users.add(currentUser);
                        }
                    }

                    listener.onUsersSucceded(users);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onUsersFailed();
            }
        });
    }

    public interface DatabaseUserListListener {

        void onUsersSucceded(ArrayList<BCUser> users);
        void onUsersFailed();
    }
}