package org.kbsriram.android.example;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import org.kbsriram.android.typesetview.TypesetView;

public class CompareDemoActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare_demo);

        String text = "Textview is red, TypesetView is blue\n\n"+
            Sample.TEXT;

        TextView tv = (TextView)
            findViewById(R.id.compare_demo_textview);
        tv.setPaintFlags
            (tv.getPaintFlags() |
             Paint.ANTI_ALIAS_FLAG |
             Paint.SUBPIXEL_TEXT_FLAG |
             Paint.DEV_KERN_TEXT_FLAG);
        tv.setText(text);

        TypesetView ts = (TypesetView)
            findViewById(R.id.compare_demo_typesetview_a);
        ts.setTypeText(text);
    }
}
