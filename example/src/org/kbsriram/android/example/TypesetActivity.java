package org.kbsriram.android.example;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import org.kbsriram.android.typesetview.TypesetView;

public class TypesetActivity
    extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView txt = (TextView) findViewById(R.id.controlview);
        txt.setPaintFlags
            (txt.getPaint().getFlags() |
             Paint.SUBPIXEL_TEXT_FLAG  |
             Paint.DEV_KERN_TEXT_FLAG);
        txt.setText(SAMPLE);

        TypesetView tv = (TypesetView) findViewById(R.id.typesetview);
        tv.setTypeText(SAMPLE);
    }

    private final static String SAMPLE =
        "Amendment III\nNo soldier shall, in time of peace be quartered in any house, without the consent of the Owner, nor in time of war, but in a manner to be prescribed by law.\n\nAmendment IV\nThe right of the people to be secure in their persons, houses, papers, and effects, against unreasonable searches and seizures, shall not be violated, and no Warrants shall issue, but upon probable cause, supported by Oath or affirmation, and particularly describing the place to be searched, and the persons or things to be seized. The right of the people to be secure in their persons, houses, papers, and effects, against unreasonable searches and seizures, shall not be violated, and no Warrants shall issue, but upon probable cause, supported by Oath or affirmation, and particularly describing the place to be searched, and the persons or things to be seized.\n\nAmendment V\nNo person shall be held to answer for a capital, or otherwise infamous crime, unless on a presentment or indictment of a Grand Jury, except in cases arising in the land or naval forces, or in the Militia, when in actual service in time of War or public danger; nor shall any person be subject for the same offence to be twice put in jeopardy of life or limb; nor shall be compelled in any criminal case to be a witness against himself, nor be deprived of life, liberty, or property, without due process of law; nor shall private property be taken for public use, without just compensation.";

    //private final static String SAMPLE =
    //"Amendment III\nNo soldier shall, in time of peace be quartered in any house, without the consent of the Owner, nor in time of war, but in a manner to be prescribed by law.";
}
