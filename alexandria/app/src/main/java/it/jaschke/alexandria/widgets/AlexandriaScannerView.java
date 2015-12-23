package it.jaschke.alexandria.widgets;

import android.content.Context;
import android.util.AttributeSet;

import it.jaschke.alexandria.R;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Paulina on 2015-10-28.
 */
public class AlexandriaScannerView extends ZXingScannerView {

    private ViewFinderView finderView;

    public AlexandriaScannerView(Context context) {
        super(context);
        initAppearance();
    }

    public AlexandriaScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initAppearance();
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        finderView = new ViewFinderView(context);
        return finderView;
    }

    private void initAppearance() {
        finderView.setMaskColor(getResources().getColor(R.color.scanner_mask));
        finderView.setBorderColor(getResources().getColor(R.color.scanner_border));
        finderView.setLaserColor(getResources().getColor(R.color.scanner_laser));
        finderView.setBorderStrokeWidth(
                getResources().getDimensionPixelSize(R.dimen.scanner_border_width));
        finderView.setBorderLineLength(
                getResources().getDimensionPixelSize(R.dimen.scanner_border_length));
    }
}
