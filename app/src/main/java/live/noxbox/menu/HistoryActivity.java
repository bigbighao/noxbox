package live.noxbox.menu;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import live.noxbox.BaseActivity;
import live.noxbox.R;
import live.noxbox.database.AppCache;
import live.noxbox.model.Noxbox;
import live.noxbox.model.Profile;
import live.noxbox.tools.Task;

public class HistoryActivity extends BaseActivity {

    public static final int CODE = 1002;

    private HistoryAdapter historyAdapter;
    private List<Noxbox> historyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        setTitle(R.string.history);

        int size = 384;
        Glide.with(this).asGif()
                .load(R.drawable.progress_cat)
                .apply(RequestOptions.overrideOf(size, size))
                .into((ImageView) findViewById(R.id.progressView));

        AppCache.readProfile(new Task<Profile>() {
            @Override
            public void execute(Profile profile) {
                loadHistory(profile);
            }
        });
    }

    private void loadHistory(Profile profile) {
        ListView listView = findViewById(R.id.historyView);
        historyAdapter = new HistoryAdapter(HistoryActivity.this, profile.getId(), historyItems);
        listView.setAdapter(historyAdapter);

//        GeoRealtime.loadHistory(new Task<Collection<Noxbox>>() {
//            @Override
//            public void execute(Collection<Noxbox> noxboxes) {
//                historyItems.addAll(noxboxes);
//                Collections.sort(historyItems);
//                historyAdapter.notifyDataSetChanged();
//
//                ImageView progressView = findViewById(R.id.progressView);
//                progressView.setVisibility(View.INVISIBLE);
//                ListView listView = findViewById(R.id.historyView);
//                listView.setVisibility(View.VISIBLE);
//            }
//        });
    }

}
