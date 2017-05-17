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

    public BCGlobals getGlobals() {
        initGlobals();
        return globals;
    }
    public DatabaseHelper getDatabaseHelper() {
        initDatabaseHelper();
        return dbHelper;
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

        initGlobals();
        initDatabaseHelper();
        application = this;
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
