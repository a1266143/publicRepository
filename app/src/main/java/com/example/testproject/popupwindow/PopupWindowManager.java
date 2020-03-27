package com.example.testproject.popupwindow;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 测试PopupWindow
 * created by xiaojun at 2020/3/25
 */
public class PopupWindowManager {

    private PopupWindow mPopupWindow;
    private View mParent;

    public PopupWindowManager(View view,View parent){
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParent = parent;
        mPopupWindow.setOutsideTouchable(true);
    }

    public void showPopup(int x,int y){
        mPopupWindow.showAtLocation(mParent, Gravity.NO_GRAVITY,x,y);
    }

}
