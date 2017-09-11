package tkmms.com.BookingCourtGrafenstein.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by tkrainz on 16/05/2017.
 */

public class BCGlobals {

    public static BCGlobals getInstance() {
        return BCApplication.getApplication().getGlobals();
    }

    private static BCUser currentUser;

    public BCUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(BCUser user) {
        currentUser = user;
    }

    public void setUserList(ArrayList<BCUser> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        SharedPreferences.Editor editor = BCApplication.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE).edit();
        editor.putString("users", json);
        editor.apply();
    }

    public ArrayList<BCUser> getUsers() {
        Gson gson = new Gson();
        String json = BCApplication.getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE).getString("users", "");

        ArrayList<BCUser> users = gson.fromJson(json, new TypeToken<ArrayList<BCUser>>(){}.getType());
        return users;
    }
}
