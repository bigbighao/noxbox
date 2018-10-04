package live.noxbox.pages;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import live.noxbox.R;
import live.noxbox.model.Notification;
import live.noxbox.model.NotificationType;
import live.noxbox.model.Profile;
import live.noxbox.state.ProfileStorage;
import live.noxbox.state.State;
import live.noxbox.tools.MapController;
import live.noxbox.tools.MarkerCreator;
import live.noxbox.tools.MessagingService;
import live.noxbox.tools.NavigatorManager;

import static live.noxbox.Configuration.REQUESTING_AND_ACCEPTING_TIMEOUT_IN_SECONDS;
import static live.noxbox.state.ProfileStorage.fireProfile;

public class Accepting implements State {

    private GoogleMap googleMap;
    private Activity activity;
    private ObjectAnimator anim;
    private AnimationDrawable animationDrawable;
    private CountDownTimer countDownTimer;
    private LinearLayout acceptingView;

    public Accepting(GoogleMap googleMap, Activity activity) {
        this.googleMap = googleMap;
        this.activity = activity;
    }

    @Override
    public void draw(final Profile profile) {
        MarkerCreator.createCustomMarker(profile.getCurrent(), googleMap);
        acceptingView = activity.findViewById(R.id.container);
        View child = activity.getLayoutInflater().inflate(R.layout.state_accepting, null);
        acceptingView.addView(child);

        activity.findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigatorManager.openNavigator(activity, profile);
            }
        });

        ((TextView) acceptingView.findViewById(R.id.blinkingInfo)).setText(R.string.acceptingConfirmation);
        acceptingView.findViewById(R.id.circular_progress_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.getCurrent().setTimeAccepted(null);
                profile.getCurrent().setTimeRequested(null);
                clear();
                fireProfile();
            }
        });

        acceptingView.findViewById(R.id.joinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.getCurrent().setTimeAccepted(System.currentTimeMillis());
                ProfileStorage.fireProfile();
            }
        });

        anim = ObjectAnimator.ofInt(activity.findViewById(R.id.circular_progress_bar), "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        animationDrawable = (AnimationDrawable) activity.findViewById(R.id.blinkingInfoLayout).getBackground();
        animationDrawable.setEnterFadeDuration(600);
        animationDrawable.setExitFadeDuration(1200);
        animationDrawable.start();


        final MessagingService messagingService = new MessagingService(activity.getApplicationContext());
        final Notification notification = new Notification()
                .setType(NotificationType.accepting)
                .setTime(String.valueOf(REQUESTING_AND_ACCEPTING_TIMEOUT_IN_SECONDS));
        messagingService.showPushNotification(notification);
        long timeCountInMilliSeconds = 60000 - (System.currentTimeMillis() - profile.getCurrent().getTimeRequested());
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) acceptingView.findViewById(R.id.countdownTime)).setText(String.valueOf(millisUntilFinished / 1000));
                notification.getType().updateNotification(activity.getApplicationContext(),
                        notification.setType(NotificationType.accepting).setTime(String.valueOf(millisUntilFinished / 1000)),
                        MessagingService.builder);
            }

            @Override
            public void onFinish() {
                if (profile.getCurrent().getTimeAccepted() == null) {
                    profile.getCurrent().setTimeAccepted(null);
                    profile.getCurrent().setTimeRequested(null);
                    ProfileStorage.fireProfile();
                }
            }

        }.start();

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        MapController.buildMapMarkerListener(googleMap, profile, activity);
        MapController.buildMapPosition(googleMap, profile, activity.getApplicationContext());
    }

    @Override
    public void clear() {
        googleMap.clear();
        activity.findViewById(R.id.navigation).setVisibility(View.GONE);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        if (anim != null && animationDrawable != null) {
            anim.cancel();
            animationDrawable.stop();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();

        }
        acceptingView.removeAllViews();
    }
}
