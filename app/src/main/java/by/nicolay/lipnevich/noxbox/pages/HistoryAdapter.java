package by.nicolay.lipnevich.noxbox.pages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import by.nicolay.lipnevich.noxbox.R;
import by.nicolay.lipnevich.noxbox.model.EventType;
import by.nicolay.lipnevich.noxbox.model.Noxbox;
import by.nicolay.lipnevich.noxbox.model.Request;
import by.nicolay.lipnevich.noxbox.state.Firebase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import java.util.List;

import static by.nicolay.lipnevich.noxbox.tools.DateTimeFormatter.date;
import static by.nicolay.lipnevich.noxbox.tools.DateTimeFormatter.time;
import static java.lang.System.currentTimeMillis;

/**
 * Created by nicolay.lipnevich on 22/06/2017.
 */

public class HistoryAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater inflater;
    List<Noxbox> noxboxes;

    public HistoryAdapter(Context context, List<Noxbox> noxboxes) {
        this.ctx = context;
        this.noxboxes = noxboxes;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return noxboxes.size();
    }

    @Override
    public Object getItem(int position) {
        return noxboxes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final View view = convertView != null ? convertView : inflater.inflate(R.layout.history_item, parent, false);

        final Noxbox noxbox = (Noxbox) getItem(position);

        ((TextView) view.findViewById(R.id.dateText)).setText(date(noxbox.getTimeCompleted()));
        ((TextView) view.findViewById(R.id.timeText)).setText(time(noxbox.getTimeCompleted()));
        ((TextView) view.findViewById(R.id.priceText)).setText(noxbox.getPrice());
        ((TextView) view.findViewById(R.id.performerName)).setText(noxbox.getParty().getName());

        // TODO (nli) remove map from list, replace with address link to map
        MapView mapView = view.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(view.getContext());
                GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(noxbox.getType().getImage()))
                        .position(noxbox.getPosition().toLatLng(), 48, 48)
                        .anchor(0.5f, 1)
                        .zIndex(1000);
                GroundOverlay marker = googleMap.addGroundOverlay(newarkMap);
                marker.setDimensions(960, 960);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(noxbox.getPosition().toLatLng(), 11));
            }
        });

        Glide.with(view.getContext())
                .load(noxbox.getParty().getPhoto())
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) view.findViewById(R.id.performerImage));

        boolean isLiked = isLiked(noxbox);

        final ImageView rateBox = view.findViewById(R.id.rateBox);
        showRating(rateBox, isLiked);
        if(isLiked) {
            rateBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.NoxboxAlertDialogStyle);
                    builder.setTitle(ctx.getResources().getString(R.string.dislikePrompt));
                    builder.setPositiveButton(ctx.getResources().getString(R.string.dislike),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    v.setOnClickListener(null);
                                    showRating((ImageView) v, false);
                                    dislike(noxbox);
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.show();
                }
            });
        }
        return view;
    }

    private boolean isLiked(Noxbox noxbox) {
        return noxbox.getParty().getTimeDisliked() == null;
    }


    private void dislike(Noxbox noxbox) {
        Firebase.sendRequest(new Request().setType(EventType.dislike)
                .setNoxbox(new Noxbox().setId(noxbox.getId())));

        noxbox.getParty().setTimeDisliked(currentTimeMillis());
        Firebase.persistHistory(noxbox);
    }

    private void showRating(ImageView view, boolean isLiked) {
        if(isLiked) {
            view.setImageResource(R.drawable.like);
        } else {
            view.setImageResource(R.drawable.dislike);
        }
    }

}
