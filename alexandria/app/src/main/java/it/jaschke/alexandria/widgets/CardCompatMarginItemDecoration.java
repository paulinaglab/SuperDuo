package it.jaschke.alexandria.widgets;

import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Paulina Glab
 * @version 0.01
 */
public class CardCompatMarginItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public CardCompatMarginItemDecoration(int spacePixelSize) {
        this.space = spacePixelSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        CardView cardView = (CardView) view;
        int sideCompatPadding = (int)
                (cardView.getMaxCardElevation() + (1 - Math.cos(45)) * cardView.getRadius());
        int verticalCompatPadding = (int)
                (cardView.getMaxCardElevation() * 1.5 + (1 - Math.cos(45)) * cardView.getRadius());

        outRect.left = space - sideCompatPadding;
        outRect.right = space - sideCompatPadding;
        outRect.top = space - verticalCompatPadding;
        outRect.bottom = space - verticalCompatPadding;
    }

}