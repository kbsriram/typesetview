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

public class MarginTypesetView
    extends TypesetView
{
    public MarginTypesetView(Context ctx, AttributeSet attrs)
    { super(ctx, attrs); }

    public MarginTypesetView(Context ctx)
    { super(ctx); }

    @Override
    protected void onMeasure(int wspec, int hspec)
    {
        setLinePositionFromWidth(MeasureSpec.getSize(wspec));
        super.onMeasure(wspec, hspec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        setLinePositionFromWidth(r - l);
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save();
        try {
            canvas.translate(getPaddingLeft(), getPaddingTop());
            canvas.drawText(BIG_TEXT, 0, m_bigpaint_y, m_bigpaint);
        }
        finally {
            canvas.restore();
        }
    }

    private void setLinePositionFromWidth(int width)
    {
        width -= (getPaddingLeft() + getPaddingRight());
        TextPaint mypaint = getPaint();
        m_bigpaint.set(mypaint);
        m_bigpaint.setAlpha(127);

        float leading = getLeading();
        m_bigpaint.setTextSize(leading*10f);

        Paint.FontMetrics bigfm = m_bigpaint.getFontMetrics();
        m_bigpaint_y = -bigfm.ascent*0.8f;

        Paint.FontMetrics fm = mypaint.getFontMetrics();

        Path path = new Path();
        m_bigpaint.getTextPath(BIG_TEXT, 0, 1, 0, m_bigpaint_y, path);
        PathMargin pm = new PathMargin
            (width, leading, -fm.top, path, leading/2f);
        setLinePosition(pm);
    }

    private final TextPaint m_bigpaint = new TextPaint();
    private float m_bigpaint_y = 0f;
    private final String BIG_TEXT = "O";

    private final static class PathMargin
        implements LinePosition
    {
        PathMargin
            (float width, float leading, float top, Path path, float padding)
        {
            m_width = width;
            m_leading = leading;
            m_adjust = (top > leading)?(top-leading):0;
            m_path = path;
            m_padding = padding;
            m_bounds = new RectF();
            m_path.computeBounds(m_bounds, true);
            //Log.d(TAG, "bounds = "+m_bounds);
        }

        public float getLeftOffset(int line)
        {
            Float ret = m_cache.get(line);
            if (ret != null) {
                return ret.floatValue();
            }
            float offset = _getLeftOffset(line);
            m_cache.put(line, offset);
            //Log.d(TAG, "offset["+line+"] = "+offset);
            return offset;
        }

        private float _getLeftOffset(int line)
        {
            int cur_bottom = (int) Math.ceil(m_leading*(line+1));
            int cur_top = (int) ((cur_bottom - m_leading) - m_adjust);
            if (cur_top < 0) { cur_top = 0; }
            
            int width = (int) m_width;

            //Log.d(TAG, "line="+line+" top,bot="+cur_top+","+cur_bottom);
            
            // quickly check if we need to bother.
            if ((cur_top > m_bounds.bottom) ||
                (cur_bottom < m_bounds.top)) {
                return 0;
            }
            
            // We could be intersecting. First compute a region that's
            // the current line-based rectangle. Use that to compute a
            // region that's the path intersecting the clip region.
            m_workregion.set(0, cur_top, width, cur_bottom);
            m_workclip.set(0, cur_top, width, cur_bottom);
            if (!m_workregion.setPath(m_path, m_workclip)) {
                return 0;
            }
            m_workrect.left = 0;
            m_workrect.right = width;
            m_workrect.top = cur_top;
            m_workrect.bottom = cur_bottom;
            // Find a bounding rectangle for the path-clipped region.
            if (!m_workregion.getBounds(m_workrect)) {
                return 0;
            }
            return m_workrect.right + m_padding;
        }

        public float getLineLength(int line)
        { return m_width - getLeftOffset(line); }

        /*
        @Override
        public boolean equals(Object o)
        {
            if (o == null) { return false; }
            if (!(o instanceof PathMargin)) { return false; }
            PathMargin other = (PathMargin) o;
            return
                (m_width == other.m_width) &&
                (m_leading == other.m_leading) &&
                (m_adjust == other.m_adjust) &&
                (m_path.equals(other.m_path)) &&
                (m_padding == other.m_padding) &&
                (m_bounds.equals(other.m_bounds));
        }
        */

        private final float m_width;
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
    private final static String TAG = MarginTypesetView.class.getName();
}
