package com.example.testproject.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义LayoutManager实现左右滑动(复用方式由offsetChildrenHorizontal滑动变为通过布局来动态改变滑动的Item位置)
 * 目标:
 * 1.实现左右滑动 （已经实现....)
 * 2.实现边界拦截(暂时实现左右边界拦截)(已经实现...)
 * 3.实现Item回收(已经实现....)
 * 4.实现Item复用(已经实现....)
 * 4-1.实现Item复用的第二种方式:每次滑动不是通过offsetChildrenHorizontal来，而是通过layout来动态更新位置(已实现.....)
 * 5.边界拦截改为左边第一个item初始化在RecyclerView中间，滑动到最右边时，最后一个Item在RecyclerView中间(已实现....)
 * 6.每次滑动会停留在某个Item的中间（需要在RecyclerView中重写相关方法实现）
 * <p>
 * created by xiaojun at 2020/3/23
 */
public class CustomLayoutManagerRecycler2 extends RecyclerView.LayoutManager {

    //保存Rect的缓存列表
    private SparseArray<Rect> mItemRects = new SparseArray<>();
    //所有item的总宽度
    private int mTotalWidth;
    //目前总共移动的距离
    private int mTotalMoveX;
    //起始显示到中间
    private int mStartX;
    //一个Item的宽度
    private int mWidthItem;
    //RecyclerView所在的Rect
    private Rect mRecyclerViewRect;
    //滑动后选择监听器
    private OnSelectedListener mSelectedListener;
    //振动器
    private Vibrator mVibrator;

    public CustomLayoutManagerRecycler2(Context context) {
        super();
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        Log.e("xiaojun","OnLayoutCompleted:"+state);
    }

    /**
     * 根据速度算出应该滚动到item中间的新的距离
     *
     * @param velocityX 速度
     * @return 新的Distance
     */
    public double getProperDistance(int velocityX, double distance) {
        //初始滑动的时候不一定是在Item边边，这里算出距离边边多少距离
        int extra = mTotalMoveX % mWidthItem;
        double realDistance = 0;
        if (velocityX > 0) {
            if (distance < mWidthItem) {
                realDistance = mWidthItem - extra;
            } else {
                realDistance = distance - distance % mWidthItem - extra;
            }
        } else {
            if (distance < mWidthItem) {
                realDistance = extra;
            } else {
                realDistance = distance - distance % mWidthItem + extra;
            }
        }

        return realDistance;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * 初始化布局（目前这个LayoutManager只支持宽度固定的Item）
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *!!!!!!!!!!!!!!!!!!!!!!!!在本LayoutManager被切换到其他APP后，回来的时候可能会重调引起错误,所以需要重新处理一下!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * @param recycler 缓存器(可以通过此缓存器拿出缓存中的View)
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.e("xiaojun","onLayoutChildren");
        detachAndScrapAttachedViews(recycler);//将现在屏幕上显示的Item离屏缓存到recycler

        //随机获取一个item的宽度用于计算当前屏幕总共可以显示多少个Item
        View randomChild = recycler.getViewForPosition(0);
        measureChildWithMargins(randomChild, 0, 0);
        int randomWidth = getDecoratedMeasuredWidth(randomChild);
        int randomHeight = getDecoratedMeasuredHeight(randomChild);
        int visibleCount = (int) Math.ceil(1.0 * getWidth() / randomWidth);//向上取整

        mStartX = getWidth() / 2 - randomWidth / 2;

        //初始化
        int offsetX = 0;
        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = new Rect(mStartX + offsetX, 0, mStartX + offsetX + randomWidth, randomHeight);
            mItemRects.put(i, rect);
            offsetX += randomWidth;
        }

        //布局初始的Item
        for (int i = 0; i < visibleCount; i++) {
            Rect childRect = mItemRects.get(i);
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            layoutDecoratedWithMargins(child, childRect.left, childRect.top, childRect.right, childRect.bottom);
        }

