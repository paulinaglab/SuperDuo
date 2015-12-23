package barqsoft.footballscores.ui;

import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Paulina on 2015-11-16.
 */
public class CardCompatMarginItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public CardCompatMarginItemDecoration(int spacePixelSize) {
        this.space = spacePixelSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view instanceof CardView) {
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

}
