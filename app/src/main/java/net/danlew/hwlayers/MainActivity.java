package net.danlew.hwlayers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private View mLayout1;
    private View mLayout2;

    private CheckBox mHwLayerCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHwLayerCheckbox = (CheckBox) findViewById(R.id.hw_layers_checkbox);

        mLayout1 = configureLayout(R.id.layout_1, R.drawable.bg_layout_1, R.string.layout1_title, R.drawable.cats_1);
        mLayout2 = configureLayout(R.id.layout_2, R.drawable.bg_layout_2, R.string.layout2_title, R.drawable.cats_2);

        // Start with one of the two hidden
        mLayout2.setAlpha(0);

        findViewById(R.id.animate_button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                animate();
            }
        });
    }

    private View configureLayout(
        @IdRes int id, @DrawableRes int background, @StringRes int title,
        @DrawableRes int cat) {
        View layout = findViewById(id);
        layout.setBackgroundResource(background);

        TextView titleView = (TextView) layout.findViewById(R.id.title);
        titleView.setText(title);

        ImageView imageView = (ImageView) layout.findViewById(R.id.cat_image);
        imageView.setImageResource(cat);

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

        int windowPixels = getResources().getDisplayMetrics().widthPixels;
        float scale = .5f;

        layoutIn.setTranslationX(windowPixels / 2);
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
            .translationX(-windowPixels / 2)
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
    }

}
