package com.sankarshan.tictactoefree.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Sankarshan on 8/8/2015.
 */
public class GameBoard extends LinearLayout{
    public GameBoard(Context context){
        super(context);
    }

    public GameBoard(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public GameBoard(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(widthMeasureSpec > heightMeasureSpec){
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
        else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }
}
