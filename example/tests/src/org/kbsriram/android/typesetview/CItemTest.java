package org.kbsriram.android.typesetview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.test.AndroidTestCase;
import android.text.TextPaint;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public class CItemTest extends AndroidTestCase
{
    public void testTokenizer()
    {
        Context ctx = getContext();

        // grab hold of our test-font. It contains "well-known" values
        // for the space and "x" characters. This lets me verify the final
        // values.
        Typeface testface = Typeface.createFromAsset
            (ctx.getAssets(), "test.ttf");

        TextPaint tp = new TextPaint
            (Paint.ANTI_ALIAS_FLAG     |
             Paint.SUBPIXEL_TEXT_FLAG  |
             Paint.DEV_KERN_TEXT_FLAG);

        tp.setTypeface(testface);
        tp.setTextSize(2048);

        Paint.FontMetrics fm = tp.getFontMetrics();
        assertEquals(1549f, tp.getFontMetrics(null));
        assertEquals(1024f, tp.measureText("x"));
        assertEquals(512f, tp.measureText(" "));

        
    }
}
