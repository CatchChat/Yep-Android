package catchla.yep.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by mariotaku on 14-8-23.
 */
public class GestureViewHelper {

    private final Context mContext;
    private GestureDetector mGestureDetector;
    private GestureDetector.OnGestureListener mGestureListener;

    public GestureViewHelper(Context context) {
        mContext = context;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector == null) return false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (mGestureListener instanceof OnUpListener) {
                    ((OnUpListener) mGestureListener).onUp(ev);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                if (mGestureListener instanceof OnCancelListener) {
                    ((OnCancelListener) mGestureListener).onCancel(ev);
                }
                break;
            }
        }
        return mGestureDetector.onTouchEvent(ev);
    }

    public void setOnGestureListener(GestureDetector.OnGestureListener listener) {
        mGestureListener = listener;
        if (listener == null) {
            mGestureDetector = null;
        } else {
            mGestureDetector = new GestureDetector(mContext, listener);
            mGestureDetector.setIsLongpressEnabled(false);
        }
    }

    public interface OnCancelListener {
        void onCancel(MotionEvent event);
    }


    public interface OnUpListener {
        void onUp(MotionEvent event);
    }
}
