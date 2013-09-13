package org.kbsriram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;
import java.util.List;

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
    }

    public void setTextPaintFrom(TextPaint tp, CharSequence text)
    {
        m_paint.set(tp);
        m_paint.setColor(0xffcc0000);

        m_paras = CItem.fromCharSequence(m_paint, text);
        m_dirty = true;
        invalidate();
    }

    private final void initTypesetView(Context ctx)
    {
        m_paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        m_paint.setTextSize(16f);
        m_paint.setColor(0xffcc0000);
        m_paint.density = ctx.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        m_linewidth = (r - l);
        m_dirty = true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if ((m_paras == null) || (m_linewidth <= 0)) { return; }

        Paint.FontMetrics fm = m_paint.getFontMetrics();
        float leading = (int) (0.5f+fm.descent - fm.ascent + fm.leading);
        float top = -fm.top;
        if (m_dirty) { layoutText(top, leading, leading, 0.22f); }

        for (List<CItem> para: m_paras) {
            for (CItem item: para) {
                if (item.getType() == CItem.Type.BOX) {
                    canvas.drawText
                        (item.getContent(), 0, item.getContent().length(),
                         item.getX(), item.getY(), m_paint);
                    // Log.d(TAG, item.getContent()+":x="+item.getX()+",y="+item.getY());
                }
            }
        }
    }

    private void layoutText
        (float top, float leading, float max_line_stretch,
         float max_glue_fraction)
    {
        CKnuthPlass.LineLength ll =
            new CKnuthPlass.ConstantLineLength(m_linewidth);
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
                    cury = cury + leading;
                }
                item.setY(cury);
            }
            cury += leading;
        }
        m_dirty = false;
    }

    private int m_linewidth = -1;
    private List<List<CItem>> m_paras = null;
    private TextPaint m_paint;
    private boolean m_dirty = true;
    private final static String TAG = TypesetView.class.getName();
}
