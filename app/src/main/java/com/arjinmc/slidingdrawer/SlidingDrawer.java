package com.arjinmc.slidingdrawer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * SlidingDrawer
 * Created by Eminem Lu on 16/6/17.
 * Email arjinmc@hotmail.com
 */

public class SlidingDrawer extends LinearLayout {

    private final int ANIMATION_DURATION = 1000;
    private final float TOUCH_EFFECTIVE_DISTANCE = 0.3f;
    private final int TOUCH_TRACKER_PIXEL_MAX = 1000;
    private final float TOUCH_VELOCITY = 2f;

    private static final int STATUS_CLOSED = 0;
    private static final int STATUS_OPEN = 1;
    private static final int STATUS_OPEN_PARTLT = 2;

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;

    private int mStatus = STATUS_CLOSED;
    private int mPartlyPositionHeight;
    private int mClosedPositionHeight = 150;
    private int mCurrentPosiontHeight;
    private int mAutoRewindHeight;
    private boolean isClickToUp = true;

    private ValueAnimator mAnimator;
    private SlidingAnimationUpdateListener mSlidingAnimationUpdateListener;
    private SlidingAnimationListener mSlidingAnimationListener;
    private VelocityTracker mVelocityTracker;
    private float mVelocity;
    private boolean lockTouch = false;
    private int mCurrentDirection;
    private boolean hasChild;

    private OnStatusChangeListener mOnStatusChangeListener;

    private float mDownY;

    public SlidingDrawer(Context context) {
        super(context);
        init();
    }

