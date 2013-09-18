package org.kbsriram.android.example;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class MarginDemo2Activity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.margin_demo2);

        BitmapTypesetView ts = (BitmapTypesetView)
            findViewById(R.id.margin_demo2_typesetview);
        // shouldn't be doing any of this on the main thread.
        Bitmap bm = BitmapFactory.decodeResource
            (getResources(), R.drawable.wikipedia_frog_prince, null);
        ts.setTypeText(Sample.TEXT);
        ts.setBitmapFloatLeft(bm, ts.getLeading()/2f);
    }
}
