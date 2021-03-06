package live.noxbox.menu.about.tutorial;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;

import live.noxbox.MapActivity;
import live.noxbox.R;
import live.noxbox.activities.BaseActivity;
import live.noxbox.analitics.BusinessActivity;
import live.noxbox.tools.GyroscopeObserver;
import live.noxbox.tools.PanoramaImageView;
import live.noxbox.tools.Router;

import static live.noxbox.Constants.FIRST_RUN_KEY;
import static live.noxbox.analitics.BusinessEvent.tutorialDislikes;
import static live.noxbox.analitics.BusinessEvent.tutorialLikes;
import static live.noxbox.tools.DisplayMetricsConservations.dpToPx;

public class TutorialActivity extends BaseActivity {
    private GyroscopeObserver gyroscopeObserver;
    private ViewPager viewPager;
    private ImageView dots;
    private Button skip;
    private Button next;

    private String tutorialKey;

    private View.OnClickListener nextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    };

    private View.OnClickListener gotItOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                BusinessActivity.businessEvent(tutorialLikes);
                if (tutorialKey != null && tutorialKey.equals(FIRST_RUN_KEY)) {
                    Router.startActivity(TutorialActivity.this, MapActivity.class);
                    finish();
                    return;
                }
                Router.finishActivity(TutorialActivity.this);
            }
        }
    };
    private View.OnClickListener skipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                BusinessActivity.businessEvent(tutorialDislikes);
                if (tutorialKey != null && tutorialKey.equals(FIRST_RUN_KEY)) {
                    Router.startActivity(TutorialActivity.this, MapActivity.class);
                    finish();
                    return;
                }

                Router.finishActivity(TutorialActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialKey = getIntent().getStringExtra(FIRST_RUN_KEY);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tutorial);

        viewPager = findViewById(R.id.viewpager);
        dots = findViewById(R.id.dots);
        dots.setImageResource(R.drawable.tutorial_intro_one);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dots.setPadding(0, dpToPx(32),0, 0);
        }

        skip = findViewById(R.id.skip);
        skip.setOnClickListener(skipOnClickListener);
        next = findViewById(R.id.next);
        next.setOnClickListener(nextOnClickListener);

        final PanoramaImageView panoramaImageView = findViewById(R.id.tutorialBackground);
        gyroscopeObserver = new GyroscopeObserver();
        // Set the maximum radian the device should rotate to show image's bounds.
        // It should be set between 0 and π/2.
        // The default value is π/9.
        gyroscopeObserver.setMaxRotateRadian(Math.PI / 2);
        gyroscopeObserver.addPanoramaImageView(panoramaImageView);

        viewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        dots.setImageResource(R.drawable.tutorial_intro_one);
                        next.setText(R.string.next);
                        next.setOnClickListener(nextOnClickListener);
                        break;
                    case 1:
                        dots.setImageResource(R.drawable.tutorial_intro_two);
                        next.setText(R.string.next);
                        next.setOnClickListener(nextOnClickListener);
                        break;
                    case 2:
                        dots.setImageResource(R.drawable.tutorial_intro_three);
                        next.setText(R.string.next);
                        next.setOnClickListener(nextOnClickListener);
                        break;
                    case 3:
                        dots.setImageResource(R.drawable.tutorial_intro_four);
                        next.setText(R.string.gotIt);
                        next.setOnClickListener(gotItOnClickListener);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        Glide.with(this)
                .asDrawable()
                .load(R.drawable.tutorial_background_with_comets)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(new ImageViewTarget<Drawable>(panoramaImageView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        panoramaImageView.setImageDrawable(resource);
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();

        gyroscopeObserver.register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroscopeObserver.unregister();
    }
}
