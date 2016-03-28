package gr.crystalogic.contacts.utils;

import android.content.Context;
import android.content.res.TypedArray;

import gr.crystalogic.contacts.R;

public class MeasureHelper {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

}
