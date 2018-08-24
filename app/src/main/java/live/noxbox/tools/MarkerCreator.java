package live.noxbox.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import live.noxbox.R;
import live.noxbox.model.Noxbox;
import live.noxbox.model.TravelMode;

public class MarkerCreator {
    public static Marker createPositionMarker(TravelMode travelMode, LatLng position, GoogleMap googleMap) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(travelMode.getImage()))
                .anchor(0.5f, 1f));
        return marker;
    }
    public static Marker createCustomMarker(Noxbox noxbox, GoogleMap googleMap, Activity activity, TravelMode travelMode) {
        Marker marker = null;
        MarkerOptions markerOptions = null;
        switch (noxbox.getRole()) {
            case supply:
                markerOptions = new MarkerOptions()
                        .position(noxbox.getPosition().toLatLng())
                        .icon(BitmapDescriptorFactory.fromBitmap(drawImage(
                                getIconBitmap(activity, noxbox.getType().getImage()),
                                getRatingColor(noxbox.getOwner().ratingToPercentage(), activity, noxbox),
                                activity.getResources().getColor(R.color.icon_background),
                                getIconTravelModeBitmap(activity, travelMode.getImage())
                        )))
                        .anchor(0.5f, 1f);

                marker = googleMap.addMarker(markerOptions);

                marker.setTag(noxbox);
            case demand:
                markerOptions = new MarkerOptions()
                        .position(noxbox.getPosition().toLatLng())
                        .icon(BitmapDescriptorFactory.fromBitmap(drawImage(
                                getIconBitmap(activity, noxbox.getType().getImage()),
                                getRatingColor(noxbox.getOwner().ratingToPercentage(), activity, noxbox),
                                activity.getResources().getColor(R.color.icon_background),
                                getIconTravelModeBitmap(activity, travelMode.getImage())
                        )))
                        .anchor(0.5f, 1f);

                marker = googleMap.addMarker(markerOptions);

                marker.setTag(noxbox);
        }
        return marker;
    }

    public static MarkerOptions createCustomMarker(Noxbox noxbox, Activity activity) {
        MarkerOptions markerOptions = null;
        switch (noxbox.getRole()) {
            case supply:
                markerOptions = new MarkerOptions()
                        .position(noxbox.getPosition().toLatLng())
                        .icon(BitmapDescriptorFactory.fromBitmap(drawImage(
                                getIconBitmap(activity, noxbox.getType().getImage()),
                                getRatingColor(noxbox.getOwner().ratingToPercentage(), activity, noxbox),
                                activity.getResources().getColor(R.color.icon_background),
                                getIconTravelModeBitmap(activity, noxbox.getOwner().getTravelMode().getImage())
                        )))
                        .anchor(0.5f, 1f);

            case demand:
                markerOptions = new MarkerOptions()
                        .position(noxbox.getPosition().toLatLng())
                        .icon(BitmapDescriptorFactory.fromBitmap(drawImage(
                                getIconBitmap(activity, noxbox.getType().getImage()),
                                getRatingColor(noxbox.getOwner().ratingToPercentage(), activity, noxbox),
                                activity.getResources().getColor(R.color.icon_background),
                                getIconTravelModeBitmap(activity, noxbox.getOwner().getTravelMode().getImage())
                        )))
                        .anchor(0.5f, 1f);

        }
        return markerOptions;
    }
    private static Bitmap drawImage(Bitmap bitmap, int raitingColor, int backgroundColor, Bitmap travelModeBitmap) {
        int borderSize = 12;
        int startPoint = 48;
        Paint raitingBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        raitingBorder.setColor(raitingColor);
        raitingBorder.setStyle(Paint.Style.STROKE);
        raitingBorder.setStrokeWidth(borderSize);

        Paint backgroundCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundCircle.setColor(backgroundColor);
        backgroundCircle.setStyle(Paint.Style.FILL);

        Bitmap bmpWithBorder = Bitmap.createBitmap(bitmap.getWidth() + startPoint * 2 + borderSize, bitmap.getHeight() + startPoint * 2 + borderSize, bitmap.getConfig());

        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawCircle(bitmap.getWidth() / 2 + startPoint, bitmap.getHeight() / 2 + startPoint, bitmap.getHeight() / 2 + borderSize, backgroundCircle);
        canvas.drawBitmap(bitmap, startPoint, startPoint, null);
        canvas.drawCircle(bitmap.getWidth() / 2 + startPoint, bitmap.getHeight() / 2 + startPoint, bitmap.getHeight() / 2 + borderSize, raitingBorder);
        canvas.drawBitmap(travelModeBitmap, bitmap.getWidth() / 2 + startPoint / 2, bitmap.getHeight() + startPoint + borderSize, null);

        return bmpWithBorder;
    }

    private static int getRatingColor(int percentage, Activity activity, Noxbox noxbox) {
//        if(TimeManager.compareTime(noxbox.getWorkSchedule().getStartInHours(),noxbox.getWorkSchedule().getStartInMinutes(),activity)){
//            return activity.getResources().getColor(R.color.divider);
//        }
        if (percentage == 100) {
            return activity.getResources().getColor(R.color.top_rating_color);
        } else if (percentage < 100 && percentage >= 95) {
            return activity.getResources().getColor(R.color.middle_rating_color);
        } else {
            return activity.getResources().getColor(R.color.low_rating_color);
        }
    }

    private static Bitmap getIconBitmap(Activity activity, int noxboxType) {
        return BitmapFactory.decodeResource(activity.getResources(), noxboxType);
    }

    private static Bitmap getIconTravelModeBitmap(Activity activity, int travelMode) {
        return BitmapFactory.decodeResource(activity.getResources(), travelMode);
    }


}