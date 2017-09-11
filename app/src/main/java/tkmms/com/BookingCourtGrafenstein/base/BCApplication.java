package tkmms.com.BookingCourtGrafenstein.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import tkmms.com.BookingCourtGrafenstein.R;
import tkmms.com.BookingCourtGrafenstein.authorization.LoginActivity;
import tkmms.com.BookingCourtGrafenstein.member.BookCourtActivity;

/**
 * Created by tkrainz on 16/05/2017.
 */

public class BCApplication extends Application {

    private static BCGlobals globals = null;

    private static DatabaseHelper dbHelper = null;

    private static BCApplication application = null;

    private static long courtClosed = 0;

    public BCGlobals getGlobals() {
        initGlobals();
        return globals;
    }

    public DatabaseHelper getDatabaseHelper() {
        initDatabaseHelper();
        return dbHelper;
    }

    public ArrayList<BCUser> getUserList() {
        return globals.getUsers();
    }

    public long getCourtClosed() {
        return courtClosed;
    }

    /**
     * Returns the application context.
     *
     * @return The application context.
     */
    public static Context getContext() {
        return application;
    }

    /**
     * Gets the current application.
     *
     * @return the application.
     */
    public static BCApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        initGlobals();
        initDatabaseHelper();
        dbHelper.getCourtClosed(new DatabaseHelper.DatabaseCourtClosedListener() {
            @Override
            public void onCourtClosedSuccedded(long isCourtClosed) {
                courtClosed = isCourtClosed;
            }
        });

        dbHelper.getUsers(new DatabaseHelper.DatabaseUserListListener() {
            @Override
            public void onUsersSucceded(ArrayList<BCUser> users) {
                globals.setUserList(users);
            }

            @Override
            public void onUsersFailed() {
                globals.setUserList(null);
            }
        });
    }

    private static void initGlobals() {
        if (globals == null) {
            globals = new BCGlobals();
        }
    }

    private static void initDatabaseHelper() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper();
        }
    }
}
