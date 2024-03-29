package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.graphics.Bitmap;

import static com.example.myapplication.Utils.SYSTEM_TYPE_IMAGE;
import static com.example.myapplication.Utils.getOutputMediaFile;

public class Activityupdate extends AppCompatActivity {

    private Uri videoUri;
    public Button mBtn;
    private ImageView mview;
    private Uri mSelectedImage;
    private static final String TAG = Activityupdate.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityupdate);
        Log.e(TAG, "onCreate: Activityupdate");
        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra("videoUri"));
        mview = findViewById(R.id.imageView3);
        loadCover(mview, intent.getStringExtra("videoUri"), this);
        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postVideo();
            }
        });
    }


    public static void loadCover(ImageView imageView, String url, Context context) {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                                .error(R.mipmap.ic_launcher)
                                .placeholder(R.mipmap.ic_launcher)
                )
                .load(url)
                .into(imageView);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        mview.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mview.getDrawingCache());
        mview.setDrawingCacheEnabled(false);

        saveBitmap(bitmap);


        MultipartBody.Part coverImage = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part video = getMultipartFromUri("video", videoUri);
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
                        Toast.makeText(Activityupdate.this, "upload success", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Activityupdate.this, "upload fail", Toast.LENGTH_SHORT).show();
                        mBtn.setText("上传");
                        mBtn.setEnabled(true);
                    }
                });
            }
        });

        this.finish();
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(Activityupdate.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public void saveBitmap(Bitmap bm) {
        Log.e(TAG, "保存图片");
        File f = getOutputMediaFile(SYSTEM_TYPE_IMAGE);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        mSelectedImage = Uri.fromFile(f);
    }

}
