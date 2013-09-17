package org.kbsriram.android.typesetview;

import android.content.Context;
import android.test.AndroidTestCase;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public class CKnuthPlassTest extends ATypefaceTest
{
    public void testLineBreak()
    {
        List<List<CItem>> paras = CItem.fromCharSequence
            (getTestPaint(), "xxx xx xx xxxxx");

        // wikipedia test.
        List<CItem> para = paras.get(0);
        CKnuthPlass.layout
            (para,
             new TypesetView.ConstantLinePosition
             (6*TEST_WIDTH), 0f, 0f, 0);

        // Expect a break after the first word and the
        // third word.
        assertEquals(1, para.get(2).getLine());
        assertEquals(0f, para.get(2).getX());
        assertEquals(2, para.get(6).getLine());
        assertEquals(0f, para.get(6).getX());
    }
}
