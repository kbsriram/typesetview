package org.kbsriram.android.typesetview;

import android.content.Context;
import android.test.AndroidTestCase;
import java.util.List;
import static junit.framework.Assert.assertEquals;

public class CKnuthPlassTest extends ATypefaceTest
{
    public void testLineBreak()
    {
        assertEquals(COST_WIKI, layoutCost(WIKI_TEXT, 6));
        assertEquals(COST_80, layoutCost(HAND_COMPARISON, 80));
        assertEquals(COST_60, layoutCost(HAND_COMPARISON, 60));
        assertEquals(COST_40, layoutCost(HAND_COMPARISON, 40));
    }

    private int layoutCost(CharSequence cs, int width)
    {
        String lines[] = layout(cs, width).split("\\n");
        int cost = 0;
        for (int i = lines.length-2; i>=0; i--) {
            int cur = (width - lines[i].length());
            cost += (cur*cur);
        }
        return cost;
    }

    private String layout(CharSequence cs, int width)
    {
        List<List<CItem>> paras = CItem.fromCharSequence(getTestPaint(), cs);
        assertEquals(1, paras.size());
        List<CItem> para = paras.get(0);

        CKnuthPlass.layout
            (para,
             new TypesetView.ConstantLinePosition
             (width*TEST_WIDTH), 0f, 0f, 0);

        int last = -1;
        StringBuilder sb = new StringBuilder();
        for (CItem item: para) {
            if (item.getType() != CItem.Type.BOX) { continue; }
            if (last != item.getLine()) {
                if (last != -1) { sb.append("\n"); }
                last = item.getLine();
                sb.append(item.getContent());
            }
            else {
                sb.append(" ");
                sb.append(item.getContent());
            }
        }
        return sb.toString();
    }
    
    private final static String WIKI_TEXT = "xxx xx xx xxxxx";
    private final static String WIKI_COST = 10;
    private final static String HAND_COMPARISON = "xxx xxxx xxxxxxx x xxxxx xxxxxxxx xxx xx xxx xxxxxx xxx xxxxxx xxx xxxx xxx xx xxxx x xxxx xx xxxxxxx xx x xxxxx xxx xxxx xxx xxxx xx x xxxx xxxxxx xx xxxxxx xxxx xxxx xx xxx xxxxx xx xxx xxx xxx xxxxxxx xxxx xx xxxx x xxxxxx xxx xxx xxx x xxxxxx xxxx xx xxx xxxxx xxxxx xxx xxx xxxxxxxxx xxxxxxxxxx xxx xxx xxx xxxxxx xxxxxxx xx xx xxxx xxx xxxx xxx xxxxxxxx xx xxxxx xx xx xxxxx xxxxx x xxxx xxx xxxxx xx xx xx xxxx xxxx xxx xxxxxx xxxxxxxx xx xx xx xxxxx xxx xxx xxxx xxxxxxx xxxxx xxx xxxxxx xxxxx xxxx xxx xxxxxxx xxxx xx xxxx xx xxxx xxxx xxxx xxx xxxxxxx xxx xxxxxxxx xxxxxx xxxx xxx xxxxxx xxxxx xxx xxxxx xxx xx xxx xxxx xxxxx xx xxxx xxxx xxx xxxxx xxx xxx xxx xxxxxx xx xxx xxxx xxx xxxxx xx xxxxxx xxx xxxxx xxx xxxxx xxxxxx xx x xxxxx xxxx xxx xx xxxx xxxxxx x xxxxx xxxx xxx xx xxxx xxxxxxx xxx xxxxxxx xxx xxxxxxxxxx xxxx x xxxx xx xxx xxxxxxx";
    private final static int COST_80 = 71;
    private final static int COST_60 = 107;
    private final static int COST_40 = 118;
}
