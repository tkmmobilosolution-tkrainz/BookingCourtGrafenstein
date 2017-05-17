package tkmms.com.BookingCourtGrafenstein;

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
}
