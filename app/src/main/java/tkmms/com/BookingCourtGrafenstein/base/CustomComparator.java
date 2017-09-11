package tkmms.com.BookingCourtGrafenstein.base;

import java.util.Comparator;

/**
 * Created by tkrainz on 15/05/2017.
 */

public class CustomComparator implements Comparator<BCUser> {
    @Override
    public int compare(BCUser o1, BCUser o2) {
        String compareString1 = o1.getLastname() + ", " + o1.getFirstname();
        String compareString2 = o2.getLastname() + ", " + o2.getFirstname();

        return compareString1.compareTo(compareString2);
    }
}
