package tkmms.com.bookingcourt;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BCButton extends android.widget.Button {

    public BCButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setButtonBackgroundForState(String state) {

        if (state.equals("Frei")) {
            this.setBackgroundColor(Color.GREEN);
        } else if (state.equals("Besetzt")) {
            this.setBackgroundColor(Color.RED);
        } else {
            this.setBackgroundColor(Color.YELLOW);
        }
    }
}
