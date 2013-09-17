package org.kbsriram.android.typesetview;

// Essentially - handle all the mechanics of laying out text within a
// rectangle.

import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.List;
import android.graphics.Paint;
import android.text.TextPaint;

class CLayoutHelper
{
    CLayoutHelper(Context ctx)
    {
        m_paint = new TextPaint
            (Paint.ANTI_ALIAS_FLAG     |
             Paint.SUBPIXEL_TEXT_FLAG  |
             Paint.DEV_KERN_TEXT_FLAG);

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        m_paint.setTextSize(DEFAULT_TYPE_SIZE_SP*metrics.scaledDensity);
        m_paint.setColor(DEFAULT_TYPE_COLOR);
        m_paint.density = metrics.density;
        reset();
    }

    void setFromTextPaint(TextPaint paint, boolean force)
    {
        m_paint.set(paint);
        if (force) {
            m_paint.setFlags
                (m_paint.getFlags()        |
                 Paint.ANTI_ALIAS_FLAG     |
                 Paint.SUBPIXEL_TEXT_FLAG  |
                 Paint.DEV_KERN_TEXT_FLAG);
        }
        reset();
    }

    boolean setLinePosition(TypesetView.LinePosition lp)
    {
        if (lp == m_line_position) { return false; }
        m_line_position = lp;
        reset();
        return true;
    }

    boolean setUserColumnWidth(float v)
    {
        if (m_user_column_width == v) { return false; }

        if ((m_user_column_count > 0) && (v > 0)) {
            throw new IllegalArgumentException
                ("Cannot set both typeColumnWidth ("+m_user_column_count+
                 ") and typeColumnWidth ("+v+")");
        }
        m_user_column_width = v;
        reset();
        return true;
    }

    boolean setUserColumnCount(int n)
    {
        if (m_user_column_count == n) { return false; }

        if (n == 0) {
            throw new IllegalArgumentException
                ("Cannot have zero columns");
        }
        if ((m_user_column_width > 0) && (n > 0)) {
            throw new IllegalArgumentException
                ("Cannot set both typeColumnWidth ("+n+
                 ") and typeColumnWidth ("+m_user_column_width+")");
        }
        m_user_column_count = n;
        reset();
        return true;
    }

    void setText(CharSequence cs)
    {
        m_paras = CItem.fromCharSequence(m_paint, cs);
        reset();
    }

    void setTextColor(int color)
    { m_paint.setColor(color); }

    boolean setLeading(float v)
    {
        if (m_user_leading == v) { return false; }
        m_user_leading = v;
        reset();
        return true;
    }

    boolean setGutterWidth(float v)
    {
        if (m_user_gutter_width == v) { return false; }
        m_user_gutter_width = v;
        reset();
        return true;
    }

    boolean setTextSize(float v)
    {
        if (m_paint.getTextSize() == v) { return false; }
        m_paint.setTextSize(v);
        reset();
        return true;
    }

    boolean setMaximumLineStretch(float v)
    {
        if (m_user_max_line_stretch == v) { return false; }
        m_user_max_line_stretch = v;
        reset();
        return true;
    }

    boolean setMaximumGlueExpansionRatio(float v)
    {
        if (m_user_max_glue_fraction == (v - 1.0f)) { return false; }
        if (v < 1f) {
            throw new IllegalArgumentException
                ("Glue expansion ratio must be >= 1.0 (was "+v+")");
        }
        m_user_max_glue_fraction = v - 1.0f;
        reset();
        return true;
    }

    // avail_width must be > 0.
    // avail_height can be -1, in which case the layout will attempt
    // to minimize the total height used by spreading the columns
    // evenly.
    // return the column height, which can be useful when you pass in
    // avail_height == -1

