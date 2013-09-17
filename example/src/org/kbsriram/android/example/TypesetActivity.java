package org.kbsriram.android.example;

import android.app.Activity;
import android.os.Bundle;

public class TypesetActivity
    extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MarginTypesetView ts = (MarginTypesetView)
            findViewById(R.id.typesetview);
        ts.setTypeText(SAMPLE);
    }

    private final static String SAMPLE =
        "ne fine evening a young princess put on her bonnet and clogs, and went out to take a walk by herself in a wood; and when she came to a cool spring of water, that rose in the midst of it, she sat herself down to rest a while. Now she had a golden ball in her hand, which was her favourite plaything; and she was always tossing it up into the air, and catching it again as it fell. After a time she threw it up so high that she missed catching it as it fell; and the ball bounded away, and rolled along upon the ground, till at last it fell down into the spring. The princess looked into the spring after her ball, but it was very deep, so deep that she could not see the bottom of it. Then she began to bewail her loss, and said, 'Alas! if I could only get my ball again, I would give all my fine clothes and jewels, and everything that I have in the world.'\n\nWhilst she was speaking, a frog put its head out of the water, and said, 'Princess, why do you weep so bitterly?' 'Alas!' said she, 'what can you do for me, you nasty frog? My golden ball has fallen into the spring.' The frog said, 'I want not your pearls, and jewels, and fine clothes; but if you will love me, and let me live with you and eat from off your golden plate, and sleep upon your bed, I will bring you your ball again.' 'What nonsense,' thought the princess, 'this silly frog is talking! He can never even get out of the spring to visit me, though he may be able to get my ball for me, and therefore I will tell him he shall have what he asks.' So she said to the frog, 'Well, if you will bring me my ball, I will do all you ask.' Then the frog put his head down, and dived deep under the water; and after a little while he came up again, with the ball in his mouth, and threw it on the edge of the spring. As soon as the young princess saw her ball, she ran to pick it up; and she was so overjoyed to have it in her hand again, that she never thought of the frog, but ran home with it as fast as she could. The frog called after her, 'Stay, princess, and take me with you as you said,' But she did not stop to hear a word.";
}
