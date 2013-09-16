package org.kbsriram.android.typesetview;

import android.content.Context;
import android.graphics.Paint;
import android.test.AndroidTestCase;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public class CLayoutHelperTest extends ATypefaceTest
{
    public void testColumnSplit()
    {
        CLayoutHelper helper = new CLayoutHelper(getContext());
        helper.setFromTextPaint(getTestPaint(), false);

        helper.setUserColumnWidth(TEST_WIDTH*5);
        helper.setGutterWidth(10f);
        helper.setText("xxxxx xxxxx");

        // See if we can get it to wrap_content two columns
        // with one line each.
        Paint.FontMetrics fm = getTestPaint().getFontMetrics();
        assertEquals
            ((int)(fm.bottom-fm.top),
             helper.setLayoutDimensions
             ((int) (0.5f+TEST_WIDTH*2*5+10f), -1));

        // I had a bug with empty paragraphs, so make sure we
        // handle those too. This should result in 4 total lines,
        // and a 2-line column
        helper.setText("xxxxx xxxxx\n\nxxxxx");
        assertEquals
            ((int) (TEST_LEADING + fm.bottom-fm.top),
             helper.setLayoutDimensions
             ((int) (0.5f+TEST_WIDTH*2*5+10f), -1));
    }
}
