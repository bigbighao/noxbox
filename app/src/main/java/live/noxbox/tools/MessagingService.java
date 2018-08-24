package live.noxbox.tools;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import live.noxbox.BuildConfig;
import live.noxbox.R;
import live.noxbox.model.Event;
import live.noxbox.model.EventType;
import live.noxbox.model.Notice;
import live.noxbox.model.Push;
import live.noxbox.model.Request;
import live.noxbox.pages.ChatActivity;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static live.noxbox.Configuration.CURRENCY;
import static live.noxbox.model.EventType.balance;
import static live.noxbox.model.EventType.request;
import static live.noxbox.model.EventType.story;

/**
 * Created by nicolay.lipnevich on 4/30/2018.
 */

public class MessagingService extends FirebaseMessagingService {

    private final String channelId = "noxbox_channel";
    private Context context;

    public MessagingService() {
    }

    public MessagingService(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        context = getApplicationContext();

        Notice notice = Notice.create(remoteMessage.getData());

        if (notice.getIgnore()) return;

        showPushNotification(notice);
    }

    public void showPushNotification(Notice notice) {
        // if(inForeground()) return;

        createChannel();

        cancelOtherNotifications(notice.getType());

        if (notice.getProgress() != null && notice.getProgress() == 100) {
            getNotificationService().cancel(getId(notice.getType()));
        } else {
            notify(notice);
        }
    }

    private boolean inForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND
                || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public NotificationManager getNotificationService() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private int getId(EventType type) {
        switch (type) {
            case request:
            case move:
            case accept:
            case performerCancel:
            case payerCancel:
            case qr:
            case complete:
                return 1;
            case balance:
                return 2;
            case story:
                return 9;
            case uploadingProgress:
                return 10;
            default:
                return 0;
        }
    }

    private void cancelOtherNotifications(EventType type) {
        HashSet<EventType> cancelOther = new HashSet<>(Arrays.asList(
                EventType.request,
                EventType.accept,
                EventType.performerCancel,
                EventType.payerCancel,
                EventType.complete));

        if (cancelOther.contains(type)) {
            getNotificationService().cancelAll();
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationService()
                    .createNotificationChannel(new NotificationChannel(channelId,
                            "Noxbox channel",
                            NotificationManager.IMPORTANCE_DEFAULT));
        }
    }

