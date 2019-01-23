package live.noxbox.menu.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import live.noxbox.R;
import live.noxbox.activities.BaseActivity;
import live.noxbox.database.AppCache;
import live.noxbox.model.Profile;
import live.noxbox.tools.Router;
import live.noxbox.tools.Task;

import static live.noxbox.menu.settings.NoxboxTypeSelectionFragment.SETTINGS_CODE;

public class MapSettingsActivity extends BaseActivity {

    public static final int CODE = 1005;

    private Switch novice;
    private Switch demand;
    private Switch supply;
    private SeekBar price;
    private TextView priceText;
    private LinearLayout typeLayout;
    private String[] typesName;
    private boolean[] typesChecked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialize();
    }

    private void initialize() {
        novice = findViewById(R.id.novice);
        demand = findViewById(R.id.demand);
        supply = findViewById(R.id.supply);
        price = findViewById(R.id.priceBar);
        priceText = findViewById(R.id.priceText);
        typeLayout = findViewById(R.id.typeLayout);


        ((TextView) findViewById(R.id.noviceTitle)).setText(getResources().getString(R.string.novice));
        ((TextView) findViewById(R.id.demandTitle)).setText(getResources().getString(R.string.demand).substring(0, 1).toUpperCase().concat(getResources().getString(R.string.demand).substring(1)));
        ((TextView) findViewById(R.id.supplyTitle)).setText(getResources().getString(R.string.supply).substring(0, 1).toUpperCase().concat(getResources().getString(R.string.supply).substring(1)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppCache.listenProfile(MapSettingsActivity.class.getName(), new Task<Profile>() {
            @Override
            public void execute(Profile profile) {
                draw(profile);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppCache.stopListen(this.getClass().getName());
        AppCache.fireProfile();
    }

    private void draw(final Profile profile) {
        drawToolbar();
        drawNovice(profile);
        drawDemand(profile);
        drawSupply(profile);
        drawPrice(profile);
        drawTypeList(profile);
    }

    private void drawToolbar() {
        ((TextView) findViewById(R.id.title)).setText(R.string.settings);
        findViewById(R.id.homeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.finishActivity(MapSettingsActivity.this);
            }
        });
    }

    private void drawNovice(final Profile profile) {
        novice.setChecked(profile.getFilters().getAllowNovices());
        novice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.getFilters().setAllowNovices(isChecked);
            }
        });
    }

    private void drawDemand(final Profile profile) {
        demand.setChecked(profile.getFilters().getDemand());
        demand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.getFilters().setDemand(isChecked);
                if (!isChecked && supply != null) {
                    supply.setChecked(true);
                }
            }
        });
    }

    private void drawSupply(final Profile profile) {
        supply.setChecked(profile.getFilters().getSupply());
        supply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.getFilters().setSupply(isChecked);
                if (!isChecked && demand != null) {
                    demand.setChecked(true);
                }

            }
        });
    }

    private void drawPrice(final Profile profile) {
        if (Integer.parseInt(profile.getFilters().getPrice()) >= 100) {
            price.setProgress(100);
            priceText.setText(getResources().getString(R.string.max));
        } else {
            priceText.setText(profile.getFilters().getPrice().concat(getResources().getString(R.string.currency)));
            price.setProgress(Integer.parseInt(profile.getFilters().getPrice()));
        }
        price.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        profile.getFilters().setPrice("1");
                        priceText.setText("1 " + getString(R.string.currency));
                        return;
                    }
                    if (progress == 100) {
                        profile.getFilters().setPrice("100000000");
                        priceText.setText(R.string.max);
                        return;
                    }
                    profile.getFilters().setPrice(String.valueOf(progress));
                    priceText.setText(progress + " " + getResources().getString(R.string.currency));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void drawTypeList(final Profile profile) {
        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new NoxboxTypeSelectionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("key", SETTINGS_CODE);
                dialog.setArguments(bundle);
                dialog.show(((FragmentActivity) MapSettingsActivity.this).getSupportFragmentManager(), NoxboxTypeSelectionFragment.TAG);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                Router.finishActivity(MapSettingsActivity.this);
                break;
        }
        return true;
    }
}