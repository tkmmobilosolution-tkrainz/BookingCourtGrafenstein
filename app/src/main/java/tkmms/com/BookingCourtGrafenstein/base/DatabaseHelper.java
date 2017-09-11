package tkmms.com.BookingCourtGrafenstein.base;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tkrainz on 16/05/2017.
 */

public class DatabaseHelper {

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference courtRef = FirebaseDatabase.getInstance().getReference().child("basic_permissions").child("isCourtClosed");

    public void getUsers(final DatabaseUserListListener listener) {
        final ArrayList<BCUser> users = new ArrayList<>();
        userRef.addValueEventListener(new ValueEventListener() {
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
                    users.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onUsersFailed();
            }
        });
    }

    public void getCourtClosed(final DatabaseCourtClosedListener listener) {
        courtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onCourtClosedSuccedded((long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setCourtClosed(long courtClosed) {
        courtRef.setValue(courtClosed);

    }

    public interface DatabaseUserListListener {

        void onUsersSucceded(ArrayList<BCUser> users);
        void onUsersFailed();
    }

    public interface DatabaseCourtClosedListener {
        void onCourtClosedSuccedded(long isCourtClosed);
    }
}