        mWidthItem = randomWidth;
        mTotalWidth = offsetX;
    }

    /**
     * 滑动处理
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        int realDx = scrollHorizontallyBy1(dx, recycler, state);
        int realDx = scrollHorizontallyBy2(dx, recycler, state);
        return realDx;
    }

    /**
     * @param state the new scroll state, one of {@link #SCROLL_STATE_IDLE},
     *                    {@link #SCROLL_STATE_DRAGGING} or {@link #SCROLL_STATE_SETTLING}
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        Log.e("xiaojun","state.Layoutmanager="+state);
        if (mSelectedListener!=null){
            if (state == RecyclerView.SCROLL_STATE_SETTLING){
//                mSelectedListener.selected();
            }
        }

    }

    public void setOnSelectedListener(OnSelectedListener listener){

    }

    public interface OnSelectedListener{
        void selected(int position);
    }

    /**
     * 第一种回收复用方式
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    private int scrollHorizontallyBy1(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //1.拦截边界
        int realDx = interceptEdge(dx);
        //2.回收item
        removeItemIfNeeded(realDx, recycler);
        //3.Item布局
        recyclerItemLayout(realDx, recycler, getRecyclerViewRect(realDx));
        //移动
        offsetChildrenHorizontal(-realDx);

        mTotalMoveX += realDx;

        return realDx;
    }

    /**
     * 第二种回收复用方式
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    private int scrollHorizontallyBy2(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //1.拦截边界
        int realDx = interceptCenter(dx);
        //2.回收item
        removeItemIfNeeded(realDx, recycler);
        mTotalMoveX += realDx;
        //3.Item布局并移动
        recyclerItemLayout2(realDx, recycler, getRecyclerViewRectNoOffset());
        return realDx;
    }

    /**
     * 获取当前RecyclerView在移动realDx距离后的Rect
     *
     * @param realDx 真正移动的距离
     * @return 返回移动realDx后的Rect
     */
    private Rect getRecyclerViewRect(int realDx) {
        if (mRecyclerViewRect == null)
            mRecyclerViewRect = new Rect(mTotalMoveX + realDx, 0, getWidth() + mTotalMoveX + realDx, getHeight());
        else {
            mRecyclerViewRect.left = mTotalMoveX + realDx;
            mRecyclerViewRect.top = 0;
            mRecyclerViewRect.right = getWidth() + mTotalMoveX + realDx;
            mRecyclerViewRect.bottom = getHeight();
        }
        return mRecyclerViewRect;
    }

    /**
     * 获取没有偏移realDx距离的Rect
     *
     * @return
     */
    private Rect getRecyclerViewRectNoOffset() {
        if (mRecyclerViewRect == null)
            mRecyclerViewRect = new Rect(mTotalMoveX, 0, getWidth() + mTotalMoveX, getHeight());
        else {
            mRecyclerViewRect.left = mTotalMoveX;
            mRecyclerViewRect.top = 0;
            mRecyclerViewRect.right = mTotalMoveX + getWidth();
            mRecyclerViewRect.bottom = getHeight();
        }
        return mRecyclerViewRect;
    }

    /**
     * 边界拦截
     *
     * @param dx 偏移位置
     * @return 返回真实的偏移距离
     */
    private int interceptEdge(int dx) {
        int realDx = dx;
        // ←
        if (dx > 0) {
            if (mTotalMoveX + dx > mTotalWidth - getWidth()) {
                realDx = mTotalWidth - getWidth() - mTotalMoveX;
            }
        }
        // →
        else {
            if (mTotalMoveX + dx < 0) {
                realDx = -mTotalMoveX;
            }
        }
        return realDx;
    }

    /**
     * 拦截到中间
     *
     * @param dx
     * @return
     */
    private int interceptCenter(int dx) {
        int realDx = dx;
        // ←
        if (dx > 0) {
            if (mTotalMoveX + dx > mTotalWidth - getWidth() / 2 - mWidthItem / 2 + mStartX) {
                realDx = mTotalWidth - getWidth() / 2 - mWidthItem / 2 + mStartX - mTotalMoveX;
            }
            //
        }
        // →
        else {
            if (mTotalMoveX + dx < 0) {
                realDx = -mTotalMoveX;
            }
        }
        return realDx;
    }

    /**
     * 移除相应会滑动到外面的View
     * 1.←：移除最左边的View
     * 2.→：移除最右边的View
     *
     * @param recycler item缓存器
     * @param realDx   真正移动的距离
     */
    private void removeItemIfNeeded(int realDx, RecyclerView.Recycler recycler) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View viewOnScreen = getChildAt(i);
            // ← 移除最左边的View
            if (realDx > 0) {
                if (getDecoratedRight(viewOnScreen) - realDx <= 0) {
                    removeAndRecycleView(viewOnScreen, recycler);
                }
            }
            // → 移除最右边的View
            else {
                if (getDecoratedLeft(viewOnScreen) - realDx >= getWidth()) {
                    removeAndRecycleView(viewOnScreen, recycler);
                }
            }
        }
    }

    /**
     * 复用Item开始布局
     *
     * @param realDx           真正移动的距离
     * @param recycler         缓存器
     * @param recyclerViewRect RecyclerView所在的Rect
     *                         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *                         !!!!!!!!这里需要注意 Rect.intersects和 rect实例.intersects的不同!!!!!!!!!!
     *                         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    private void recyclerItemLayout(int realDx, RecyclerView.Recycler recycler, Rect recyclerViewRect) {
        // ← 最右边开始新增Item
        if (realDx > 0) {
            View childLast = getChildAt(getChildCount() - 1);
            int positionLast = getPosition(childLast);
            int positionNeeded = positionLast + 1;
            for (int i = positionNeeded; i < getItemCount(); i++) {
                Rect rect = mItemRects.get(i);
                //和RecyclerViewRect相交才进行布局操作
                if (Rect.intersects(recyclerViewRect, rect)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalMoveX, rect.top, rect.right - mTotalMoveX, rect.bottom);
                }
            }
        }
        // → 最左边开始新增Item
        else {
            View viewFirst = getChildAt(0);
            int positionFirst = getPosition(viewFirst);
            int positionNeeded = positionFirst - 1;
            for (int i = positionNeeded; i >= 0; i--) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(recyclerViewRect, rect)) {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalMoveX, 0, rect.right - mTotalMoveX, rect.bottom);
                }
            }
        }
    }

    /**
     * 通过动态布局来实现View的复用以及移动
     *
     * @param realDx           真正移动的距离
     * @param recycler         缓存器
     * @param recyclerViewRect RecyclerView所在的Rect
     */
    private void recyclerItemLayout2(int realDx, RecyclerView.Recycler recycler, Rect recyclerViewRect) {
        View viewFirst = getChildAt(0);
        View viewLast = getChildAt(getChildCount() - 1);

        detachAndScrapAttachedViews(recycler);

        // ← 从屏幕上显示的第一个(左边)View开始向最后一个View开始遍历
        if (realDx > 0) {
            int positionFirst = getPosition(viewFirst);
            for (int i = positionFirst; i < getItemCount(); i++) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(rect, recyclerViewRect)) {
                    View child = recycler.getViewForPosition(i);
                    selectedSetting(rect,child);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalMoveX, 0, rect.right - mTotalMoveX, rect.bottom);
//                    child.setRotationX(child.getRotationX() + 1);
                }
            }
        }
        // → 从屏幕上显示的最后(最右边)View开始向第一个View开始遍历
        else {
            int positionLast = getPosition(viewLast);
            for (int i = 0; i <= positionLast; i++) {
                Rect rect = mItemRects.get(i);
                if (Rect.intersects(rect, recyclerViewRect)) {
                    View child = recycler.getViewForPosition(i);
                    selectedSetting(rect,child);
                    addView(child);
                    measureChildWithMargins(child, 0, 0);
                    layoutDecoratedWithMargins(child, rect.left - mTotalMoveX, 0, rect.right - mTotalMoveX, rect.bottom);
//                    child.setRotationX(child.getRotationX() + 1);
                }
            }
        }
    }

    private View mLastChild;

    private void selectedSetting(Rect rect,View child) {
        if (getWidth()/2>=rect.left-mTotalMoveX&&getWidth()/2<rect.right-mTotalMoveX){
            child.setBackgroundColor(Color.YELLOW);
            if (child!=mLastChild){
                shake();
                mLastChild = child;
            }
        }

        else{
            child.setBackgroundColor(Color.parseColor("#ff00ddff"));
        }
    }

    private void shake(){
        if (mVibrator.hasVibrator()){
            mVibrator.vibrate(40);
        }
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        Log.e("xiaojun","layoutManager:onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        Log.e("xiaojun","layoutManager:onDetachedFromWindow");
    }
}
