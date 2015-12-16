package catchla.yep.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.AnimationUtilsTrojan;
import android.support.design.widget.FloatingActionButtonImplTrojan;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mariotaku on 15/12/14.
 */
public class FloatingActionMenu extends CardView {
    private boolean mIsHiding;

    public FloatingActionMenu(final Context context) {
        super(context);
    }

    public FloatingActionMenu(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionMenu(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void hide() {
        hide(null);
    }

    public void show() {
        show(null);
    }

    public void show(final InternalVisibilityChangedListener listener) {
        if (this.getVisibility() != View.VISIBLE) {
            if (ViewCompat.isLaidOut(this) && !this.isInEditMode()) {
                this.setAlpha(0f);
                this.setScaleY(0f);
                this.setScaleX(0f);
                this.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(FloatingActionButtonImplTrojan.SHOW_HIDE_ANIM_DURATION)
                        .setInterpolator(AnimationUtilsTrojan.FAST_OUT_SLOW_IN_INTERPOLATOR)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                FloatingActionMenu.this.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (listener != null) {
                                    listener.onShown();
                                }
                            }
                        });
            } else {
                this.setVisibility(View.VISIBLE);
                this.setAlpha(1f);
                this.setScaleY(1f);
                this.setScaleX(1f);
                if (listener != null) {
                    listener.onShown();
                }
            }
        }
    }

    public void hide(final InternalVisibilityChangedListener listener) {
        if (mIsHiding || getVisibility() != View.VISIBLE) {
            // A hide animation is in progress, or we're already hidden. Skip the call
            if (listener != null) {
                listener.onHidden();
            }
            return;
        }

        if (!ViewCompat.isLaidOut(this) || isInEditMode()) {
            // If the view isn't laid out, or we're in the editor, don't run the animation
            this.setVisibility(View.GONE);
            if (listener != null) {
                listener.onHidden();
            }
        } else {
            animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(FloatingActionButtonImplTrojan.SHOW_HIDE_ANIM_DURATION)
                    .setInterpolator(AnimationUtilsTrojan.FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mIsHiding = true;
                            FloatingActionMenu.this.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mIsHiding = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mIsHiding = false;
                            FloatingActionMenu.this.setVisibility(View.GONE);
                            if (listener != null) {
                                listener.onHidden();
                            }
                        }
                    });
        }
    }


    interface InternalVisibilityChangedListener {
        void onShown();

        void onHidden();
    }
}
