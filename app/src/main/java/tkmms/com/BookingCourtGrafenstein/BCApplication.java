package tkmms.com.BookingCourtGrafenstein;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

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

            @Override
            public void onCourtClosedFailed() {

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
