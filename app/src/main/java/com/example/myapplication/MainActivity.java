package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRv;
    public static List<Feed> mFeeds = new ArrayList<>();
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private boolean finish = false;
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    public Button mBtn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtns();
        closeAndroidPDialog();
        initRecyclerView();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(IMiniDouyinService.class).FeedResponse().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "fetchfeed success", Toast.LENGTH_SHORT).show();
                    }
                });
                mFeeds = response.body().getFeeds();
                try {
                    mRv.getAdapter().notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "fetchfeed fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.Layout_3_5).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        1);
            } else {
                Toast.makeText(this, "already granted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
            }
        });

    }





    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(MainActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        //mRv.setLayoutManager(new GridLayoutManager(this,2));
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRv.setAdapter(new ImageVideoAdapter(this));
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }


    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO);

    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
               mBtn.setText(R.string.post_it);
            }
        }
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImage = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part video = getMultipartFromUri("video", mSelectedVideo);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(IMiniDouyinService.class).createVideo("3170105781", "cat", coverImage, video).enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                        mBtn.setText("上传");
                        mBtn.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "upload fail", Toast.LENGTH_SHORT).show();
                        mBtn.setText("上传");
                        mBtn.setEnabled(true);
                    }
                });
            }
        });
    }


    private void initBtns() {
        mBtn = findViewById(R.id.Layout_0_2);
         if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    1);
        } else {
            Toast.makeText(this, "already granted", Toast.LENGTH_SHORT).show();
        }

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ;
                String s = mBtn.getText().toString();
                if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    }
                }
                else if ("上传".equals(s)) {
                    chooseImage();
                }else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                }
            }
        });
        mBtn2 = findViewById(R.id.Layout_0_1);
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFeed(v);
            }
        });

    }

    private void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetRefreshBtn() {
        mBtn2.setText("刷新");
        mBtn2.setEnabled(true);
    }

    public void fetchFeed(View view) {
        mBtn2.setText("requesting...");
        mBtn2.setEnabled(false);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(IMiniDouyinService.class).FeedResponse().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "fetchfeed success", Toast.LENGTH_SHORT).show();
                    }
                });
                mFeeds = response.body().getFeeds();
                resetRefreshBtn();
                try {
                    mRv.getAdapter().notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "fetchfeed fail", Toast.LENGTH_SHORT).show();
                    }
                });
                resetRefreshBtn();
            }
        });
    }

}
