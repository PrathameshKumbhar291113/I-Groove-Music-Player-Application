package com.example.igroove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        runtimePermission();
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displaySongs();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> fetchSongs(@NonNull File file) {//This method will fetch songs from the external storage of the device.
        ArrayList<File> arrayList = new ArrayList<>();
        File[] filesSongArray = file.listFiles();
        if(filesSongArray!=null){
            for (File songFile : filesSongArray) {
                if (songFile.isDirectory() && !songFile.isHidden()) {
                    arrayList.addAll(fetchSongs(songFile));
                } else {
                    if (songFile.getName().endsWith(".mp3") && !songFile.getName().startsWith(".")) {
                        arrayList.add(songFile);
                    }
                }
            }
        }

        return arrayList;
    }

    public void displaySongs(){
        final ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i =0 ;i<mySongs.size();i++){
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","");
        }

        CustomAdapter cAd = new CustomAdapter();
        listView.setAdapter(cAd);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class).putExtra("songList",mySongs).putExtra("songName",songName).putExtra("position",i);
                startActivity(intent);
            }
        });

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSongName = view.findViewById(R.id.textSongName);
            textSongName.setSelected(true);
            textSongName.setText(items[i]);
            return view;
        }
    }

}