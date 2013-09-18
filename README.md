# TypesetView

TypesetView is an Android Library, providing a [View](https://developer.android.com/reference/android/view/View.html) that's great for displaying long blocks ofrag-right static body text. It adds typographic niceties from more sophisticated text layout engines, allowing refined control over your content.

#### Multicolumn text
![Multi column example](https://kbsriram.github.com/typesetview/img/multicolumn1.png)
You can either directly set the number of columns, or just specify a minimum column width. If you set a minimum column width, TypesetView will automatically add columns based on the available width.

#### Flexible margins
You can programatically control the margins for each line in the View. For instance, you can flow text around arbitrary glyphs, as in this example shows.
![Flowing text around glyph](https://kbsriram.github.com/typesetview/img/flow1.png)

#### Advanced linebreaks
Unlike TextView, TypesetView looks ahead and breaks lines when possible to minimize long gaps in rag-right text. Here is a side-by-side comparison of TextView on the left, and TypesetView on the right.
![Line breaking comparison](https://kbsriram.github.com/typesetview/img/line_breaking1.png)
TypesetView has moved the word "of" from the first line down to the second line, which leaves a slightly bigger gap for the first line. However, this lets it avoid a much larger gap in the third line. Software typophiles - it minimizes the sum of the squares of the end-of-line gaps in text that's set rag-right. It uses a variation of the Knuth-Plass algorithm (used in TeX.) It doesn't hyphenate words, and the "badness" of a line is simply the square of the right-side gap. The last line is ignored in the calculations.

#### Lastly...

Two small, but useful typographic specifications.
First, you can choose to allow inter-word spaces to expand in controlled amounts, if you want to further minimize gaps at the ends of lines.
Second, you can set the [typographic leading](https://en.wikipedia.org/wiki/Leading) explicitly, rather than TextView's unintuitive spacing "multipliers" and "adders". If you want to set text at 16/18dp - you just set typeSize and typeLeading to 16dp and 18dp respectively.

## Using TypesetView

Just add `typesetview-library` as a library project. To add a TypesetView within your layout, you might do something like

<pre>
    <ScrollView
      xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:typeset="http://schemas.android.com/apk/res-auto"
      android:layout_width="match_parent"
      android:layout_height="wrap_content">

      <org.kbsriram.android.typesetview.TypesetView
          android:layout_width="match_parent" 
          android:layout_height="wrap_content"
          typeset:typeColor="#ff303030"
          typeset:typeSize="12sp"/>
    </ScrollView>
</pre>

Notice that you should use `http://schemas.android.com/apk/res-auto` to refer to the attributes that are specific to TypesetView.
