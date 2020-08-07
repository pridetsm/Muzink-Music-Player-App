package com.selfeval.muzink;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.RequiresApi;

public class BlurBuilder {
    private static final float BITMAP_SCALE=0.4f;
    private static final float BLUR_RADIUS =15f;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap bitmap) {
        int width=Math.round(bitmap.getWidth()*BITMAP_SCALE);
        int height=Math.round(bitmap.getHeight()*BITMAP_SCALE);
        Bitmap inputBitmap=Bitmap.createScaledBitmap(bitmap,width,height,false);
        Bitmap outputBitmap=Bitmap.createBitmap(inputBitmap);
        RenderScript rs=RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur=ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tempIn=Allocation.createFromBitmap(rs,inputBitmap);
        Allocation tempOut=Allocation.createFromBitmap(rs,outputBitmap);
        intrinsicBlur.setRadius(BLUR_RADIUS);
        intrinsicBlur.setInput(tempIn);
        intrinsicBlur.forEach(tempOut);
        return outputBitmap;
    }

}
