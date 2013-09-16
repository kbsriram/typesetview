package org.kbsriram.android.typesetview;

import android.content.Context;
import android.test.AndroidTestCase;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public class CItemTest extends ATypefaceTest
{
    public void testTokenizer()
    {
        // Basic test.
        List<List<CItem>> paras = CItem.fromCharSequence
            (getTestPaint(), "xxx xx xx xxxxx");
        assertEquals(1, paras.size());
        checkWidths(new int[]{ 3, 2, 2, 5 }, paras.get(0));

        // Check that we collapse whitespace properly.
        paras = CItem.fromCharSequence
            (getTestPaint(), "  xxx    xx xx xxxxx   ");
        assertEquals(1, paras.size());
        checkWidths(new int[]{ 3, 2, 2, 5 }, paras.get(0));

        // Check multiple paragraphs are handled too.
        paras = CItem.fromCharSequence
            (getTestPaint(), "xxx xx xx xxxxx  \n xxx xx xx xxxxx\n\n  \n");
        assertEquals(4, paras.size());
        checkWidths(new int[]{ 3, 2, 2, 5 }, paras.get(0));
        checkWidths(new int[]{ 3, 2, 2, 5 }, paras.get(1));
        assertEquals(0, paras.get(2).size());
        assertEquals(0, paras.get(3).size());
    }

    private void checkWidths(int[] widths, List<CItem> para)
    {
        assertEquals(widths.length*2-1, para.size());

        float x = 0;
        for (int i=0; i<widths.length; i++) {
            float w = widths[i]*TEST_WIDTH;
            check(CItem.Type.BOX, x, w, para.get(i*2));
            x += w;
            if (i < (widths.length-1)) {
                check(CItem.Type.GLUE, x, TEST_WIDTH, para.get(i*2+1));
                x += TEST_WIDTH;
            }
        }
    }

    private void check(CItem.Type typ, float x, float w, CItem item)
    {
        assertEquals(typ, item.getType());
        assertEquals(x, item.getSWidth());
        assertEquals(w, item.getWidth());
    }
}
