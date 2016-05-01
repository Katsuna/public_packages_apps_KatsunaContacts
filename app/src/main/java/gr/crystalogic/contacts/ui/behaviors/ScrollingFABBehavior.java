package gr.crystalogic.contacts.ui.behaviors;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

public class ScrollingFABBehavior extends FloatingActionButton.Behavior {

    private Handler handler;
    private Runnable runnable;
    private DateTime lastScroll;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
        handler = new Handler();

    }

    public boolean onStartNestedScroll(CoordinatorLayout parent, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               final FloatingActionButton child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        lastScroll = new DateTime();
        child.hide();
        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (lastScroll.plusMillis(1000).isBefore(new DateTime())) {
                    child.show();
                }
            }
        };
        handler.postDelayed(runnable, 1001);
    }
}