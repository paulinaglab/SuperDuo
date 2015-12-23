package it.jaschke.alexandria.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Paulina on 2015-11-05.
 */
public class FabSubmenuAnimation {

    private static final int FAB_ROTATION_DURATION = 100;
    private static final int FAB_EXPANDED_DEGREES = 135;
    private static final int FAB_COLLAPSED_DEGREES = 0;

    private static final int SUB_FAB_MOVING_DURATION = 200;


    public static Animator getExpandingAnimator(
            FloatingActionButton mainFab, FloatingActionButton subFab1, FloatingActionButton subFab2,
            View uiDim) {

        Animator fabRotation = getMainFabRotateAnimator(mainFab, FAB_EXPANDED_DEGREES);
        Animator dimAlphaAnimation = getDimAlphaAnimator(uiDim, 1);
        Animator subFab1Moving = getSubFabMovingAnimator(
                subFab1, getSubUnderMainFabTranslationY(mainFab, subFab1), 0);
        Animator subFab2Moving = getSubFabMovingAnimator(
                subFab2, getSubUnderMainFabTranslationY(mainFab, subFab2), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fabRotation, dimAlphaAnimation, subFab1Moving, subFab2Moving);
        return animatorSet;
    }


    public static Animator getCollapsingAnimator(
            FloatingActionButton mainFab, FloatingActionButton subFab1, FloatingActionButton subFab2,
            View uiDim) {

        Animator fabRotation = getMainFabRotateAnimator(mainFab, FAB_COLLAPSED_DEGREES);
        Animator dimAlphaAnimation = getDimAlphaAnimator(uiDim, 0);
        Animator subFab1Moving = getSubFabMovingAnimator(
                subFab1, 0, getSubUnderMainFabTranslationY(mainFab, subFab1));
        Animator subFab2Moving = getSubFabMovingAnimator(
                subFab2, 0, getSubUnderMainFabTranslationY(mainFab, subFab2));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fabRotation, dimAlphaAnimation, subFab1Moving, subFab2Moving);
        return animatorSet;
    }


    private static Animator getMainFabRotateAnimator(FloatingActionButton mainFab, float endDegrees) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mainFab, View.ROTATION, endDegrees);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(FAB_ROTATION_DURATION);

        return animator;
    }

    private static Animator getDimAlphaAnimator(View uiDim, float alpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(uiDim, View.ALPHA, alpha);
        animator.setInterpolator(new DecelerateInterpolator());

        return animator;
    }

    private static Animator getSubFabMovingAnimator(
            FloatingActionButton subFab, float startTranslationY, float endTranslationY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                subFab, View.TRANSLATION_Y, startTranslationY, endTranslationY);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(SUB_FAB_MOVING_DURATION);

        return animator;
    }

    private static float getSubUnderMainFabTranslationY(
            FloatingActionButton mainFab, FloatingActionButton subFab) {
        float mainFabCenterY = mainFab.getY() + mainFab.getHeight() / 2f;
        float subFabCenterY = subFab.getY() + subFab.getHeight() / 2f;
        return mainFabCenterY - (subFabCenterY - subFab.getTranslationY());
    }
}
