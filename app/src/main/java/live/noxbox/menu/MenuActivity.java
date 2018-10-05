/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package live.noxbox.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import live.noxbox.BuildConfig;
import live.noxbox.R;
import live.noxbox.filters.MapFiltersActivity;
import live.noxbox.model.IntentAndKey;
import live.noxbox.model.Profile;
import live.noxbox.pages.AuthActivity;
import live.noxbox.profile.ProfileActivity;
import live.noxbox.state.ProfileStorage;
import live.noxbox.tools.Task;

public abstract class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProfileStorage.listenProfile(MenuActivity.class.getName(), new Task<Profile>() {
            @Override
            public void execute(Profile profile) {
                draw(profile);
            }
        });
    }

    protected Drawer menu;

    private void draw(Profile profile) {
        makeProfileImageRounded();

        ProfileDrawerItem account = new ProfileDrawerItem()
                .withName(profile.getName())
                .withEmail(profile.ratingToPercentage() + " %");
        if (profile.getPhoto() == null) {
            account.withIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.profile_picture_blank));
        } else {
            account.withIcon(profile.getPhoto());
        }

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary)
                .withTextColorRes(R.color.secondary)
                .withProfileImagesClickable(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withAccountHeader(R.layout.material_drawer_header)
                .addProfiles(account)
                .build();

        SecondaryDrawerItem version = new SecondaryDrawerItem()
                .withName("Version " + BuildConfig.VERSION_NAME)
                .withEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        menu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withAccountHeader(header)
                .addDrawerItems(convertToItems(getMenu()))
                .addStickyDrawerItems(version)
                .withStickyFooterShadow(false)
                .build();

        menu.getDrawerLayout().setFitsSystemWindows(true);


        ImageView menuImage = findViewById(R.id.menu);
        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.openDrawer();
            }
        });
        menuImage.setVisibility(View.VISIBLE);
    }

    private void makeProfileImageRounded() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(final ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(getApplicationContext()).asBitmap().load(uri)
                        .apply(RequestOptions.placeholderOf(placeholder).circleCrop())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(resource);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
                                    }
                                });
                            }
                        });
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(getApplicationContext()).clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return ContextCompat.getDrawable(getApplicationContext(), R.drawable.profile_picture_blank);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return placeholder(ctx);
            }
        });
    }

    private IDrawerItem[] convertToItems(Map<String, IntentAndKey> menu) {
        List<IDrawerItem> items = new ArrayList<>();
        for (final Map.Entry<String, IntentAndKey> entry : menu.entrySet()) {
            PrimaryDrawerItem item = new PrimaryDrawerItem()
                    .withIdentifier(entry.getKey().hashCode())
                    .withName(entry.getKey())
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            startActivityForResult(entry.getValue().getIntent(), entry.getValue().getKey());
                            return true;
                        }
                    })
                    .withTextColorRes(R.color.primary);

            items.add(item);
        }

        PrimaryDrawerItem logout = new PrimaryDrawerItem()
                .withIdentifier(999)
                .withName(getResources().getString(R.string.logout))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this,
                                R.style.NoxboxAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.logoutPrompt));
                        builder.setPositiveButton(getResources().getString(R.string.logout),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        AuthUI.getInstance()
                                                .signOut(MenuActivity.this)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                                        // TODO (nli) start activity for result
                                                        startActivity(new Intent(MenuActivity.this, AuthActivity.class));
                                                    }
                                                });
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, null);
                        builder.show();
                        return true;
                    }
                })
                .withTextColorRes(R.color.primary);
        items.add(logout);

        return items.toArray(new IDrawerItem[menu.size()]);
    }

    protected SortedMap<String, IntentAndKey> getMenu() {
        TreeMap<String, IntentAndKey> menu = new TreeMap<>();
        menu.put(getString(R.string.history), new IntentAndKey()
                .setIntent(new Intent(getApplicationContext(), HistoryActivity.class))
                .setKey(HistoryActivity.CODE));
        menu.put(getString(R.string.wallet), new IntentAndKey()
                .setIntent(new Intent(getApplicationContext(), WalletActivity.class))
                .setKey(WalletActivity.CODE));
        menu.put(getString(R.string.filters), new IntentAndKey()
                .setIntent(new Intent(getApplicationContext(), MapFiltersActivity.class))
                .setKey(MapFiltersActivity.CODE));
        menu.put(getString(R.string.profile), new IntentAndKey()
                .setIntent(new Intent(getApplicationContext(), ProfileActivity.class))
                .setKey(ProfileActivity.CODE));
        return menu;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == WalletActivity.CODE) {
            ProfileStorage.readProfile(new Task<Profile>() {
                @Override
                public void execute(Profile profile) {
                    draw(profile);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
