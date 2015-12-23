package it.jaschke.alexandria.widgets;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.GridLayout;

/**
 * Created by Paulina on 2015-10-30.
 */
public class OptionalColorsLayout extends GridLayout
        implements View.OnClickListener {

    private static final int DEFAULT_SELECTED_COLOR_INDEX = 0;

    private int selectedColorIndex = -1;
    private OnColorSelectedListener onColorSelectedListener;

    public OptionalColorsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionalColorsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initChildrenListener();
    }

    private void initChildrenListener() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnClickListener(this);
        }
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    private void notifySelectedColorChanged() {
        if (onColorSelectedListener != null)
            onColorSelectedListener.onColorSelected(
                    ((ColorToggleButton) getChildAt(selectedColorIndex)).getCircleColor());

    }

    public void setSelectedColor(@ColorInt int color) {
        for (int i = 0; i < getChildCount(); i++) {
            if (((ColorToggleButton) getChildAt(i)).getCircleColor() == color) {
                setSelectedColorIndex(i);
                return;
            }
        }
    }

    public void setSelectedColorIndex(int newSelectedColorIndex) {
        if (selectedColorIndex != -1) {
            Checkable oldSelected = (Checkable) getChildAt(selectedColorIndex);
            oldSelected.setChecked(false);
        }
        this.selectedColorIndex = newSelectedColorIndex;
        Checkable clickedChild = (Checkable) getChildAt(selectedColorIndex);
        clickedChild.setChecked(true);
        notifySelectedColorChanged();
    }

    public void selectDefault() {
        setSelectedColorIndex(DEFAULT_SELECTED_COLOR_INDEX);
    }

    @Override
    public void onClick(View v) {
        if (indexOfChild(v) != selectedColorIndex) {
            setSelectedColorIndex(indexOfChild(v));
        }
    }

    public interface OnColorSelectedListener {

        void onColorSelected(@ColorInt int color);

    }

}
