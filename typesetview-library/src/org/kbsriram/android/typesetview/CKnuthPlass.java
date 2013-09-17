package org.kbsriram.android.typesetview;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

// Knuth-Plass line-breaking algorithm - modified to use the square of
// the ragged-right margin as the badness score. Ie - the goal of the
// algorithm is to minimize the square of the size of the gap at the
// end of each line, summed over all lines. The last line is not
// included in this calculation.

public class CKnuthPlass
{
    public static void layout
        (List<CItem> items, TypesetView.LinePosition lp,
         float max_line_stretch, float max_glue_fraction,
         int line_number_offset)
    {
        Node base = findBest(items, lp, line_number_offset);

        // Traverse base backwards to identify the line break
        // indices within the items list.
        Node curnode = base;
        List<Integer> breaks = new ArrayList<Integer>();
        while (curnode != null) {
            CItem item = curnode.getItem();
            if (item != null) {
                breaks.add(item.getIndex());
            }
            curnode = curnode.getParent();
        }

        // The breaks list is in reverse. Walk through our
        // items, calculating x/y positions - and update
        // y position whenever we hit a break index.
        int cur_break_index;
        if (breaks.size() > 0) {
            cur_break_index = breaks.remove(breaks.size()-1);
        }
        else {
            cur_break_index = -1;
        }
        float x = 0f;
        int line = 0;
        int curline_start_index = 0;
        for (CItem item: items) {
            item.setX(x);
            item.setLine(line);
            if (item.getIndex() == cur_break_index) {
                // Slightly redistribute x positions to available
                // space.
                readjustGlue
                    (items, curline_start_index, item.getIndex()-1,
                     x, lp.getLineLength(line+line_number_offset),
                     max_line_stretch, max_glue_fraction);
                x = 0f;
                line++;
                curline_start_index = item.getIndex()+1;
                if (breaks.size() > 0) {
                    cur_break_index = breaks.remove(breaks.size()-1);
                }
            }
            else {
                x += item.getWidth();
            }
        }
    }

    private static void readjustGlue
        (List<CItem> items, int start, int end,
         float actual_length, float target_length,
         float max_line_stretch, float max_glue_stretch_fraction)
    {
        int nglue = (end - start)/2;

        // No glue, no clue
        if (nglue < 1) { return; }

        float delta = target_length - actual_length;
        if (delta <= 0) { return; }

        if (delta >= max_line_stretch) { delta = max_line_stretch; }

        float adjust = delta/nglue;

        CItem item = items.get(start);
        float curadjust = 0f;
        for (int i=start+1; i<=end; i++) {
            item = items.get(i);
            item.setX(item.getX()+curadjust);
            if (item.getType() == CItem.Type.GLUE) {
                float max = item.getWidth()*max_glue_stretch_fraction;
                if (adjust < max) {
                    curadjust += adjust;
                }
                else {
                    curadjust += max;
                }
            }
        }
    }

    private static Node findBest
        (List<CItem> items, TypesetView.LinePosition lp, int line_number_offset)
    {
        Set<Node> active = new HashSet<Node>();
        active.add(new Node(null, 0, 0, null));

        Set<Node> feasible_parents = new HashSet<Node>();

        int item_last = items.size()-1;
        for (CItem item: items) {
            if (!isLegalBreakpoint(item)) {
                // clear out infeasible nodes from the active set,
                // if we're the last entry and we're a box.
                if ((item.getIndex() == item_last) &&
                    (item.getType() == CItem.Type.BOX)) {
                    for (Iterator<Node> it=active.iterator(); it.hasNext();) {
                        Node an = it.next();
                        CItem start = an.getItem();
                        float gap = findGap
                            (start, item, lp.getLineLength
                             (an.getLineNo()+line_number_offset));
                        gap -= item.getWidth();
                        if (gap < 0) {
                            it.remove();
                            continue;
                        }
                    }
                }
                continue;
            }

            //System.out.println();
            //System.out.println("Considering: "+item);

            feasible_parents.clear();
            float best_cost = Float.MAX_VALUE;
            Node fallback = null;
            float best_fallback_cost = Float.MAX_VALUE;

            for (Iterator<Node> it=active.iterator(); it.hasNext();) {
                Node an = it.next();
                CItem start = an.getItem();
                float gap = findGap
                    (start, item, lp.getLineLength
                     (an.getLineNo()+line_number_offset));
                if (gap < 0) {
                    //System.out.println("Remove "+start+" from active");
                    if (-gap < best_fallback_cost) {
                        fallback = an;
                        best_fallback_cost = -gap;
                    }
                    it.remove();
                    continue;
                }
                //System.out.println("Gap = "+gap);
                feasible_parents.add(an);
                 float curcost = costFrom(gap, an);
                //System.out.println("Cost from: "+an+" = "+curcost);
                an.curryCost(curcost);
                if (curcost < best_cost) { best_cost = curcost; }
            }

            if (feasible_parents.isEmpty()) {
                feasible_parents.add(fallback);
                best_cost = 0f;
                fallback.curryCost(0f);
            }

            //System.out.println("best_cost: "+best_cost);
            for (Node feasible: feasible_parents) {
                if (feasible.getCurriedCost() == best_cost) {
                    //System.out.println("  add "+feasible.getItem());
                    active.add
                        (new Node
                         (item, best_cost,
                          feasible.getLineNo()+1, feasible));
                }
            }
            //System.out.println("active nodes:");
            //System.out.println(active);
        }

        // System.out.println("-------------");
        float best = Float.MAX_VALUE;
        Node ret = null;
        for (Node n: active) {
            //System.out.println(n);
            if (n.getCost() < best) {
                best = n.getCost();
                ret = n;
            }
        }
        return ret;
    }

    private final static float costFrom(float gap, Node parent)
    { return gap*gap + parent.getCost(); }

    private final static boolean isLegalBreakpoint(CItem item)
    { return item.getType() == CItem.Type.GLUE; }

    private final static float findGap(CItem start, CItem end, float linewidth)
    {
        float delta = end.getSWidth();
        if (start != null) {
            delta -= (start.getSWidth()+start.getWidth());
        }
        return linewidth - delta;
    }

    private final static class Node
    {
        private Node(CItem item, float cost, int lineno, Node parent)
        {
            m_item = item;
            m_cost = cost;
            m_lineno = lineno;
            m_parent = parent;
        }
        public CItem getItem() { return m_item; }
        public void setItem(CItem item) { m_item = item; }
        public int getLineNo() { return m_lineno; }
        public void setLineNo(int line) { m_lineno = line; }
        public Node getParent() { return m_parent; }
        public void setParent(Node p) { m_parent = p; }
        public float getCost() { return m_cost; }
        public void setCost(float cost) { m_cost = cost; }
        public void curryCost(float v) { m_ccost = v; }
        public float getCurriedCost() { return m_ccost; }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            if (m_parent != null) {
                CItem pi = m_parent.getItem();
                if (pi != null) {
                    sb.append(pi.getContent());
                }
            }
            sb.append(" -> ");
            if (m_item != null) {
                sb.append(m_item.getContent());
            }
            sb.append(" : ");
            sb.append(String.valueOf(m_cost));
            sb.append(" [");
            sb.append(String.valueOf(m_lineno));
            sb.append("]");
            return sb.toString();
        }

        private CItem m_item;
        private float m_cost;
        private int m_lineno;
        private Node m_parent;
        private float m_ccost;
    }
}
