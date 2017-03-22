package com.droidrui.refreshlayoutdemo.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.droidrui.refreshlayoutdemo.R;

/**
 * Created by Administrator on 2016/12/6.
 */
public class RefreshLayout extends ViewGroup {

    private static final int STATUS_REFRESHING_HIDED = 4;
    private static final int STATUS_REFRESH_COMPLETE = 3;
    private static final int STATUS_REFRESHING = 2;
    private static final int STATUS_WANT_TO_REFRESH = 1;
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_WANT_TO_LOAD_MORE = -1;
    private static final int STATUS_LOADING_MORE = -2;
    private static final int STATUS_LOAD_MORE_COMPLETE = -3;
    private static final int STATUS_LOADING_MORE_HIDED = -4;

    private static final int SCROLL_DURATION = 250;

    private static final int INVALID_COORDINATE = -1;
    private static final int INVALID_POINTER = -1;

    private static final float SLOPE = 0.0010f;

    private static final int SCROLL_DELAY = 500;

    private boolean mRefreshEnabled = true;
    private boolean mLoadMoreEnabled = true;

    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;

    private int mHandlingStatus;

    private boolean mTrigger;

    private int mTouchSlop;

    private RefreshHeaderView mHeaderView;
    private View mContentView;
    private LoadMoreFooterView mFooterView;

    private int mLayoutHeight;
    private int mHeaderWidth;
    private int mHeaderHeight;
    private int mTargetWidth;
    private int mTargetHeight;
    private int mFooterWidth;
    private int mFooterHeight;

    private int mActivePointerId;
    private float mFirstX;
    private float mFirstY;
    private float mLastX;
    private float mLastY;
    private boolean mAttached = true;

    private boolean mOnAttached = false;

    private boolean mDraging = false;

