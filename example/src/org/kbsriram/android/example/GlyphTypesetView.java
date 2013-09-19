package org.kbsriram.android.example;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.kbsriram.android.typesetview.TypesetView;

public class GlyphTypesetView
    extends TypesetView
{
    public GlyphTypesetView(Context ctx, AttributeSet attrs)
    { super(ctx, attrs); }

    public GlyphTypesetView(Context ctx)
    { super(ctx); }

    @Override
    public void setTypeText(CharSequence cs)
    {
        m_big_text = cs.subSequence(0, 1).toString();
        super.setTypeText(cs.subSequence(1, cs.length()));
    }

    public void setGlyphMultiplier(float v)
    {
        m_bigpaint_multiplier = v;
        requestLayout();
        invalidate();
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
        if (m_big_text != null) {
            canvas.save();
            try {
                canvas.translate(getPaddingLeft(), getPaddingTop());
                canvas.drawText
                    (m_big_text, m_bigpaint_x, m_bigpaint_y, m_bigpaint);
            }
            finally {
                canvas.restore();
            }
        }
    }

    private void setMarginPositionFromWidth(int width)
    {
        if (m_big_text == null) { return; }

        width -= (getPaddingLeft() + getPaddingRight());
        TextPaint mypaint = getPaint();
        m_bigpaint.set(mypaint);
        m_bigpaint.setAlpha(127);

        float leading = getLeading();
        m_bigpaint.setTextSize(leading*m_bigpaint_multiplier);

        Path path = new Path();
        m_bigpaint.getTextPath(m_big_text, 0, 1, 0, 0, path);

        // Find the bounds, and set it so it aligns with
        // a body text leading.
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        // Log.d(TAG, "bounds = "+bounds);

        float h = -bounds.top;
        int n = (int) Math.floor(h/leading);
        if (n <= 0) { n = 1; }
        m_bigpaint_y = n*leading;
        m_bigpaint_x = -bounds.left;

        Paint.FontMetrics fm = mypaint.getFontMetrics();
        float adjust = -fm.top - fm.leading;
        if (adjust > 0) { m_bigpaint_y += adjust; }
        // Log.d(TAG, "Glyph height = "+m_bigpaint_y);
        path.offset(m_bigpaint_x, m_bigpaint_y);

        PathMargin pm = new PathMargin(leading, -fm.top, path, leading/2f);
        setMarginPosition(pm);
    }

    private final TextPaint m_bigpaint = new TextPaint();
    private float m_bigpaint_x = 0f;
    private float m_bigpaint_y = 0f;
    private float m_bigpaint_multiplier = 10f;
    private String m_big_text = null;

    private final static class PathMargin
        implements MarginPosition
    {
        PathMargin
            (float leading, float top, Path path, float padding)
        {
            m_leading = leading;
            m_adjust = (top > leading)?(top-leading):0;
            m_path = path;
            m_padding = padding;
            m_bounds = new RectF();
            m_path.computeBounds(m_bounds, true);
            //Log.d(TAG, "bounds = "+m_bounds);
        }

        public float getLeftMargin(int line)
        {
            Float ret = m_cache.get(line);
            if (ret != null) {
                return ret.floatValue();
            }
            float offset = _getLeftMargin(line);
            m_cache.put(line, offset);
            //Log.d(TAG, "offset["+line+"] = "+offset);
            return offset;
        }

        private float _getLeftMargin(int line)
        {
            int cur_bottom = (int) Math.ceil(m_leading*(line+1));
            int cur_top = (int) ((cur_bottom - m_leading) - m_adjust);
            if (cur_top < 0) { cur_top = 0; }
            
            //Log.d(TAG, "line="+line+" top,bot="+cur_top+","+cur_bottom);
            
            // quickly check if we need to bother.
            if ((cur_top > m_bounds.bottom) ||
                (cur_bottom < m_bounds.top)) {
                return 0;
            }

            // We could be intersecting. First compute a region that's
            // the current line-based rectangle. Use that to compute a
            // region that's the path intersecting the clip region.
            int right_bound = (int) (0.5f + m_bounds.right);
            m_workregion.set(0, cur_top, right_bound, cur_bottom);
            m_workclip.set(0, cur_top, right_bound, cur_bottom);
            if (!m_workregion.setPath(m_path, m_workclip)) {
                return 0;
            }
            m_workrect.left = 0;
            m_workrect.right = right_bound;
            m_workrect.top = cur_top;
            m_workrect.bottom = cur_bottom;
            // Find a bounding rectangle for the path-clipped region.
            if (!m_workregion.getBounds(m_workrect)) {
                return 0;
            }
            return m_workrect.right + m_padding;
        }

        public float getRightMargin(int line)
        { return 0f; }

        private final float m_leading;
        private final float m_adjust;
        private final Path m_path;
        private final float m_padding;
        private final RectF m_bounds;
        private final Rect m_workrect = new Rect();
        private final Region m_workregion = new Region();
        private final Region m_workclip = new Region();
        private final Map<Integer,Float> m_cache =
            new HashMap<Integer, Float>();
    }
    private final static String TAG = GlyphTypesetView.class.getName();
}
