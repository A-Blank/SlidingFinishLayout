package com.slidingfinishlayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * Created by 丶 on 2017/3/29.
 */

/**
 * 滑动结束Activity的布局实现
 */

public class SlidingFinishView extends FrameLayout {

    /**
     * 记录先前的点击坐标点
     */
    private float oldX, oldY, X, Y;
    private float downX, downY;
    private float InterceptDownX, InterceptX;
    private float InterceptDownY, InterceptY;
    private ViewGroup viewGroup;
    private OverScroller scroller;
    private SlidingFinishCallback callback;
    private boolean InterceptFlag = false;
    private boolean flag;

    /**
     * 是否支持滑动结束activity
     */
    private boolean SlidingEnable;


    public SlidingFinishView(Context context) {
        super(context);
        Init(context);
    }

    public SlidingFinishView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public SlidingFinishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public void setSlideEnable(boolean Enable) {
        SlidingEnable = Enable;
    }

    public void Init(Context context) {
        callback = (SlidingFinishCallback) context;
        scroller = new OverScroller(context);
        SlidingEnable = true;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewGroup = (ViewGroup) this.getParent();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!SlidingEnable) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = oldX = event.getRawX();
                downY = oldY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                X = event.getRawX();
                Y = event.getRawY();
                if (oldX == 0) {
                    oldX = X;
                }
                if (oldY == 0) {
                    oldY = Y;
                }
                int subX = (int) (X - oldX);
                int subY = (int) (Y - oldY);
                viewGroup.scrollBy(0, -subY);
                setAlpha(viewGroup.getScrollY());
                oldX = X;
                oldY = Y;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(viewGroup.getScrollY() * 3) > viewGroup.getHeight() || Math.abs(viewGroup.getScrollX() * 3) > viewGroup.getWidth()) {
                    if (viewGroup.getScrollY() < 0) {
                        scroller.startScroll(0, viewGroup.getScrollY(), 0, -viewGroup.getHeight() + viewGroup.getScrollY(), 1000);
                    }
                    if (viewGroup.getScrollY() > 0) {
                        scroller.startScroll(0, viewGroup.getScrollY(), 0, viewGroup.getHeight() - viewGroup.getScrollY(), 1000);
                    }
                    flag = true;
                } else {
//                    Log.i("TAG", "action up");
                    scroller.startScroll(0, viewGroup.getScrollY(), 0, -viewGroup.getScrollY(), 500);
                    flag = false;
                }
                oldX = oldY = 0;
                invalidate();
                break;
        }

        /**
         * 若返回true继续处理事件
         * 若返回false事件处理完毕
         */
        return true;
    }

    /**
     * 处理事件拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!SlidingEnable) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                InterceptDownX = ev.getRawX();
                InterceptDownY = ev.getRawY();
//                break;
            case MotionEvent.ACTION_MOVE:
                InterceptX = ev.getRawX();
                InterceptY = ev.getRawY();
//                Log.i("TAG", ""+InterceptX);
                int subX = (int) (InterceptX - InterceptDownX);
                int subY = (int) (InterceptY - InterceptDownY);
                if (Math.abs(subY) > Math.abs(subX)) {
                    /**
                     * 由于ACTION_DOWN事件未被捕获
                     * 因此需要在此处初始化oldY的值
                     */
                    oldY = 0;
                    return true;
                } else {
                    break;
                }
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;

    }

    /**
     * 响应startScroll，并将view移动到相应位置
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            viewGroup.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            setAlpha(scroller.getCurrY());
            postInvalidate();
            Log.i("TAG", "computeScroll:"+viewGroup.getScrollX()+" "+viewGroup.getWidth()+" "+viewGroup.getScrollY()+" "+viewGroup.getHeight());
            if ( Math.abs(viewGroup.getScrollX())>= viewGroup.getWidth()||Math.abs(viewGroup.getScrollY())>= viewGroup.getHeight()) {
                ((Activity)getContext()).finish();
            }
        }
    }

    /**
     * 改变背景透明度:
     * setAlpha改变的是整个view的透明度,setBackgroundColor才能改变Layout的背景透明度
     */
    public void setAlpha(float CurrY) {

        int alpha = (int) ((getHeight() - Math.abs(CurrY)) / getHeight() * 255);
        String alphaHex = Integer.toHexString(alpha);
        if (alphaHex.length() != 2) {
            alphaHex = "00";
        }
        viewGroup.setBackgroundColor(Color.parseColor("#" + alphaHex + "000000"));

    }

    public interface SlidingFinishCallback {
        void onFinish();
    }

}
