package com.whalespool.puzzlle.puzzle;

import android.graphics.Bitmap;

/**
 * Created by yodazone on 2016/10/21.
 */

public class PuzzlePiece {
    public int index = 0;

    public Bitmap bitmap = null;

    public void recycleBitmap(){
        if(bitmap != null){
            bitmap.recycle();
        }
    }
}
