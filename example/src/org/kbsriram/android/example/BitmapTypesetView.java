package org.kbsriram.android.example;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import org.kbsriram.android.typesetview.TypesetView;

public class BitmapTypesetView
    extends TypesetView
{
    public BitmapTypesetView(Context ctx, AttributeSet attrs)
    { super(ctx, attrs); }

    public BitmapTypesetView(Context ctx)
    { super(ctx); }

    boolean setBitmapFloatLeft(Bitmap bm, float pad)
    {
        if ((m_bitmap == bm) && (pad == m_bitmap_pad)) {
            return false;
        }
        m_bitmap = bm;
        m_bitmap_pad = pad;
        requestLayout();
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int wspec, int hspec)
    {
        setMarginPositionFromWidth(MeasureSpec.getSize(wspec));
        super.onMeasure(wspec, hspec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        setMarginPositionFromWidth(r - l);
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (m_bitmap != null) {
            canvas.drawBitmap
                (m_bitmap, getPaddingLeft(), getPaddingTop(), null);
        }
    }

    private void setMarginPositionFromWidth(int width)
    {
        if (m_bitmap == null) {
            // let the view use its defaults.
            setMarginPosition(null);
            return;
        }

        float view_width =
            width - getPaddingLeft() - getPaddingRight();
        float leading = getLeading();
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        float adjust = (fm.top > leading)?(fm.top-leading):0f;
        int nlines = (int) Math.ceil(m_bitmap.getHeight()/leading);
        float left_off = m_bitmap.getWidth() + m_bitmap_pad;
        setMarginPosition(new MP(left_off, nlines));
    }

    private Bitmap m_bitmap = null;
    private float m_bitmap_left = -1f;
    private float m_bitmap_pad = -1f;

    private final static class MP
        implements MarginPosition
    {
        MP(float left_margin, int nlines)
        {
            m_left_margin = left_margin;
            m_nlines = nlines;
        }

        public float getLeftMargin(int line)
        {
            if (line < m_nlines) { return m_left_margin; }
            else { return 0f; }
        }

        public float getRightMargin(int line)
        { return 0f; }

        private final float m_left_margin;
        private final int m_nlines;
    }
    private final static String TAG = BitmapTypesetView.class.getName();
}
