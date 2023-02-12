package com.tonghu.removeallbackgroundapp;

import static com.tonghu.removeallbackgroundapp.util.AppUtils.isPackageRunning;
import static com.tonghu.removeallbackgroundapp.util.AppUtils.isSystemApp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private PackageManager mPackageManager;
    private List<IAppInfo> appInfoList = new ArrayList<>();
    private RecyclerView.Adapter<Holder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackageManager = getPackageManager();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.rv);

        updateRunningPackages();

        adapter = new RecyclerView.Adapter<Holder>() {
            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.view_item, parent, false);
                return new Holder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull Holder holder, int position) {
                IAppInfo appInfo = appInfoList.get(position);
                holder.tv.setText(appInfo.name());
                holder.iv.setImageDrawable(appInfo.getIcon(MainActivity.this));
                Log.i("test", "name: " + appInfo.name());

                holder.itemView.setOnClickListener(v -> {
                    IAppInfo itemAtPosition = appInfoList.get(position);
                    String packageName = itemAtPosition.packageName();
                    Uri packageUri = Uri.parse("package:" + packageName);
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                    startActivityForResult(uninstallIntent, 0);
                });
            }

            @Override
            public int getItemCount() {
                return appInfoList.size();
            }
        };
        rv.setAdapter(adapter);
        updateInstalledPackage();
    }

    private void updateInstalledPackage() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo installedPackage : installedPackages) {
            String packageName = installedPackage.packageName;
            if (isSystemApp(this, packageName)) {
                continue;
            }
            String name = pm.getApplicationLabel(installedPackage.applicationInfo).toString();
            Log.i("test", "packageName: " + packageName + ", name: " + name);
            AppInfo appInfo = new AppInfo(packageName, name);
            appInfoList.add(appInfo);
        }
        Log.i("test", "list: " + appInfoList);
        adapter.notifyDataSetChanged();
    }


    private void updateRunningPackages() {
//        runningResolveInfo.clear();
//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
//        for (int i = 0; i < pkgAppsList.size(); i++) {
//            String packageName = pkgAppsList.get(i).activityInfo.packageName;
//            if (!isSystemApp(this, packageName)) {
//                runningResolveInfo.add(pkgAppsList.get(i));
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateRunningPackages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class Holder extends RecyclerView.ViewHolder {
        private TextView tv;
        private ImageView iv;
        public Holder(@NonNull View root) {
            super(root);
            this.tv = root.findViewById(R.id.tv);
            this.iv = root.findViewById(R.id.iv);
        }
    }

    interface IAppInfo {
        String packageName();
        String name();

        @Nullable
        Drawable getIcon(Context context);
    }

    class AppInfo implements IAppInfo {
        private final String packageName;
        private final String name;

        public AppInfo(String packageName, String name) {
            this.packageName = packageName;
            this.name = name;
        }

        @Override
        public String packageName() {
            return packageName;
        }

        @Override
        public String name() {
            return name;
        }

        @Nullable
        @Override
        public Drawable getIcon(Context context) {
            try {
                return context.getPackageManager().getApplicationIcon(getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
    }
}
