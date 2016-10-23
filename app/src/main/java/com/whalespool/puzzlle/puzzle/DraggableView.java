package com.whalespool.puzzlle.puzzle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by yodazone on 2016/10/21.
 */

public class DraggableView extends ImageView{

    private int mIndex;

    public DraggableView(Context context) {
        super(context);
        init(context);
    }

    public DraggableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DraggableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.startDrag(null, new View.DragShadowBuilder(v), mIndex, 0);
                return true;
            }
        });
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex(){
        return mIndex;
    }
}
