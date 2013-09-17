package org.kbsriram.android.example;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import org.kbsriram.android.typesetview.TypesetView;

public class GlueDemoActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glue_demo);

        TypesetView ts = (TypesetView)
            findViewById(R.id.glue_demo_typesetview_a);
        ts.setTypeText("Adjustments - none.\n\n"+Sample.TEXT);
        ts = (TypesetView)
            findViewById(R.id.glue_demo_typesetview_b);
        ts.setTypeText("Adjustments - default.\n\n"+Sample.TEXT);
        ts = (TypesetView)
            findViewById(R.id.glue_demo_typesetview_c);
        ts.setTypeText("Adjustments - loose.\n\n"+Sample.TEXT);
    }
}