    public SlidingDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingDrawer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        setOrientation(LinearLayout.VERTICAL);
        setOnClickListener(null);
    }

    public void setPartlyPositionHeight(int height) {
        mPartlyPositionHeight = height;
    }

    public void setClosedPostionHeight(int height) {
        mClosedPositionHeight = height;
        postInvalidate();
    }

    public void setAutoRewindHeight(int height) {
        mAutoRewindHeight = height;
    }

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        mOnStatusChangeListener = onStatusChangeListener;
    }


    public void open() {

        if (mStatus == STATUS_CLOSED || mStatus == STATUS_OPEN_PARTLT) {

            if (mCurrentPosiontHeight == 0)
                mCurrentPosiontHeight = mClosedPositionHeight;

            startAnimation(mCurrentPosiontHeight, getMeasuredHeight());
            mCurrentPosiontHeight = getMeasuredHeight();
            mStatus = STATUS_OPEN;
        }
    }

    public void openPartly() {
        if (mStatus == STATUS_CLOSED) {

            if (mCurrentPosiontHeight == 0)
                mCurrentPosiontHeight = mClosedPositionHeight;

            startAnimation(mCurrentPosiontHeight, mPartlyPositionHeight);
            mCurrentPosiontHeight = mPartlyPositionHeight;
            mStatus = STATUS_OPEN_PARTLT;
        }
    }

    public void close() {

        if (mStatus == STATUS_OPEN_PARTLT || mStatus == STATUS_OPEN) {
            scrollListViewToTop();
            startAnimation(mCurrentPosiontHeight, mClosedPositionHeight);
            mCurrentPosiontHeight = mClosedPositionHeight;
            mStatus = STATUS_CLOSED;
        }
    }

    public void setClickFirstChildToUp(boolean toUp) {
        isClickToUp = toUp;
    }


    private void startAnimation(int fromLength, int toLength) {

        lockTouch = true;

        if (mSlidingAnimationUpdateListener == null)
            mSlidingAnimationUpdateListener = new SlidingAnimationUpdateListener();
        if (mSlidingAnimationListener == null)
            mSlidingAnimationListener = new SlidingAnimationListener();

        mAnimator = ValueAnimator.ofInt(fromLength, toLength);
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(mSlidingAnimationUpdateListener);
        mAnimator.addListener(mSlidingAnimationListener);
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() != 0) {
            if (mPartlyPositionHeight == 0)
                mPartlyPositionHeight = getMeasuredHeight() / 3;
            setTranslationY(getMeasuredHeight() - mClosedPositionHeight);
            if (getChildCount() != 0) hasChild = true;
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                float alterY = event.getY() - mDownY;
                if (!lockTouch) {
                    if (hasChild) {
                        if (getChildAt(0) instanceof ListView) {
                            ListView listView = (ListView) getChildAt(0);
                            float alterEvenY = event.getY() <= 0 ? event.getY() + getTranslationY() : event.getY();
                            if (alterEvenY < listView.getTop()
                                    || alterEvenY > listView.getBottom()
                                    || event.getX() < listView.getLeft()
                                    || event.getX() > listView.getRight()
                                    || !isListViewFirstChildEntireVisible(listView)) {
                                return true;
                            }
                        }
                    }

                    if (alterY <= 0) {
                        mCurrentDirection = DIRECTION_UP;

                        if (mCurrentPosiontHeight - alterY <= getMeasuredHeight()) {
                            mCurrentPosiontHeight -= alterY;
                        } else {
                            mCurrentPosiontHeight = getMeasuredHeight();
                        }
                    } else {
                        mCurrentDirection = DIRECTION_DOWN;

                        if (mCurrentPosiontHeight - mClosedPositionHeight - alterY >= 0
                                && mCurrentDirection - alterY - mClosedPositionHeight <= getMeasuredHeight()) {
                            mCurrentPosiontHeight -= alterY;
                        } else {
                            mCurrentPosiontHeight = mClosedPositionHeight;
                        }
                    }
                    setTranslationY(getMeasuredHeight() - mCurrentPosiontHeight);
                }
                mVelocityTracker.computeCurrentVelocity(1, TOUCH_TRACKER_PIXEL_MAX);
                mVelocity = mVelocityTracker.getYVelocity();
                break;
            case MotionEvent.ACTION_UP:

                if (mCurrentDirection == DIRECTION_UP) {
                    if (Math.abs(mVelocity) >= TOUCH_VELOCITY) {
                        open();
                    } else if (mAutoRewindHeight > 0 && mCurrentPosiontHeight < mAutoRewindHeight) {

                        if (mStatus == STATUS_CLOSED && isTouchListViewFirstChild(event.getX(), event.getY())
                                && isClickToUp) {
                            open();
                        } else {
                            mStatus = STATUS_OPEN;
                            close();
                        }

                    } else if (Math.abs(mDownY-event.getY())<=TOUCH_EFFECTIVE_DISTANCE
                            && mStatus == STATUS_OPEN_PARTLT
                            && isTouchListViewFirstChild(event.getX(), event.getY())) {
                        clickListViewFirstChild();
                        close();
                    } else {
                        mStatus = STATUS_OPEN_PARTLT;
                        open();
                    }
                } else {
                    if (mStatus == STATUS_CLOSED && isTouchListViewFirstChild(event.getX(), event.getY())
                            && isClickToUp) {
                        open();
                    }else if (Math.abs(mDownY-event.getY())==0
                            && mStatus == STATUS_OPEN_PARTLT
                            && isTouchListViewFirstChild(event.getX(), event.getY())) {
                        clickListViewFirstChild();
                        close();
                    } else {
                        mStatus = STATUS_OPEN_PARTLT;
                        close();
                    }
                }
                mDownY = 0f;
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocity = 0f;
        }

        if (lockTouch) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        onTouchEvent(ev);

        if (mStatus == STATUS_CLOSED) {
            return true;
        } else if (mStatus == STATUS_OPEN_PARTLT
                && getChildCount() != 0 && getChildAt(0) instanceof ListView) {
            ListView listView = (ListView) getChildAt(0);
            if (isListViewFirstChildEntireVisible(listView)) {
                return true;
            } else {
                return super.onInterceptTouchEvent(ev);
            }

        } else if (mStatus == STATUS_OPEN
                && getChildCount() != 0 && getChildAt(0) instanceof ListView) {
            ListView listView = (ListView) getChildAt(0);
            if (mCurrentDirection == DIRECTION_UP && isListViewFirstChildEntireVisible(listView)) {
                return super.onInterceptTouchEvent(ev);
            } else if (mCurrentDirection == DIRECTION_DOWN && isListViewFirstChildEntireVisible(listView)) {
                return true;
            } else {
                return super.onInterceptTouchEvent(ev);
            }

        } else {
            return super.onInterceptTouchEvent(ev);
        }

    }

    private boolean isListViewFirstChildEntireVisible(ListView listView) {
        if (listView == null)
            return false;
        if (listView.getChildCount() != 0) {
            View firstChild = listView.getChildAt(0);
            if (listView.getFirstVisiblePosition() == 0 && firstChild.getTop() >= listView.getPaddingTop()) {
                return true;
            }

        }
        return false;
    }

    private boolean isTouchListViewFirstChild(float x, float y) {

        if (getChildCount() == 0)
            return false;
        if (!(getChildAt(0) instanceof ListView))
            return false;
        ListView listView = (ListView) getChildAt(0);
        if (listView == null)
            return false;
        if (listView.getChildCount() != 0) {
            View firstChild = listView.getChildAt(0);
            if (x >= firstChild.getLeft() && x <= firstChild.getRight()
                    && y >= firstChild.getTop() && y <= firstChild.getBottom()) {
                return true;
            }
        }
        return false;
    }

    private void clickListViewFirstChild() {
        if (getChildCount() != 0 && getChildAt(0) instanceof ListView) {
            ListView listView = (ListView) getChildAt(0);
            if (listView.getChildCount() != 0)
                listView.performItemClick(listView.getChildAt(0), 0, listView.getChildAt(0).getId());
        }
    }


    private void scrollListViewToTop() {
        if (getChildCount() != 0 && getChildAt(0) instanceof ListView) {
            ListView listView = (ListView) getChildAt(0);
            if (listView.getChildCount() != 0)
                listView.setSelection(0);
        }
    }


    private class SlidingAnimationUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            setTranslationY(getMeasuredHeight() - (Float.valueOf((int) valueAnimator.getAnimatedValue())));
        }
    }

    private class SlidingAnimationListener implements ValueAnimator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            lockTouch = false;
            if (mOnStatusChangeListener != null) {
                switch (mStatus) {
                    case STATUS_OPEN:
                        mOnStatusChangeListener.onOpen();
                        break;
                    case STATUS_CLOSED:
                        mOnStatusChangeListener.onClosed();
                        break;
                    case STATUS_OPEN_PARTLT:
                        mOnStatusChangeListener.onOpenPartly();
                        break;
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    public interface OnStatusChangeListener {

        void onOpen();

        void onClosed();

        void onOpenPartly();
    }
}
