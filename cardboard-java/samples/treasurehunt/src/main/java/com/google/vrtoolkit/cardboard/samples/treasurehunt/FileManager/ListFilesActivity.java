package com.google.vrtoolkit.cardboard.samples.treasurehunt.FileManager;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.FileManager.FolderOrPDF;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.FileManager.RecyclerViewFolderPDF;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Horasis Team on 10/05/2016.
 */
public class ListFilesActivity extends Activity {
    private String path;
    private ArrayList values = new ArrayList<FolderOrPDF>();
    private TextView textViewPath;
    private RecyclerView mRecyclerView;
    private RecyclerViewFolderPDF mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        mRecyclerView = new RecyclerView(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_pdf);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        values = new ArrayList<FolderOrPDF>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Use the current directory as title
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                if (getIntent().hasExtra("path")) {
                    path = getIntent().getStringExtra("path");
                }
                textViewPath = (TextView) findViewById(R.id.text_view_path);
                textViewPath.setText(path);

                // Read all files sorted into the values-array
                File dir = new File(path);
                if (!dir.canRead()) {
                    setTitle(getTitle() + " (inaccessible)");
                }
                String[] list = dir.list();
                if (list != null) {
                    for (String file : list) {
                        if (!file.startsWith(".")) {
                            FolderOrPDF filename;
                            if (path.endsWith(File.separator)) {
                                filename = new FolderOrPDF(path + file);
                            } else {
                                filename = new FolderOrPDF(path + File.separator + file);
                            }
                            if (filename.getPath().endsWith(".pdf")){
                                filename.setType("pdf");
                                values.add(filename);
                            }
                            else if (new File(filename.getPath()).isDirectory()) {
                                filename.setType("folder");
                                values.add(filename);
                            }
                        }
                    }
                }
                Collections.sort(values);
                mAdapter.setFolderOrPDF(values);
                //Deposer le Runnable dans la file d'attente de l'UI thread
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //code execute par l'UI thread
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewFolderPDF(values,this);
        mRecyclerView.setAdapter(mAdapter);
    }

}
