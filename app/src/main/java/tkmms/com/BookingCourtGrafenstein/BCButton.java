package tkmms.com.BookingCourtGrafenstein;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

/**
 * Created by tkrainz on 03/05/2017.
 */

public class BCButton extends android.widget.Button {

    private Context context;
    public BCButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setButtonBackgroundForState(String state) {

        if (state.contains("Frei")) {
            this.setBackgroundTintList(generateColorStateList(R.color.bc_green));
        } else if (state.contains("Training")){
            this.setBackgroundTintList(generateColorStateList(R.color.bc_blue));
        } else {
            this.setBackgroundTintList(generateColorStateList(R.color.bc_red));
        }
    }

    private ColorStateList generateColorStateList(int colorResource) {
        return ColorStateList.valueOf(ContextCompat.getColor(context, colorResource));
    }
}
