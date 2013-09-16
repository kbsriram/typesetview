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
        m_layouthelper = new CLayoutHelper(ctx);
    }

    public TypesetView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        m_layouthelper = new CLayoutHelper(ctx);
        TypedArray a = ctx.obtainStyledAttributes
            (attrs, R.styleable.TypesetView);

        setTypeColor
            (a.getColor(R.styleable.TypesetView_typeColor,
                        CLayoutHelper.DEFAULT_TYPE_COLOR));

        float v = a.getDimension(R.styleable.TypesetView_typeSize, -1f);
        if (v > 0) { setTypeSize(v); }

        v = a.getDimension(R.styleable.TypesetView_typeLeading, -1f);
        if (v > 0) { setTypeLeading(v); }

        v = a.getDimension(R.styleable.TypesetView_typeGutterWidth, -1f);
        if (v > 0) { setTypeGutterWidth(v); }

        v = a.getDimension(R.styleable.TypesetView_typeMaximumLineStretch, -1f);
        if (v > 0) { setTypeMaximumLineStretch(v); }

        v = a.getFloat
            (R.styleable.TypesetView_typeMaximumGlueExpansionRatio, -1f);
        if (v > 0) { setTypeMaximumGlueExpansionRatio(v); }

        v = a.getFloat
            (R.styleable.TypesetView_typeColumnWidth, -1f);
        if (v > 0) { setTypeColumnWidth(v); }

        int n = a.getInt
            (R.styleable.TypesetView_typeColumnCount, -1);
        if (n > 0) { setTypeColumnCount(n); }

        CharSequence s = a.getString(R.styleable.TypesetView_typeText);
        if (s != null) { setTypeText(s); }
        a.recycle();
    }

    public void setTextPaintFrom(TextPaint tp)
    { justSetTextPaintAndIKnowWhatImDoing(tp, true); }

    public void justSetTextPaintAndIKnowWhatImDoing
        (TextPaint tp, boolean force)
    {
        m_layouthelper.setFromTextPaint(tp, force);
        requestLayout();
        invalidate();
    }

    public void setTypeColumnWidth(float v)
    {
        if (m_layouthelper.setUserColumnWidth(v)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeColumnCount(int n)
    {
        if (m_layouthelper.setUserColumnCount(n)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeText(CharSequence cs)
    {
        m_layouthelper.setText(cs);
        requestLayout();
        invalidate();
    }

    public void setTypeColor(int color)
    {
        m_layouthelper.setTextColor(color);
        invalidate();
    }

    public void setTypeLeading(float v)
    {
        if (m_layouthelper.setLeading(v)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeGutterWidth(float v)
    {
        if (m_layouthelper.setGutterWidth(v)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeSize(float v)
    {
        if (m_layouthelper.setTextSize(v)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeMaximumLineStretch(float v)
    {
        if (m_layouthelper.setMaximumLineStretch(v)) {
            requestLayout();
            invalidate();
        }
    }

    public void setTypeMaximumGlueExpansionRatio(float v)
    {
        if (m_layouthelper.setMaximumGlueExpansionRatio(v)) {
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int avail_width = r - l - getPaddingLeft() - getPaddingRight();
        int avail_height = b - t - getPaddingTop() - getPaddingBottom();

        Log.d(TAG, "on-layout: avail_width="+avail_width);
        m_layouthelper.setLayoutDimensions(avail_width, avail_height);
    }

    @Override
    protected void onMeasure(int wspec, int hspec)
    {
        Log.d(TAG, "wspec="+MeasureSpec.toString(wspec)+
              ",hspec="+MeasureSpec.toString(hspec));

        // If we don't have any content, set generic defaults for
        // width and height and be done.
        if (!m_layouthelper.hasContent()) {
            super.onMeasure(wspec, hspec);
            return;
        }

        // First get a reasonable value for our width.
        int wsize = MeasureSpec.getSize(wspec);
        int wmode = MeasureSpec.getMode(wspec);

        if (wmode == MeasureSpec.UNSPECIFIED) {
            wsize = (int) (0.5f + getPaddingLeft()+getPaddingRight()+
                           m_layouthelper.getUnconstrainedWidth());
        }

        int avail_width = wsize - getPaddingLeft() - getPaddingRight();
        if (avail_width < 0) {
            avail_width = 0;
        }

        // Now for the height.
        int hmode = MeasureSpec.getMode(hspec);
        int hsize = MeasureSpec.getSize(hspec);

        int avail_height;

        if (hmode == MeasureSpec.EXACTLY) {
            avail_height = hsize - getPaddingTop() - getPaddingBottom();
        }
        else {
            // ask the helper to provide a suitable height.
            avail_height = -1;
        }

        // run the layout algorithm
        int measured_height = m_layouthelper.setLayoutDimensions
            (avail_width, avail_height);

        // Few more checks to merge with our layout request.
        if (hmode == MeasureSpec.EXACTLY) {
            measured_height = hsize;
        }
        else {
            measured_height += (getPaddingTop() + getPaddingBottom());
            if ((hmode == MeasureSpec.AT_MOST) &&
                (measured_height > hsize)) {
                measured_height = hsize;
            }
        }
        Log.d(TAG, "Setting measured-size: "+wsize+"x"+measured_height);
        setMeasuredDimension(wsize, measured_height);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (!m_layouthelper.maybeLayout()) {
            return;
        }

        canvas.save();
        try {
            canvas.translate(getPaddingLeft(), getPaddingTop());
            Paint p = m_layouthelper.getTextPaint();
            float leading = m_layouthelper.getLeading();
            boolean have_bounds = canvas.getClipBounds(m_cliprect);

            for (List<CItem> para: m_layouthelper.getParagraphs()) {
                for (CItem item: para) {
                    if (item.getType() == CItem.Type.BOX) {
                        if (have_bounds &&
                            canSkipItem(canvas, item, leading)) {
                            continue;
                        }
                        CharSequence cs = item.getContent();
                        canvas.drawText
                            (cs, 0, cs.length(),
                             item.getAdjustedX(), item.getAdjustedY(), p);
                    }
                }
            }
        }
        finally {
            canvas.restore();
        }
    }

    private final boolean canSkipItem(Canvas c, CItem item, float leading)
    {
        float x = item.getAdjustedX();
        float y = item.getAdjustedY();
        return c.quickReject
            (x, y-leading, x+item.getWidth(), y+leading, Canvas.EdgeType.AA);
    }

    private final CLayoutHelper m_layouthelper;
    private final Rect m_cliprect = new Rect();
    private final static String TAG = TypesetView.class.getName();
}
