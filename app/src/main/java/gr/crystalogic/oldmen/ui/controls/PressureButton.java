package gr.crystalogic.oldmen.ui.controls;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import gr.crystalogic.oldmen.utils.Constants;

public class PressureButton extends Button {

    private final Handler handler = new Handler();
    private boolean mBooleanIsPressed;

    public void setRunnable(final Runnable runnable) {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handler.postDelayed(runnable, Constants.PRESSURE_SENSITIVITY_TIMEOUT);
                    mBooleanIsPressed = true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                }

                return false;
            }
        });
    }

    public PressureButton(Context context) {
        super(context);
    }

    public PressureButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressureButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
