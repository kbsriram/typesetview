package org.kbsriram.android.typesetview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.test.AndroidTestCase;
import android.text.TextPaint;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public abstract class ATypefaceTest extends AndroidTestCase
{
    private Typeface m_testface;
    private TextPaint m_testpaint;

    public void setUp()
        throws Exception
    {
        super.setUp();

        Context ctx = getContext();

        // grab hold of our test-font.
        m_testface = Typeface.createFromAsset
            (ctx.getAssets(), "DroidSansMono.ttf");

        m_testpaint = new TextPaint
            (Paint.ANTI_ALIAS_FLAG     |
             Paint.SUBPIXEL_TEXT_FLAG  |
             Paint.DEV_KERN_TEXT_FLAG);

        m_testpaint.setTypeface(m_testface);
        m_testpaint.setTextSize(2048);

        Paint.FontMetrics fm = m_testpaint.getFontMetrics();
        assertEquals(TEST_LEADING, m_testpaint.getFontMetrics(null));
        assertEquals(TEST_WIDTH, m_testpaint.measureText("x"));
        assertEquals(TEST_WIDTH, m_testpaint.measureText(" "));
    }

    protected TextPaint getTestPaint()
    { return m_testpaint; }
    protected Typeface  getTestTypeface()
    { return m_testface; }
    final static float TEST_LEADING = 2384f;
    final static float TEST_WIDTH = 1229f;
}
