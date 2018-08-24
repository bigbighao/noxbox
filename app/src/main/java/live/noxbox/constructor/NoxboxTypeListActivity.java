package live.noxbox.constructor;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import live.noxbox.R;
import live.noxbox.model.NoxboxType;
import live.noxbox.model.Portfolio;
import live.noxbox.model.Profile;
import live.noxbox.profile.ProfileActivity;
import live.noxbox.state.ProfileStorage;
import live.noxbox.tools.Task;

public class NoxboxTypeListActivity extends ListActivity {
    public static final int PROFILE_CODE = 1010;

    private List<NoxboxType> typeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = this.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(getResources().getColor(R.color.primary));

        ProfileStorage.readProfile(new Task<Profile>() {
            @Override
            public void execute(Profile profile) {
                if (getIntent().getStringExtra(ProfileActivity.class.getName()) != null
                        && getIntent().getStringExtra(ProfileActivity.class.getName()).equals(NoxboxTypeListActivity.class.getName())) {
                    typeList = new ArrayList<>();
                    for (NoxboxType type : NoxboxType.values()) {
                        if (profile.getPortfolio().get(type.name()) == null) {
                            typeList.add(type);
                        }
                    }
                    createArrayAdapter();
                    drawInProfile(profile);

                } else {

                    typeList = Arrays.asList(NoxboxType.values());
                    createArrayAdapter();
                    drawInConstructor(profile);

                }
            }
        });


    }

    private void createArrayAdapter() {
        ArrayAdapter<NoxboxType> itemArrayAdapter = new NoxboxTypeAdapter(NoxboxTypeListActivity.this, typeList);
        setListAdapter(itemArrayAdapter);
    }

    private void drawInConstructor(final Profile profile) {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                profile.getCurrent().setType(typeList.get(i));
                finish();
            }
        });
    }

    private void drawInProfile(final Profile profile) {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                profile.getPortfolio().put(typeList.get(i).name(), new Portfolio());
                finish();
            }
        });
    }
}