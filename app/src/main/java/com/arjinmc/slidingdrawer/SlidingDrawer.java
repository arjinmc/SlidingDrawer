package com.arjinmc.slidingdrawer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * SlidingDrawer
 * Created by Eminem Lo on 16/6/17.
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
    /**
     * mark if openpartly should callback the OnScrollListener.onCurrentHeightChange
     */
    private boolean isOpenPartlyCallbackChange;

    private ValueAnimator mAnimator;
    private SlidingAnimationUpdateListener mSlidingAnimationUpdateListener;
    private SlidingAnimationListener mSlidingAnimationListener;
    private VelocityTracker mVelocityTracker;
    private float mVelocity;
    private boolean lockTouch = false;
    private int mCurrentDirection;
    private boolean hasChild;
    private boolean isCallingOpenPartly;

    private OnStatusChangeListener mOnStatusChangeListener;
    private OnScrollListener mOnScrollListener;
    private OnFirstChildClickListener mOnFirstChildClickListener;

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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * reiniti the original position
     * call it if it's nessessery
     * especially this layout is on the mapview(call onmeasure frequently) layer
     */
    public void initLayoutPosition() {
        setTranslationY(getMeasuredHeight() - mClosedPositionHeight);
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

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public void setOnFirstChildClickListener(OnFirstChildClickListener onFirstChildClickListener) {
        mOnFirstChildClickListener = onFirstChildClickListener;
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
            isCallingOpenPartly = true;
        }
    }

    public void close() {

        if (mStatus == STATUS_OPEN_PARTLT || mStatus == STATUS_OPEN) {
            scrollToTop();
            startAnimation(mCurrentPosiontHeight, mClosedPositionHeight);
            mCurrentPosiontHeight = mClosedPositionHeight;
            mStatus = STATUS_CLOSED;
        }
    }

    /**
     * set if click first child to open
     *
     * @param toOpen
     */
    public void setClickFirstChildToOpen(boolean toOpen) {
        isClickToUp = toOpen;
    }

    /**
     * set if openpartly should callback the OnScrollListener.onCurrentHeightChange
     *
     * @param callChange
     */
    public void setOpenPartltCallbackChange(boolean callChange) {
        isOpenPartlyCallbackChange = callChange;
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
        if (getChildCount() > 1)
            throw new IllegalArgumentException("SlidingDraw can have only one child");
        if (getMeasuredHeight() != 0) {
            if (mPartlyPositionHeight == 0)
                mPartlyPositionHeight = getMeasuredHeight() / 3;
            setTranslationY(getMeasuredHeight() - mClosedPositionHeight);
            if (getChildCount() != 0) hasChild = true;
        }
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

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
                        if (getChildAt(0) instanceof ScrollView) {
                            ScrollView scrollView = (ScrollView) getChildAt(0);
                            float alterEvenY = event.getY() <= 0 ? event.getY() + getTranslationY() : event.getY();
                            if (alterEvenY < scrollView.getTop()
                                    || alterEvenY > scrollView.getBottom()
                                    || event.getX() < scrollView.getLeft()
                                    || event.getX() > scrollView.getRight()
                                    || !isScrollViewScrollTop())
                                return true;
                        }
                        if (getChildAt(0) instanceof RecyclerView) {
                            RecyclerView recyclerView = (RecyclerView) getChildAt(0);
                            float alterEvenY = event.getY() <= 0 ? event.getY() + getTranslationY() : event.getY();
                            if (alterEvenY < recyclerView.getTop()
                                    || alterEvenY > recyclerView.getBottom()
                                    || event.getX() < recyclerView.getLeft()
                                    || event.getX() > recyclerView.getRight()
                                    || !isRecyclerViewScrollTop())
                                return true;
                        }
                    }

                    if (alterY <= 0) {
                        mCurrentDirection = DIRECTION_UP;

                        if (mCurrentPosiontHeight - alterY <= getMeasuredHeight()) {
                            mCurrentPosiontHeight -= alterY;
                        }
                    } else {
                        mCurrentDirection = DIRECTION_DOWN;

                        if (mCurrentPosiontHeight - mClosedPositionHeight - alterY >= 0
                                && mCurrentDirection - alterY - mClosedPositionHeight <= getMeasuredHeight()) {
                            mCurrentPosiontHeight -= alterY;
                        }
                    }
                    setTranslationY(getMeasuredHeight() - mCurrentPosiontHeight);
                }

                mVelocityTracker.computeCurrentVelocity(1, TOUCH_TRACKER_PIXEL_MAX);
                mVelocity = mVelocityTracker.getYVelocity();

                if (mOnScrollListener != null
                        && !(mStatus == STATUS_OPEN_PARTLT && mCurrentDirection == DIRECTION_DOWN)) {
                    float alphaPercent = (float) (mCurrentPosiontHeight) / (float) getMeasuredHeight();
                    mOnScrollListener.onCurrentHeightChange(alphaPercent);
                }

                break;
            case MotionEvent.ACTION_UP:

                if (mCurrentDirection == DIRECTION_UP) {
                    if (Math.abs(mVelocity) >= TOUCH_VELOCITY) {
                        open();
                    } else if (mAutoRewindHeight > 0 && mCurrentPosiontHeight < mAutoRewindHeight) {

                        if (mStatus == STATUS_CLOSED && isTouchFirstChild(event.getX(), event.getY())
                                && Math.abs(mDownY - event.getY()) == 0
                                && isClickToUp) {
                            open();
                        } else {
                            mStatus = STATUS_OPEN;
                            close();
                        }

                    } else if (Math.abs(mDownY - event.getY()) <= TOUCH_EFFECTIVE_DISTANCE
                            && mStatus == STATUS_OPEN_PARTLT
                            && isTouchFirstChild(event.getX(), event.getY())) {
                        clickFirstChild();
                        close();
                    } else {
                        mStatus = STATUS_OPEN_PARTLT;
                        open();
                    }
                } else {
                    if (mStatus == STATUS_CLOSED && isTouchFirstChild(event.getX(), event.getY())
                            && Math.abs(mDownY - event.getY()) == 0
                            && isClickToUp) {
                        open();
                    } else if (Math.abs(mDownY - event.getY()) == 0
                            && mStatus == STATUS_OPEN_PARTLT
                            && isTouchFirstChild(event.getX(), event.getY())) {
                        clickFirstChild();
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

        } else if (mStatus == STATUS_OPEN_PARTLT
                && getChildCount() != 0 && getChildAt(0) instanceof ScrollView) {
            if (isScrollViewScrollTop()) {
                return true;
            } else {
                return super.onInterceptTouchEvent(ev);
            }

        } else if (mStatus == STATUS_OPEN_PARTLT
                && getChildCount() != 0 && getChildAt(0) instanceof RecyclerView) {
            if (isRecyclerViewScrollTop()) {
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

        } else if (mStatus == STATUS_OPEN
                && getChildCount() != 0 && getChildAt(0) instanceof ScrollView) {
            if (mCurrentDirection == DIRECTION_UP && isScrollViewScrollTop()) {
                return super.onInterceptTouchEvent(ev);
            } else if (mCurrentDirection == DIRECTION_DOWN && isScrollViewScrollTop()) {
                return true;
            } else {
                return super.onInterceptTouchEvent(ev);
            }

        } else if (mStatus == STATUS_OPEN
                && getChildCount() != 0 && getChildAt(0) instanceof RecyclerView) {
            if (mCurrentDirection == DIRECTION_UP && isRecyclerViewScrollTop()) {
                return super.onInterceptTouchEvent(ev);
            } else if (mCurrentDirection == DIRECTION_DOWN && isRecyclerViewScrollTop()) {
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

    private boolean isTouchFirstChild(float x, float y) {

        if (getChildCount() == 0)
            return false;
        if (!(getChildAt(0) instanceof ViewGroup))
            return false;
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup == null)
            return false;
        if (viewGroup.getChildCount() != 0) {
            View firstChild = viewGroup.getChildAt(0);
            if (x >= firstChild.getLeft() && x <= firstChild.getRight()
                    && y >= firstChild.getTop() && y <= firstChild.getBottom()) {
                return true;
            }
        }
        return false;
    }

    private void clickFirstChild() {
        if (getChildCount() != 0) {
            if (getChildAt(0) instanceof ListView) {
                ListView listView = (ListView) getChildAt(0);
                if (listView.getChildCount() != 0)
                    listView.performItemClick(listView.getChildAt(0), 0, listView.getChildAt(0).getId());
            } else if (getChildAt(0) instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) getChildAt(0);
                if (scrollView.getChildCount() != 0 && scrollView.getChildAt(0) instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) scrollView.getChildAt(0);
                    if (viewGroup.getChildCount() != 0) {
                        viewGroup.getChildAt(0).performClick();
                    }
                }
            } else if (getChildAt(0) instanceof RecyclerView) {
                if (mOnFirstChildClickListener != null) {
                    mOnFirstChildClickListener.onClick();
                }
            }
        }
    }


    private void scrollToTop() {
        if (getChildCount() != 0 && getChildAt(0) instanceof ListView) {
            ListView listView = (ListView) getChildAt(0);
            if (listView.getChildCount() != 0)
                if (stopListViewScrolling(listView)) {
                    listView.setSelection(0);
                    if (!isListViewFirstChildEntireVisible(listView))
                        listView.smoothScrollToPositionFromTop(0, 0, 0);
                } else {
                    listView.smoothScrollToPositionFromTop(0, 0, 0);
                }

        } else if (getChildCount() != 0 && getChildAt(0) instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) getChildAt(0);
            scrollView.smoothScrollTo(0, 0);
            scrollView.scrollTo(0, 0);
        } else if (getChildCount() != 0 && getChildAt(0) instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) getChildAt(0);
            recyclerView.scrollToPosition(0);
        }
    }

    private boolean stopListViewScrolling(ListView listView) {
        try {
            Method mFlingEndMethod;
            Field mFlingEndField = AbsListView.class.getDeclaredField("mFlingRunnable");
            mFlingEndField.setAccessible(true);
            mFlingEndMethod = mFlingEndField.getType().getDeclaredMethod("endFling");
            mFlingEndMethod.setAccessible(true);
            mFlingEndMethod.invoke(mFlingEndField.get(listView));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isScrollViewScrollTop() {
        if (getChildCount() == 0)
            return false;
        if (!(getChildAt(0) instanceof ScrollView))
            return false;
        ScrollView scrollView = (ScrollView) getChildAt(0);
        if (scrollView.getScrollY() == 0)
            return true;
        return false;
    }

    private boolean isRecyclerViewScrollTop() {

        if (getChildCount() == 0)
            return false;
        if (!(getChildAt(0) instanceof RecyclerView))
            return false;
        RecyclerView recyclerView = (RecyclerView) getChildAt(0);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
            return true;
        return false;
    }


    private class SlidingAnimationUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            setTranslationY(getMeasuredHeight() - (Float.valueOf((int) valueAnimator.getAnimatedValue())));
            if (mOnScrollListener != null) {
                if (isCallingOpenPartly && !isOpenPartlyCallbackChange) {
                    return;
                } else if (mStatus == STATUS_OPEN_PARTLT && mCurrentDirection == DIRECTION_DOWN) {
                    return;
                }

                float alphaPercent = (float) (getMeasuredHeight() - getTranslationY()) / getMeasuredHeight();
                mOnScrollListener.onCurrentHeightChange(alphaPercent);
            }
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
            isCallingOpenPartly = false;
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

    public interface OnScrollListener {
        void onCurrentHeightChange(float percent);
    }

    public interface OnFirstChildClickListener {
        void onClick();
    }
}
