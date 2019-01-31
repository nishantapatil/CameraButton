package com.nishant.camerabutton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements CameraButton.CameraButtonResultListener {

    @BindView(R.id.activity_home_imageview)
    ImageView mImageview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_home_fab_photo)
    FloatingActionButton mFabPhoto;


    private Activity mActivity;
    private CameraButton mCamerabutton;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mActivity = this;
        setupToolBar();
        mCamerabutton = new CameraButton(mActivity);
        mCamerabutton.setCameraButtonResultListener(this);

    }

    /**
     * Set up the {@link Toolbar}.
     */
    private void setupToolBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @OnClick({R.id.activity_home_fab_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_home_fab_photo:
                mCamerabutton.performClick();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraButton.TAKE_PICTURE_REQUEST_CODE || requestCode == CameraButton.SELECT_PICTURE_REQUEST_CODE || requestCode == CameraButton.AFTER_IMAGE_CROPPING) {
            mCamerabutton.handleActivityResults(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CameraButton.REQUEST_WRITE_EXTERNAL_STORAGE || requestCode == CameraButton.REQUEST_CAMERA) {
            mCamerabutton.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void imageSaved(Uri uri) {
        Log.i("CameraButton", "Photo Selected : " + uri.getPath());
        mImageUri = uri;
        mImageview.setImageURI(uri);
    }
}
