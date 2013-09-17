package org.kbsriram.android.example;

import android.app.Activity;
import android.os.Bundle;
import org.kbsriram.android.typesetview.TypesetView;

public class MulticolumnDemoActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multicol_demo);

        TypesetView ts = (TypesetView)
            findViewById(R.id.multicol_demo_typesetview);
        ts.setTypeText
            ("Set type in very narrow columns, and add columns automatically.\n\n"+
             Sample.TEXT);
    }
}
