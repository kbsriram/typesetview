package org.kbsriram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import java.util.List;
import org.kbsriram.android.R;

public class TypesetView
    extends View
{
    public TypesetView(Context ctx)
    {
        super(ctx);
        initTypesetView(ctx);
    }

    public TypesetView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        initTypesetView(ctx);
        TypedArray a = ctx.obtainStyledAttributes
            (attrs, R.styleable.TypesetView);

        setTypeColor
            (a.getColor(R.styleable.TypesetView_typeColor, DEFAULT_TYPE_COLOR));

        float v = a.getDimension(R.styleable.TypesetView_typeSize, -1f);
        if (v > 0) { setTypeSize(v); }

        v = a.getDimension(R.styleable.TypesetView_typeLeading, -1f);
        if (v > 0) { setTypeLeading(v); }

        v = a.getDimension(R.styleable.TypesetView_typeMaximumLineStretch, -1f);
        if (v > 0) { setTypeMaximumLineStretch(v); }

        v = a.getFloat
            (R.styleable.TypesetView_typeMaximumGlueExpansionRatio, -1f);
        if (v > 0) { setTypeMaximumGlueExpansionRatio(v); }

        CharSequence s = a.getString(R.styleable.TypesetView_typeText);
        if (s != null) { setTypeText(s); }
        a.recycle();
    }

    public void setTextPaintFrom(TextPaint tp)
    {
        m_paint.set(tp);
        m_dirty = true;
        requestLayout();
        invalidate();
    }

    public void setTypeText(CharSequence cs)
    {
        m_paras = CItem.fromCharSequence(m_paint, cs);
        m_dirty = true;
        requestLayout();
        invalidate();
    }

    public void setTypeColor(int color)
    {
        m_paint.setColor(color);
        invalidate();
    }

    public void setTypeLeading(float v)
    {
        m_user_leading = v;
        invalidate();
    }

    public void setTypeSize(float v)
    {
        m_paint.setTextSize(v);
        m_dirty = true;
        requestLayout();
        invalidate();
    }

    public void setTypeMaximumLineStretch(float v)
    {
        m_user_max_line_stretch = v;
        m_dirty = true;
        requestLayout();
        invalidate();
    }

    public void setTypeMaximumGlueExpansionRatio(float v)
    {
        if (v < 1f) {
            throw new IllegalArgumentException
                ("Glue expansion ratio must be >= 1.0 (was "+v+")");
        }
        m_user_max_glue_fraction = v - 1.0f;
        m_dirty = true;
        requestLayout();
        invalidate();
    }

    private final void initTypesetView(Context ctx)
    {
        m_paint = new TextPaint
            (Paint.ANTI_ALIAS_FLAG     |
             Paint.SUBPIXEL_TEXT_FLAG  |
             Paint.DEV_KERN_TEXT_FLAG);

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        m_paint.setTextSize(DEFAULT_TYPE_SIZE_SP*metrics.scaledDensity);
        m_paint.setColor(DEFAULT_TYPE_COLOR);
        m_paint.density = metrics.density;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int avail_width = r - l - getPaddingLeft() - getPaddingRight();
        Log.d(TAG, "on-layout: avail_width="+avail_width+",m_linewidth="+
              m_linewidth);
        if (avail_width != m_linewidth) {
            m_linewidth = avail_width;
            m_dirty = true;
        }
    }

    @Override
    protected void onMeasure(int wspec, int hspec)
    {
        Log.d(TAG, "wspec="+MeasureSpec.toString(wspec)+
              ",hspec="+MeasureSpec.toString(hspec)+",m_linewidth="+
              m_linewidth);
        // If we don't have any text, set generic defaults for width
        // and height and be done.
        if ((m_paras == null) || (m_paras.size() == 0)) {
            super.onMeasure(wspec, hspec);
            return;
        }

        Paint.FontMetrics fm = m_paint.getFontMetrics();
        // First get a reasonable value for our width.
        int wsize = MeasureSpec.getSize(wspec);
        int wmode = MeasureSpec.getMode(wspec);

        if (wmode == MeasureSpec.UNSPECIFIED) {
            // just make it a one-liner from the first
            // paragraph.
            wsize = (int)
                (0.5f+getPaddingLeft()+getPaddingRight()+
                 firstParaRawWidth());
        }

        int line_width = wsize - getPaddingLeft() - getPaddingRight();
        if (line_width < 0) {
            line_width = 0;
        }

        if (line_width != m_linewidth) {
            m_linewidth = line_width;
            m_dirty = true;
        }

        int hmode = MeasureSpec.getMode(hspec);
        int hsize = MeasureSpec.getSize(hspec);

        // If our height is fixed, just use it.
        if (hmode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(wsize, hsize);
            return;
        }

        // Layout our text if necessary.
        if (m_dirty) {
            layoutText(m_linewidth);
        }

        float layout_height;

        CItem item = findLastItem();
        if (item == null) {
            // go back to defaults.
            layout_height = getSuggestedMinimumHeight();
        }
        else {
            layout_height = item.getY();
        }

        int desired_height = (int)
            (0.5f + layout_height + fm.bottom +
             getPaddingTop() + getPaddingBottom());
        if (hmode == MeasureSpec.AT_MOST) {
            if (desired_height > hsize) {
                desired_height = hsize;
            }
        }
        setMeasuredDimension(wsize, desired_height);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if ((m_paras == null) || (m_linewidth <= 0)) { return; }

        if (m_dirty) { layoutText(m_linewidth); }

        canvas.save();
        try {
            canvas.translate(getPaddingLeft(), getPaddingTop());
            boolean have_bounds = canvas.getClipBounds(m_cliprect);

            for (List<CItem> para: m_paras) {
                for (CItem item: para) {
                    if (item.getType() == CItem.Type.BOX) {
                        if (have_bounds && canSkipItem(canvas, item)) {
                            continue;
                        }
                        canvas.drawText
                            (item.getContent(), 0, item.getContent().length(),
                             item.getX(), item.getY(), m_paint);
                    }
                }
            }
        }
        finally {
            canvas.restore();
        }
    }

    private final boolean canSkipItem(Canvas c, CItem item)
    {
        return c.quickReject
            (item.getX(), item.getY()-m_leading,
             item.getX()+item.getWidth(),item.getY()+m_leading,
             Canvas.EdgeType.AA);
    }

    private CItem findLastItem()
    {
        if (m_paras == null) {
            return null;
        }
        int idx = m_paras.size()-1;
        while (idx >= 0) {
            List<CItem> para = m_paras.get(idx--);
            if (para.size() == 0) { continue; }
            return para.get(para.size()-1);
        }
        return null;
    }

    private float firstParaRawWidth()
    {
        if (m_paras == null) {
            return 0f;
        }
        int max = m_paras.size();
        for (int i=0; i<max; i++) {
            List<CItem> para = m_paras.get(i);
            if (para.size() == 0) { continue; }
            CItem last = para.get(para.size()-1);
            return last.getSWidth()+last.getWidth();
        }
        return 0f;
    }

    private void layoutText(int line_width)
    {
        Log.d(TAG, "layout-text: "+line_width);
        Paint.FontMetrics fm = m_paint.getFontMetrics();
        if (m_user_leading > 0) {
            m_leading = m_user_leading;
        }
        else {
            m_leading = (int) (0.5f+fm.descent - fm.ascent + fm.leading);
        }
        float top = -fm.top;
        float max_line_stretch = (m_user_max_line_stretch > 0)?
            m_user_max_line_stretch:m_leading;
        float max_glue_fraction = (m_user_max_glue_fraction > 0)?
            m_user_max_glue_fraction:DEFAULT_GLUE_FRAC;

        CKnuthPlass.LineLength ll =
            new CKnuthPlass.ConstantLineLength(line_width);
        float cury = top;
        for (List<CItem> para: m_paras) {
            CKnuthPlass.layout(para, ll, max_line_stretch, max_glue_fraction);
            // update y, based on leading, etc.
            float lastline = -1f;
            for (CItem item: para) {
                float curline = item.getY();
                if (lastline < 0) {
                    lastline = curline;
                }
                else if (curline > lastline) {
                    lastline = curline;
                    cury = cury + m_leading;
                }
                item.setY(cury);
            }
            cury += m_leading;
        }
        m_dirty = false;
    }

    private int m_linewidth = -1;
    private List<List<CItem>> m_paras = null;
    private TextPaint m_paint;
    private float m_leading;
    private float m_user_leading = -1f;
    private float m_user_max_line_stretch = -1f;
    private float m_user_max_glue_fraction = -1f;
    private boolean m_dirty = true;
    private final Rect m_cliprect = new Rect();
    private final static String TAG = TypesetView.class.getName();
    private final static float DEFAULT_TYPE_SIZE_SP = 16f;
    private final static int DEFAULT_TYPE_COLOR = 0xff333333;
    private final static float DEFAULT_GLUE_FRAC = 0.22f;
}
