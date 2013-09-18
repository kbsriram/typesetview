package org.kbsriram.android.example;

import android.app.Activity;
import android.os.Bundle;

public class MarginDemoActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.margin_demo);

        GlyphTypesetView ts = (GlyphTypesetView)
            findViewById(R.id.margin_demo_typesetview);
        ts.setTypeText(Sample.TEXT);
    }
}
