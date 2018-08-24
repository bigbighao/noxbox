package live.noxbox.pages;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import live.noxbox.R;
import live.noxbox.model.Profile;
import live.noxbox.state.State;
import live.noxbox.tools.PathFinder;

import static live.noxbox.state.ProfileStorage.fireProfile;

public class Requesting implements State {

    private GoogleMap googleMap;
    private Activity activity;
    private ObjectAnimator anim;
    private AnimationDrawable animationDrawable;

    public Requesting(GoogleMap googleMap, Activity activity) {
        this.googleMap = googleMap;
        this.activity = activity;
    }

    @Override
    public void draw(final Profile profile) {
        ((TextView)activity.findViewById(R.id.blinkingInfo)).setText(R.string.connectionWithInitiator);
        activity.findViewById(R.id.blinkingInfoLayout).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.timeLayout).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.circular_progress_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.getCurrent().setTimeRequested(null);
                clear();
                fireProfile();
            }
        });
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        activity.findViewById(R.id.locationButton).setVisibility(View.GONE);
        activity.findViewById(R.id.menu).setVisibility(View.GONE);
        activity.findViewById(R.id.exchange_rate).setVisibility(View.GONE);

        anim = ObjectAnimator.ofInt(activity.findViewById(R.id.circular_progress_bar), "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        animationDrawable = (AnimationDrawable) activity.findViewById(R.id.blinkingInfoLayout).getBackground();
        animationDrawable.setEnterFadeDuration(600);
        animationDrawable.setExitFadeDuration(1200);
        animationDrawable.start();

        profile.getViewed().setParty(profile);

        PathFinder.createRequestPoints(profile.getCurrent(), googleMap, activity);
    }


    @Override
    public void clear() {
        googleMap.clear();
        activity.findViewById(R.id.travelTime).setVisibility(View.GONE);
        activity.findViewById(R.id.blinkingInfoLayout).setVisibility(View.GONE);
        activity.findViewById(R.id.timeLayout).setVisibility(View.GONE);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        activity.findViewById(R.id.locationButton).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.menu).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.exchange_rate).setVisibility(View.VISIBLE);
        if (anim != null && animationDrawable != null) {
            anim.cancel();
            animationDrawable.stop();
        }
    }
}