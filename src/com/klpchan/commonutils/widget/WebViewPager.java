package com.klpchan.commonutils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.webkit.WebView;

public class WebViewPager extends WebView {
    private VelocityTracker mVelocityTracker;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitialMotionX;
    private int mMaximumVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;
    private boolean mIsBeingDragged;
    private int mTouchSlop;

    public WebViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WebViewPager(Context context) {
        super(context);
        init();
    }

    public WebViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
            }
            break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    try {
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(pointerIndex);
                        final float xDiff = Math.abs(x - mLastMotionX);
                        final float y = ev.getY(pointerIndex);
                        final float yDiff = Math.abs(y - mLastMotionY);
                        if (xDiff > mTouchSlop && xDiff > yDiff) {
                            mIsBeingDragged = true;
                            requestParentDisallowInterceptTouchEvent(true);
                            mLastMotionX = x - mInitialMotionX > 0 ? mInitialMotionX + mTouchSlop :
                                    mInitialMotionX - mTouchSlop;
                            mLastMotionY = y;

                            // Disallow Parent Intercept, just in case
                            ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    } catch (Exception ignored) {}
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                try {
                    final int index = ev.getActionIndex();
                    final float x = ev.getX(index);
                    mLastMotionX = x;
                    mActivePointerId = ev.getPointerId(index);
                } catch (Exception ignored){}
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                try {
                    mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                } catch (Exception ignored){}
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}