    private void notify(final Notice notice) {

        final Builder builder = new Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(null)
                .setSound(null)
                .setContentTitle(getTitle(notice))
                .setContentText(getContent(notice))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getContent(notice)))
                .setAutoCancel(!BuildConfig.DEBUG)
                .setOnlyAlertOnce(isAlertOnce(notice.getType()))
                .setContentIntent(getIntent(notice));

        switch (notice.getType()) {
            case uploadingProgress: {
                break;
            }
            default: builder.setVibrate(new long[]{100, 500, 200, 100, 100}).setSound(getSound(notice.getType()));
        }

        setProgress(builder, notice);

        Task<Bitmap> iconLoading = new Task<Bitmap>() {
            @Override
            public void execute(Bitmap icon) {
                builder.setLargeIcon(icon);
                getNotificationService().notify(getId(notice.getType()), builder.build());
            }
        };

        if (notice.getLocal() != null && notice.getLocal()) {
            loadIconAsync(notice, iconLoading);
        } else {
            iconLoading.execute(loadIcon(notice));
        }
    }

    private void setProgress(Builder builder, Notice notice) {
        if (notice.getProgress() != null) {
            builder.setProgress(100, notice.getProgress(), false);
        }
    }

    private PendingIntent getIntent(Notice notice) {
        if (notice.getType() == story) {
            return PendingIntent.getActivity(context, ChatActivity.CODE,
                    new Intent(context, ChatActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return PendingIntent.getActivity(context, 0, context.getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private String getContent(Notice notice) {
        switch (notice.getType()) {
            case request:
                return format(R.string.requestPushBody, notice.getName(), notice.getEstimation());
            case move:
            case accept:
                return format(R.string.acceptPushBody, notice.getName(), notice.getEstimation());
            case performerCancel:
                return format(R.string.performerCancelPushBody);
            case payerCancel:
                return format(R.string.payerCancelPushBody);
            case qr:
                return format(R.string.qrPushBody, notice.getName());
            case story:
                return notice.getMessage();
            case complete:
                return format(R.string.completePushBody, notice.getPrice(), CURRENCY);
            case balance:
                return format(R.string.balancePushBody, notice.getBalance(), CURRENCY);
            case uploadingProgress: {

                if (notice.getTime() == null) {
                    return format(R.string.uploadingStarted);
                }
                return format(R.string.uploadingProgressBody, notice.getTime());
            }

            default:
                return "Glad to serve you!";
        }
    }


    private String format(int resource, Object... args) {
        return String.format(context.getResources().getString(resource), args);
    }

    private CharSequence getTitle(Notice notice) {
        switch (notice.getType()) {
            case request:
                return format(R.string.requestPushTitle);
            case move:
            case accept:
                return format(R.string.acceptPushTitle);
            case performerCancel:
                return format(R.string.performerCancelPushTitle);
            case payerCancel:
                return format(R.string.payerCancelPushTitle);
            case qr:
                return format(R.string.qrPushTitle);
            case story:
                return notice.getName();
            case complete:
                return format(R.string.completePushTitle);
            case uploadingProgress:
                return format(R.string.uploadingProgressTitle, notice.getProgress());
            case balance:
                return format(R.string.balancePushTitle);
            default:
                return format(R.string.noxbox);
        }
    }

    private boolean isAlertOnce(EventType type) {
        return type != EventType.balance && type != story;
    }

    private Bitmap loadIcon(Notice notice) {
        try {
            if (notice.getType() == balance) {
                return Glide.with(context).asBitmap().load(R.drawable.noxbox)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .submit().get();
            }
            if (notice.getIcon() != null) {
                return Glide.with(context).asBitmap().load(notice.getIcon())
                        .apply(new RequestOptions().transform(new RoundedCorners(49))
                                .override(128, 128)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .error(R.drawable.profile_picture_blank)).submit().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
            ;
        }
        return null;
    }

    private void loadIconAsync(Notice notice, final Task<Bitmap> task) {
        if (notice.getIcon() != null) {
            Glide.with(context).asBitmap()
                    .load(notice.getIcon())
                    .apply(new RequestOptions().transform(new RoundedCorners(49))
                            .override(128, 128)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .error(R.drawable.profile_picture_blank))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<?
                                super Bitmap> transition) {
                            task.execute(resource);
                        }
                    });
        }
    }


    private Uri getSound(EventType type) {
        int sound = R.raw.push;
        if (type == request) {
            sound = R.raw.requested;
        }

        return Uri.parse("android.resource://" + context.getPackageName() + "/raw/"
                + context.getResources().getResourceEntryName(sound));
    }

    public static Push generatePush(Event event, String recipientId) {
        if (event.getType() == EventType.story) {
            return new Push()
                    .setRecipientId(recipientId)
                    .setType(event.getType().name())
                    .setMessage(event.getMessage())
                    .setName(event.getSender().getName())
                    .setIcon(event.getSender().getPhoto());
        }
        return null;
    }

    public static Push generatePush(Request request) {
        Push push = new Push().setType(request.getType().name());
        switch (request.getType()) {
            case request:
            case payerCancel:
                return push.setRecipientId(request.getNoxbox().getOwner().getId())
                        .setName(request.getNoxbox().getParty().getName())
                        .setIcon(request.getNoxbox().getParty().getPhoto())
                        .setEstimation(request.getNoxbox().getEstimationTime());
            case accept:
            case performerCancel:
            case complete:
            case qr:
                return push.setRecipientId(request.getNoxbox().getParty().getId())
                        .setName(request.getNoxbox().getOwner().getName())
                        .setIcon(request.getNoxbox().getOwner().getPhoto())
                        .setPrice(request.getNoxbox().getPrice())
                        .setEstimation(request.getNoxbox().getEstimationTime());
            case balance:
                return push.setRecipientId(request.getId());
        }
        return null;
    }

}