package net.danlew.hwlayers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private View mLayout1;
    private View mLoremIpsum1;

    private View mLayout2;
    private View mLoremIpsum2;

    // When checked, this enables using hardware layers on the main layouts
    private CheckBox mHwLayerCheckbox;

    // When checked, the animation gets a bit more complex - not only do the containers animate
    // but one of the views inside each do their own independent animation. This will cause the HW
    // layers to need to be constantly redrawn, which is detrimental to performance.
    //
    // In this case, the animation is simple enough that it still may run *close* to smooth, but if
    // there were a few extra Views or more work then this would chug.
    private CheckBox mComplexAnimationCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHwLayerCheckbox = (CheckBox) findViewById(R.id.hw_layers_checkbox);
        mComplexAnimationCheckbox = (CheckBox) findViewById(R.id.complex_anim_checkbox);

        mLayout1 = configureLayout(R.id.layout_1, R.drawable.bg_layout_1, R.string.layout1_title);
        mLoremIpsum1 = mLayout1.findViewById(R.id.lorem_ipsum);
        mLayout2 = configureLayout(R.id.layout_2, R.drawable.bg_layout_2, R.string.layout2_title);
        mLoremIpsum2 = mLayout2.findViewById(R.id.lorem_ipsum);

        // Start with one of the two hidden
        mLayout2.setAlpha(0);

        findViewById(R.id.animate_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                animate();
            }
        });
    }

    private View configureLayout(@IdRes int id, @DrawableRes int background, @StringRes int title) {
        View layout = findViewById(id);
        layout.setBackgroundResource(background);

        TextView titleView = (TextView) layout.findViewById(R.id.title);
        titleView.setText(title);

        return layout;
    }

    private void animate() {
        float layout1Alpha = mLayout1.getAlpha();
        if (layout1Alpha != 0 && layout1Alpha != 1) {
            // Mid-animation; don't animate
            return;
        }

        final View layoutIn = layout1Alpha == 0 ? mLayout1 : mLayout2;
        final View layoutOut = layoutIn == mLayout1 ? mLayout2 : mLayout1;

        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        float scale = .5f;

        layoutIn.setTranslationX(widthPixels / 2);
        layoutIn.setScaleX(scale);
        layoutIn.setScaleY(scale);

        layoutIn.animate()
            .alpha(1)
            .translationX(0)
            .scaleX(1)
            .scaleY(1)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mHwLayerCheckbox.isChecked()) {
                        layoutIn.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mHwLayerCheckbox.isChecked()) {
                        layoutIn.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                }
            })
            .start();

        layoutOut.animate()
            .alpha(0)
            .translationX(-widthPixels / 2)
            .scaleX(scale)
            .scaleY(scale)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mHwLayerCheckbox.isChecked()) {
                        layoutOut.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mHwLayerCheckbox.isChecked()) {
                        layoutOut.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                }
            })
            .start();

        if (mComplexAnimationCheckbox.isChecked()) {
            final View ipsumIn = layoutIn == mLayout1 ? mLoremIpsum1 : mLoremIpsum2;
            final View ipsumOut = layoutOut == mLayout2 ? mLoremIpsum2 : mLoremIpsum1;

            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            int target = heightPixels / 2;

            ipsumIn.setTranslationY(target);
            ipsumIn.setAlpha(0);

            ipsumIn.animate()
                .translationY(0)
                .alpha(1)
                .start();

            ipsumOut.animate()
                .translationY(target)
                .alpha(0)
                .start();
        }
        else {
            mLoremIpsum1.setTranslationY(0);
            mLoremIpsum1.setAlpha(1);
            mLoremIpsum2.setTranslationY(0);
            mLoremIpsum2.setAlpha(1);
        }
    }

}