    private AutoScroller mAutoScroller;
    private SparseBooleanArray mHorizontalMap;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mAutoScroller = new AutoScroller();
        mHorizontalMap = new SparseBooleanArray();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        mHeaderView = (RefreshHeaderView) findViewById(R.id.refresh_header_view);
        mContentView = findViewById(R.id.content_view);
        mFooterView = (LoadMoreFooterView) findViewById(R.id.load_more_footer_view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentView == null) {
            return;
        }
        mLayoutHeight = getMeasuredHeight();
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderWidth = mHeaderView.getMeasuredWidth();
            mHeaderHeight = mHeaderView.getMeasuredHeight();
        }
        measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
        mTargetWidth = mContentView.getMeasuredWidth();
        mTargetHeight = mContentView.getMeasuredHeight();
        if (mFooterView != null) {
            measureChild(mFooterView, widthMeasureSpec, heightMeasureSpec);
            mFooterWidth = mFooterView.getMeasuredWidth();
            mFooterHeight = mFooterView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContentView == null) {
            return;
        }
        int top = mContentView.getTop();
        if (mHeaderView != null) {
            mHeaderView.layout(0, top - mHeaderHeight, mHeaderWidth, top);
        }
        mContentView.layout(0, top, mTargetWidth, mTargetHeight + top);
        if (mFooterView != null) {
            mFooterView.layout(0, mLayoutHeight + top, mFooterWidth, mLayoutHeight + top + mFooterHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDraging = true;
                mActivePointerId = ev.getPointerId(0);
                mFirstX = getMotionEventX(ev, mActivePointerId);
                mFirstY = getMotionEventY(ev, mActivePointerId);
                if (mFirstX == INVALID_COORDINATE || mFirstY == INVALID_COORDINATE) {
                    return false;
                }
                mLastX = mFirstX;
                mLastY = mFirstY;
                mAutoScroller.onActionDown();
                super.dispatchTouchEvent(ev);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                mDraging = true;
                float x = getMotionEventX(ev, mActivePointerId);
                float y = getMotionEventY(ev, mActivePointerId);
                float diffX = x - mFirstX;
                float diffY = y - mFirstY;
                float offsetX = x - mLastX;
                float offsetY = y - mLastY;
                mLastX = x;
                mLastY = y;
                if (isHorizontalScroll()) { //ViewPager的onPageScrollStateChanged回调方法很靠谱，很及时，没问题
                    return super.dispatchTouchEvent(ev);
                }
                if (mAttached) {
                    if (Math.abs(diffY) > mTouchSlop && (!(Math.abs(diffX) * 0.5f > Math.abs(diffY)))) {
                        if (offsetY > 0) {
                            if (onCheckCanRefresh()) {
                                if (mHandlingStatus == STATUS_NORMAL) {//0
                                    setHandlingStatus(STATUS_WANT_TO_REFRESH);//1
                                } else if (mHandlingStatus == STATUS_REFRESHING_HIDED) {//4
                                    setHandlingStatus(STATUS_REFRESHING);//2
                                }
                                mAttached = false;
                            }
                        } else {
                            if (onCheckCanLoadMore()) {
                                if (mHandlingStatus == STATUS_NORMAL) {//0
                                    setHandlingStatus(STATUS_WANT_TO_LOAD_MORE);//-1
                                } else if (mHandlingStatus == STATUS_LOADING_MORE_HIDED) {//-4
                                    setHandlingStatus(STATUS_LOADING_MORE);//-2
                                }
                                mAttached = false;
                            }
                        }
                    }
                }
                if (!mAttached) {
                    if (mTrigger) {
                        if (Math.abs(diffX) <= mTouchSlop && Math.abs(diffY) <= mTouchSlop) {
                            return super.dispatchTouchEvent(ev);
                        }
                        if (Math.abs(diffY) > mTouchSlop) {
                            int top = mContentView.getTop();
                            float ratio = -SLOPE * Math.abs(top) + 1;
                            int offset = (int) (offsetY * ratio);
                            float coorY = top + offset;
                            if ((mHandlingStatus > STATUS_NORMAL && coorY <= 0) || (mHandlingStatus < STATUS_NORMAL && coorY >= 0)) {//0
                                offset = -top;
                                mAttached = true;
                            }
                            mTrigger = false;//要滑动了，就不在触发位置上了
                            updateScroll(offset);
                            if (mAttached) {
                                mOnAttached = true;
                                if (mHandlingStatus == STATUS_WANT_TO_REFRESH) {//1
                                    setHandlingStatus(STATUS_NORMAL);//0
                                } else if (mHandlingStatus == STATUS_WANT_TO_LOAD_MORE) {//-1
                                    setHandlingStatus(STATUS_NORMAL);//0
                                } else if (mHandlingStatus == STATUS_REFRESHING) {//2
                                    setHandlingStatus(STATUS_REFRESHING_HIDED);//4
                                } else if (mHandlingStatus == STATUS_LOADING_MORE) {//-2
                                    setHandlingStatus(STATUS_LOADING_MORE_HIDED);//-4
                                }
                                ev.setAction(MotionEvent.ACTION_DOWN);
                                return super.dispatchTouchEvent(ev);
                            }
                            return true;
                        }
                        if (Math.abs(diffX) > mTouchSlop && Math.abs(diffX) * 0.5f > Math.abs(diffY)) {
                            return super.dispatchTouchEvent(ev);
                        }
                    }
                    int top = mContentView.getTop();
                    if (mHandlingStatus == STATUS_WANT_TO_REFRESH) {//1
                        mHeaderView.onTopChange(top);
                    } else if (mHandlingStatus == STATUS_WANT_TO_LOAD_MORE) {//-1
                        mFooterView.onTopChange(top);
                    }
                    float ratio = -SLOPE * Math.abs(top) + 1;
                    int offset = (int) (offsetY * ratio);
                    float coorY = top + offset;
                    if ((mHandlingStatus > STATUS_NORMAL && coorY <= 0) || (mHandlingStatus < STATUS_NORMAL && coorY >= 0)) {//0
                        offset = -top;
                        mAttached = true;
                    }
                    mTrigger = false;//要滑动了，就不在触发位置上了
                    updateScroll(offset);
                    if (mAttached) {
                        mOnAttached = true;
                        if (mHandlingStatus == STATUS_WANT_TO_REFRESH) {//1
                            setHandlingStatus(STATUS_NORMAL);//0
                        } else if (mHandlingStatus == STATUS_WANT_TO_LOAD_MORE) {//-1
                            setHandlingStatus(STATUS_NORMAL);//0
                        } else if (mHandlingStatus == STATUS_REFRESHING) {//2
                            setHandlingStatus(STATUS_REFRESHING_HIDED);//4
                        } else if (mHandlingStatus == STATUS_LOADING_MORE) {//-2
                            setHandlingStatus(STATUS_LOADING_MORE_HIDED);//-4
                        }
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        return super.dispatchTouchEvent(ev);
                    }
                    return true;
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondPointerDown(ev);
                mLastX = getMotionEventX(ev, mActivePointerId);
                mLastY = getMotionEventY(ev, mActivePointerId);
                if (!mAttached) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(ev);
                mLastX = getMotionEventX(ev, mActivePointerId);
                mLastY = getMotionEventY(ev, mActivePointerId);
                if (!mAttached) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDraging = false;
                mActivePointerId = INVALID_POINTER;
                if (!mAttached && !mTrigger) {
                    int top = mContentView.getTop();
                    if (mHandlingStatus == STATUS_WANT_TO_REFRESH) {//1
                        if (top > mHeaderHeight) {
                            top = top - mHeaderHeight;
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        } else {
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        }
                    } else if (mHandlingStatus == STATUS_WANT_TO_LOAD_MORE) {//-1
                        if (top < -mFooterHeight) {
                            top = top + mFooterHeight;
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        } else {
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        }
                    } else if (mHandlingStatus == STATUS_REFRESHING) {//2
                        if (top > mHeaderHeight) {
                            top = top - mHeaderHeight;
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        } else {
                            mTrigger = true;//此时也可以触发子View点击事件
                        }
                    } else if (mHandlingStatus == STATUS_LOADING_MORE) {//-2
                        if (top < -mFooterHeight) {
                            top = top + mFooterHeight;
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        } else {
                            mTrigger = true;//此时也可以触发子View点击事件
                        }
                    } else if (mHandlingStatus == STATUS_REFRESH_COMPLETE) {//3
                        mAutoScroller.onActionUp(top, SCROLL_DURATION);
                    } else if (mHandlingStatus == STATUS_LOAD_MORE_COMPLETE) {//-3
                        mAutoScroller.onActionUp(top, SCROLL_DURATION);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(ev);
                    return true;
                }
                if (mAttached && mOnAttached) {
                    mOnAttached = false;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private float getMotionEventX(MotionEvent event, int pointerId) {
        int index = event.findPointerIndex(pointerId);
        if (index < 0) {
            return INVALID_COORDINATE;
        }
        return event.getX(index);
    }

    private float getMotionEventY(MotionEvent event, int pointerId) {
        int index = event.findPointerIndex(pointerId);
        if (index < 0) {
            return INVALID_COORDINATE;
        }
        return event.getY(index);
    }

    private void onSecondPointerDown(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId != INVALID_POINTER) {
            mActivePointerId = pointerId;
        }
    }

    private void onSecondPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            int newPointerIndex = (pointerIndex == 0 ? 1 : 0);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private boolean onCheckCanRefresh() {
        return mRefreshEnabled && !ViewCompat.canScrollVertically(mContentView, -1);
    }

    private boolean onCheckCanLoadMore() {
        return mLoadMoreEnabled && !ViewCompat.canScrollVertically(mContentView, 1);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //用Runnable代替，因为在computeScroll方法里面textView.setText无效
    }

    private void updateScroll(int offset) {
        if (mHandlingStatus > STATUS_NORMAL) {//0
            mHeaderView.offsetTopAndBottom(offset);
        } else if (mHandlingStatus < STATUS_NORMAL) {//0
            mFooterView.offsetTopAndBottom(offset);
        }
        mContentView.offsetTopAndBottom(offset);
    }

    private boolean isHorizontalScroll() {
        for (int i = 0; i < mHorizontalMap.size(); i++) {
            if (!mHorizontalMap.valueAt(i)) {
                return true;
            }
        }
        return false;
    }

    public void setVeritcalScrollEnabled(int key, boolean enabled) {
        mHorizontalMap.put(key, enabled);
    }

    public void setRefreshEnabled(boolean enabled) {
        mRefreshEnabled = enabled;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        mLoadMoreEnabled = enabled;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void completeRefresh() {
        if (mHandlingStatus == STATUS_REFRESHING || mHandlingStatus == STATUS_REFRESHING_HIDED) {//2 4
            setHandlingStatus(STATUS_REFRESH_COMPLETE);//3
            mTrigger = false;
            if (!mAttached) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mDraging) {
                            int top = mContentView.getTop();
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        }
                    }
                }, SCROLL_DELAY);
            } else {
                setHandlingStatus(STATUS_NORMAL);//0
            }
        }
    }

    public void completeLoadMore() {
        if (mHandlingStatus == STATUS_LOADING_MORE || mHandlingStatus == STATUS_LOADING_MORE_HIDED) {//-2 -4
            setHandlingStatus(STATUS_LOAD_MORE_COMPLETE);//-3
            mTrigger = false;
            if (!mAttached) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mDraging) {
                            int top = mContentView.getTop();
                            mAutoScroller.onActionUp(top, SCROLL_DURATION);
                        }
                    }
                }, SCROLL_DELAY);
            } else {
                setHandlingStatus(STATUS_NORMAL);//0
            }
        }
    }

    private void setHandlingStatus(int status) {
        if (status > STATUS_NORMAL) {//0
            mHeaderView.setStatus(status);
        } else if (status < STATUS_NORMAL) {//0
            mFooterView.setStatus(status);
        } else {
            if (mHandlingStatus > STATUS_NORMAL) {//0
                mHeaderView.setStatus(status);
            } else if (mHandlingStatus < STATUS_NORMAL) {//0
                mFooterView.setStatus(status);
            }
        }
        mHandlingStatus = status;
    }

    private class AutoScroller implements Runnable {

        private Scroller mScroller;
        private int mScrollLastY;

        public AutoScroller() {
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }

        /**
         * 只取消Runnable本身的调用，不手动结束mScroller，可以保证mScroller.computeScrollOffset()结果是按照我们预想的正常滑动结束。
         * 只有3种情况：
         * 1.触发刷新中状态；
         * 2.触发加载中状态；
         * 3.复位（top等于0）
         */
        public void onActionDown() {
            removeCallbacks(this);
        }

        public void onActionUp(int dy, int duration) {
            mScrollLastY = 0;
            mScroller.startScroll(0, 0, 0, -dy, duration);
            post(this);
        }

        @Override
        public void run() {
            if (!mScroller.computeScrollOffset()) {
                int top = mContentView.getTop();
                if (top > 0) {
                    mTrigger = true;
                    if (mHandlingStatus == STATUS_WANT_TO_REFRESH) {//1
                        setHandlingStatus(STATUS_REFRESHING);//2
                        if (mRefreshListener != null) {
                            mRefreshListener.onRefresh();
                        }
                    }
                } else if (top < 0) {
                    mTrigger = true;
                    if (mHandlingStatus == STATUS_WANT_TO_LOAD_MORE) {//-1
                        setHandlingStatus(STATUS_LOADING_MORE);//-2
                        if (mLoadMoreListener != null) {
                            mLoadMoreListener.onLoadMore();
                        }
                    }
                } else if (top == 0) {
                    setHandlingStatus(STATUS_NORMAL);//0
                    mAttached = true;
                }
                return;
            }
            int currY = mScroller.getCurrY();
            int offset = currY - mScrollLastY;
            mScrollLastY = currY;
            updateScroll(offset);
            post(this);
        }
    }

    public interface UpdateViewCallback {
        void setStatus(int status);

        void onTopChange(int top);
    }

}
