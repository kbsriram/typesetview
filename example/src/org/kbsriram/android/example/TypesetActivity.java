package org.kbsriram.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TypesetActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        m_demos = new DemoItem[] {
            new DemoItem
            (MulticolumnDemoActivity.class, "Multicolumn text"),
            new DemoItem
            (GlueDemoActivity.class, "Inter word spacings"),
            new DemoItem
            (MarginDemo2Activity.class, "Flow text - image"),
            new DemoItem
            (MarginDemoActivity.class, "Flow text - glyph"),
            new DemoItem
            (CompareDemoActivity.class, "Compare linebreaks"),
        };

        ListView lv = (ListView) findViewById(R.id.main_list);
        lv.setAdapter
            (new ArrayAdapter<DemoItem>
             (this, R.layout.main_item, m_demos));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick
                    (AdapterView p, View v, int pos, long id) {
                    m_demos[pos].launchActivity(TypesetActivity.this);
                }
            });
    }

    private DemoItem m_demos[] = null;

    private final static class DemoItem
    {
        private DemoItem(Class cls, String desc)
        {
            m_cls = cls;
            m_desc = desc;
        }
        private void launchActivity(Context ctx)
        { ctx.startActivity(new Intent(ctx, m_cls)); }

        public String toString()
        { return m_desc; }
        private final Class m_cls;
        private final String m_desc;
    }
}

