package org.kbsriram.android.widget;

import java.util.List;
import java.util.ArrayList;

import android.text.TextPaint;
import android.util.Log;
import android.text.Layout;

// Data structure to hold the Box, glue or penalty in the Knuth-Plass
// algorithm.
//
// Also contains methods to convert text into a list of such boxes.

class CItem
{
    enum Type { BOX, GLUE, PENALTY };

    // Returns a list of paragraphs. Each paragraph is a list of
    // CItems. The tokenizer ends a paragraph when it encounters a
    // newline.
    final static List<List<CItem>> fromCharSequence
        (TextPaint p, CharSequence cs)
    {
        List<List<CItem>> paras = new ArrayList<List<CItem>>();
        int len = cs.length();
        int cidx = 0;
        float ws_width = p.measureText(" ");
        float cum_width = 0f;
        String last_box = null;

        List<CItem> para = new ArrayList<CItem>();
        while (cidx < len) {
            char cur = cs.charAt(cidx++);

            if (cur == '\n') {
                // remove any following whitespace, unless there's
                // another para indicator.
                while ((cidx < len) &&
                       (cs.charAt(cidx) != '\n') &&
                       Character.isWhitespace(cs.charAt(cidx))) {
                    cidx++;
                }
                // end para.
                paras.add(para);
                para = new ArrayList<CItem>();
                cum_width = 0f;
                continue;
            }

            if (Character.isWhitespace(cur)) {
                // collapse any following whitespace into
                // a single glue.
                while ((cidx < len) &&
                       (cs.charAt(cidx) != '\n') &&
                       Character.isWhitespace(cs.charAt(cidx))) {
                    cidx++;
                }
                para.add
                    (new CItem
                     (Type.GLUE, ws_width, cum_width,
                      last_box+"|", para.size()));
                cum_width += ws_width;
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(cur);
            // accumulate any following non-whitespace
            while ((cidx < len) &&
                   !Character.isWhitespace(cur=cs.charAt(cidx))) {
                sb.append(cur);
                cidx++;
            }
            float txt_w = p.measureText(sb, 0, sb.length());
            para.add
                (new CItem(Type.BOX, txt_w, cum_width, sb, para.size()));
            cum_width += txt_w;
            last_box = sb.toString();
        }
        if (para.size() > 0) {
            paras.add(para);
        }
        return paras;
    }

    private CItem
        (Type t, float width, float x, CharSequence content, int index)
    {
        m_type = t;
        m_width = width;
        m_x = x;
        m_content = content;
        m_index = index;
    }
    public float getWidth()  { return m_width; }
    public float getX() { return m_x; }
    public float getY() { return m_y; }
    public void setX(float v) { m_x = v; }
    public void setY(float v) { m_y = v; }
    public Type getType() { return m_type; }
    public CharSequence getContent() { return m_content; }
    public int getIndex() { return m_index; }
    public String toString()
    { return m_index+": "+m_type+": ["+m_width+"]='"+m_content+"'"; }

    private final Type m_type;
    // To save memory, these value are aliased
    private float m_width;
    private float m_x;
    private float m_y;
    private final CharSequence m_content;
    private final int m_index;
    private final static String TAG = CItem.class.getName();
}
