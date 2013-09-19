package org.kbsriram.android.example;

// Note: Images and text used here are used from the Nippon Design
// Center web site at http://www.ndc.co.jp and are used just because
// I like Kenya Hara's work and I wanted a nice looking sample.
// All the images are copyright them, and hopefully this use falls
// under the Fair Use category.

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.kbsriram.android.typesetview.TypesetView;

public class JustForFunActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just_for_fun);

        KenyaItem items[] = new KenyaItem[] {
            new KenyaItem
            (R.drawable.kenzo_power, "I designed a very simple bottle for Sake called Hakkin. This bottle holds 720ml of liquor and I had that clearly in my head as one of the factors to be included in its design. When I was asked to design this (Kenzo Power) perfume bottle, I did not know if I could apply this idea of Sake to perfume. I was actually more doubtful at first whether I could rearrange a Sake bottle for perfume. I drafted many sketches for the perfume bottle along with what resembles a Hakkin’s bottle design but I became aware that it was difficult to overcome the Hakkin’s design. And I realized what I was expected to do here.\n\nWhat I had in my mind may have been an image of design for 720ml. By changing the size, the liquor bottle can become that of perfume or vice versa without altering the shape or design. I realized that it is sometimes possible to create a few different bottles by using the same form with different volumes."),
            new KenyaItem
            (R.drawable.muji_landscape1, "Muji refrains from launching messages even in advertising. Instead, the company intends to facilitate communication by publishing ads that are like an empty vessel allowing multiple interpretations. Take a look at this poster as an example. Pictured here is just the earth and a human being. There is nothing, yet everything.\n\nI'm sure that everyone here through his or her work is already aware of the meaning of emptiness. To create is not just to produce an object or phenomenon, coming up with a question is also creation. In fact, a question that has a huge receptive capacity doesn't even need a definitive answer. The very essence of a question is its power to elicit the possibilities for reply, to trigger a variety of thoughts. Questioning is emptiness. The total quantity of thoughts triggered by the questioning is what matters most."),
            new KenyaItem
            (R.drawable.mori_logo, "Roppongi Hills was a project that Mori Building had worked on for many years. It was a symbolic project which aimed for urban complex development that was radically different from conventional building development. The company conceived of an art museum as the facility which would create the core image of this urban complex, and planned to construct it on the top floor of the massive Mori Tower. This art museum was to be a cultural project that would be largely responsible for generating the crucial image of Roppongi Hills.\n\nThe question was what sort of image should it present. In Tokyo, where population is overcrowded in the horizontal plane and sparsely populated in the vertical dimension, there was a strong desire to build toward the sky. This was the image of the ideal city presented by Le Corbusier in his book The Radiant City. Mori Building was aiming to create the vertical garden city imagined by Le Corbusier in Japan. The Mori Building symbol is a visual representation of looking up from the ground toward the sky in a high-rise city composed of a cluster of super high-rise buildings, and is a perfect representation of a vertical garden city.\n\nThe mark which symbolizes looking up at the tops of tall buildings is a good match for the exterior of a high-rise building. As outdoor lighting changes to fluorescent lighting and LEDs, this precisely detailed mark can be provided with a highly accurate light source in order to make a sharp impression to the eye at nighttime also.")

        };

        ListView lv = (ListView) findViewById(R.id.just_for_fun_list);
        lv.setAdapter(new KenyaAdapter(items, getLayoutInflater()));
    }

    private final static class KenyaItem
    {
        private KenyaItem(int resid, String content)
        {
            m_resid = resid;
            m_content = content;
        }
        private final int m_resid;
        private final String m_content;
    }

    private final static class KenyaAdapter
        extends BaseAdapter
    {
        private KenyaAdapter
            (KenyaItem[] items, LayoutInflater li)
        {
            m_items = items;
            m_li = li;
        }

        public int getCount() { return m_items.length+1; }
        public long getItemId(int pos) { return pos; }
        public Object getItem(int pos) { return pos; }
        public boolean hasStableIds() { return true; }
        public int getViewTypeCount() { return 2; }
        public int getItemViewType(int pos) { return pos==0?0:1; }

        public View getView(int pos, View convert, ViewGroup parent)
        {
            if (pos == 0) {
                return getTitleView(convert, parent);
            }
            else {
                return getItemView(pos-1, convert, parent);
            }
        }

        private View getTitleView(View convert, ViewGroup parent)
        {
            if (convert != null) {
                return convert;
            }
            else {
                return m_li.inflate(R.layout.just_for_fun_title, parent, false);
            }
        }

        private View getItemView(int pos, View convert, ViewGroup parent)
        {
            ViewGroup vg;
            if (convert != null) {
                vg = (ViewGroup) convert;
            }
            else {
                vg = (ViewGroup) m_li.inflate
                    (R.layout.just_for_fun_item, parent, false);
                GlyphTypesetView gv = (GlyphTypesetView) vg.findViewById
                (R.id.just_for_fun_typesetview);
                gv.setGlyphMultiplier(6.4f);
            }

            KenyaItem item = m_items[pos];

            ImageView iv = (ImageView) vg.findViewById
                (R.id.just_for_fun_imageview);
            TypesetView tsv = (TypesetView) vg.findViewById
                (R.id.just_for_fun_typesetview);

            iv.setImageResource(item.m_resid);
            tsv.setTypeText(item.m_content);
            return vg;
        }

        private final KenyaItem[] m_items;
        private final LayoutInflater m_li;
    }
}