    int setLayoutDimensions(int avail_width, int avail_height)
    {
        // Log.d(TAG, "set-layout-dimensions: "+avail_width+"x"+avail_height);

        ColInfo colinfo = computeColInfoFor(avail_width, avail_height);

        if (!colinfo.equals(m_column_info)) {
            //Log.d(TAG, "set-layout-dimensions: colinfo change "+
            // m_column_info+" -> "+colinfo);
            m_column_info = colinfo;
            reset();
        }

        if (maybeLayout()) {
            return m_column_info.m_height;
        }
        else {
            return 0;
        }
    }

    float getUnconstrainedWidth()
    {
        float ret = 0f;
        // Use a single column's width, if it
        // was provided.
        if (m_user_column_width > 0) {
            ret = m_user_column_width;
        }
        else if (m_paras != null) {
            // Use the first non-empty paragraph's width,
            // as if it were a single line.
            int max = m_paras.size();
            for (int i=0; i<max; i++) {
                List<CItem> para = m_paras.get(i);
                if (para.size() == 0) { continue; }
                CItem last = para.get(para.size()-1);
                ret = last.getSWidth()+last.getWidth();
                break;
            }
        }
        return ret;
    }

    final float getLeading()
    {
        if (m_user_leading > 0) { return m_user_leading; }

        Paint.FontMetrics fm = m_paint.getFontMetrics();
        return ((int)(0.5f + fm.descent - fm.ascent + fm.leading));
    }

    boolean hasContent()
    { return ((m_paras != null) && (m_paras.size() > 0)); }

    List<List<CItem>> getParagraphs()
    { return m_paras; }

    TextPaint getTextPaint()
    { return m_paint; }

    boolean maybeLayout()
    {
        if (m_paras == null) { return false; }
        if (m_state == State.OK) { return true; }
        if (m_column_info.m_column_width <= 0) { return false; }

        Paint.FontMetrics fm = m_paint.getFontMetrics();
        float leading = getLeading();
        float gutter_width;
        if (m_user_gutter_width > 0) { gutter_width = m_user_gutter_width; }
        else { gutter_width = leading; }

        TypesetView.LinePosition lp =
            (m_line_position != null)?m_line_position:
            new TypesetView.ConstantLinePosition(m_column_info.m_column_width);

        if (m_state == State.NEEDS_LINEBREAK) {

            // Log.d(TAG, "maybe-layout -> line-break with w="+
            // m_column_info.m_column_width);

            float max_line_stretch = (m_user_max_line_stretch > 0)?
                m_user_max_line_stretch:leading;
            float max_glue_fraction = (m_user_max_glue_fraction > 0)?
                m_user_max_glue_fraction:DEFAULT_GLUE_FRAC;

            int line_extra = 0;
            for (List<CItem> para: m_paras) {
                CKnuthPlass.layout
                    (para, lp, max_line_stretch, max_glue_fraction, line_extra);
                if (para.size() == 0) { line_extra ++; }
                else { line_extra += (para.get(para.size()-1).getLine()+1); }
            }
        }

        int height = m_column_info.m_height;

        if (height < 0) {
            // First divide the height evenly.

            // Log.d(TAG, "leading = "+leading);
            float even_height =
                (getLineCount()*leading)/m_column_info.m_column_count;
            // Log.d(TAG, "even-height = "+even_height);
            // Calculate lines/column.
            int col_lines = (int) Math.ceil(even_height/leading);
            // Log.d(TAG, "col_lines = "+col_lines);
            // Increase to accomodate top/bottom of font, if necessary.
            // Log.d(TAG, "fm.top="+fm.top+",fm.bottom="+fm.bottom);
            float adjust = (fm.bottom - fm.top) - leading;
            // Log.d(TAG, "adjust = "+adjust);
            if (adjust < 0) { adjust = 0; }
            height = (int) Math.ceil(col_lines*leading + adjust);
            // Log.d(TAG, "maybe-layout -> re-adjust height to "+height);
            m_column_info.m_height = height;
        }

        // Log.d(TAG, "Setting positions, ncols="+m_column_info.m_column_count+
        // ", height="+m_column_info.m_height);

        float y = -fm.top;
        float xadjust = 0f;
        int curline = 0;
        float curoffset = lp.getLeftOffset(0);

        for (List<CItem> para: m_paras) {
            int last_line = 0;
            for (CItem item: para) {
                if (item.getLine() > last_line) {
                    last_line = item.getLine();
                    y += leading;
                    curline++;
                    curoffset = lp.getLeftOffset(curline);
                    // System.out.println("curline: "+curline+" at "+item.getContent());
                    if (y > height) {
                        y = -fm.top;
                        xadjust +=
                            (m_column_info.m_column_width + gutter_width);
                    }
                }
                item.setAdjustedX(xadjust + item.getX() + curoffset);
                item.setAdjustedY(y);
            }
            y += leading;
            last_line = 0;
            curline++;
            curoffset = lp.getLeftOffset(curline);
            if (y > height) {
                y = -fm.top;
                xadjust +=
                    (m_column_info.m_column_width + gutter_width);
            }
        }
        m_state = State.OK;
        return true;
    }

