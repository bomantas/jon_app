package com.poh.jon_app;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class customRowView  extends View{
    int viewWidth = 0;
    int viewHeight = 0;

    public customRowView(Context context) {
            super(context);
    }
    
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            viewWidth = xNew;
            viewHeight = yNew;
            /*
            these viewWidth and viewHeight variables
            are the global int variables
            that were declared above
            */
    }
    @Override
    protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            /*
            finally, once the view gets drawn on to the screen
            we can retrieve the dimension values
            as stored in the global variables declared above
            */
            String msg = "width: " + viewWidth + "height: " + viewHeight;
            System.out.println(msg);
    }    

}