    private void reset()
    {
        // Log.d(TAG, "resetting!");
        m_state = State.NEEDS_LINEBREAK;
    }

    private int getLineCount()
    {
        if (!hasContent()) { return 0; }

        int linecount = 0;
        for (List<CItem> para: m_paras) {
            if (para.size() == 0) {
                linecount ++;
            }
            else {
                linecount += (1 + para.get(para.size()-1).getLine());
            }
        }
        // Log.d(TAG, "linecount = "+linecount);
        return linecount;
    }

    private final ColInfo computeColInfoFor(int width, int height)
    {
        // Log.d(TAG, "computing colinfo for: "+width+"x"+height);
        int ncols;
        float gutter_width;
        if (m_user_gutter_width > 0) { gutter_width = m_user_gutter_width; }
        else { gutter_width = getLeading(); }

        if (m_user_column_count > 0) {
            ncols = m_user_column_count;
        }
        else if (m_user_column_width > 0) {
            ncols = (int)
                ((width + gutter_width)/
                 (m_user_column_width + gutter_width));
        }
        else {
            ncols = 1;
        }
        if (ncols <= 0) { ncols = 1; }
        float colwidth = (width - (ncols - 1)*gutter_width)/ncols;
        // Log.d(TAG, "return ["+ncols+","+colwidth+","+height+"]");
        return new ColInfo(ncols, colwidth, height);
    }

    private List<List<CItem>> m_paras = null;
    private TextPaint m_paint;
    private float m_user_gutter_width = -1f;
    private float m_user_leading = -1f;
    private float m_user_max_line_stretch = -1f;
    private float m_user_max_glue_fraction = -1f;
    private int m_user_column_count = -1;
    private float m_user_column_width = -1f;
    private TypesetView.LinePosition m_line_position = null;
    private ColInfo m_column_info = new ColInfo(1, -1f, -1);
    private State m_state = State.NEEDS_LINEBREAK;
    private final static String TAG = CLayoutHelper.class.getName();
    private final static float DEFAULT_TYPE_SIZE_SP = 16f;
    public final static int DEFAULT_TYPE_COLOR = 0xff333333;
    private final static float DEFAULT_GLUE_FRAC = 0.22f;
    private final static float DEFAULT_GUTTER_WIDTH_SP = 16;

    private final static class ColInfo
    {
        private ColInfo(int columncount, float columnwidth, int height)
        {
            m_column_count = columncount;
            m_column_width = columnwidth;
            m_height = height;
        }
        public boolean equals(ColInfo colinfo)
        {
            if (colinfo == null) { return false; }
            return
                (m_column_count == colinfo.m_column_count) &&
                (m_column_width == colinfo.m_column_width) &&
                (m_height == colinfo.m_height);
        }
        public String toString()
        { return "["+m_column_count+", "+m_column_width+", "+m_height+"]"; }

        private final int m_column_count;
        private final float m_column_width;
        private int m_height;
    }


    private enum State { NEEDS_LINEBREAK, NEEDS_POSITION, OK };
}
