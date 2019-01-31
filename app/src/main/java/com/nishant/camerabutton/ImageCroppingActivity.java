package com.nishant.camerabutton;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ImageCroppingActivity extends AppCompatActivity {

	public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
	public static final int REQUEST_CAMERA = 1;
	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;


	@BindView(R.id.activty_image_cropping_cropImageView)
	CropImageView mCropView;
	@BindView(R.id.activty_image_cropping_btn_fit_image)
	Button mBtnFitImage;
	@BindView(R.id.activty_image_cropping_btn_1_1)
	Button mBtn11;
	@BindView(R.id.activty_image_cropping_btn_16_9)
	Button mBtn169;
	@BindView(R.id.activty_image_cropping_btn_4_3)
	Button mBtn43;
	@BindView(R.id.activty_image_cropping_btn_free)
	Button mBtnFree;
	@BindView(R.id.activty_image_cropping_btn_circle)
	Button mBtnCircle;
	@BindView(R.id.activty_image_cropping_btn_grid)
	ImageButton mBtnGrid;
	@BindView(R.id.activty_image_cropping_layout_progress)
	FrameLayout mLayoutProgress;

	private Activity mActivity;
	private Uri mSaveUri;
	private View previousSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cropping);
		ButterKnife.bind(this);

		mActivity = this;
		setupToolBar();
		loadIntentData();

		checkIntentData();

		mBtnFree.setSelected(true);
		mBtnGrid.setSelected(true);
		previousSelected = mBtnFree;
		mCropView.setCropMode(CropImageView.CropMode.FREE);
		mCropView.setCompressFormat(Bitmap.CompressFormat.JPEG);
		mCropView.setCompressQuality(80);
		mCropView.setOutputMaxSize(2000, 2000);
		mCropView.setCropMode(CropImageView.CropMode.FREE);
	}

	private void loadIntentData() {
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			mSaveUri = intent.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
		}
	}

	/**
	 * Set up the {@link Toolbar}.
	 */
	private void setupToolBar() {
		final Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.image_crop, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_done:
				doneClicked();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void doneClicked() {
		mLayoutProgress.setVisibility(View.VISIBLE);
		mCropView.startCrop(mSaveUri, new CropCallback() {
			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onSuccess(Bitmap cropped) {

			}
		}, new SaveCallback() {
			@Override
			public void onError(Throwable e) {
				mLayoutProgress.setVisibility(View.GONE);
			}

			@Override
			public void onSuccess(Uri outputUri) {
				mLayoutProgress.setVisibility(View.GONE);
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}

	private void checkIntentData() {
		if (!mayRequestStorageNCamera()) {
			return;
		}
		if (getIntent().getData() != null) {
			mLayoutProgress.setVisibility(View.VISIBLE);
			mCropView.startLoad(getIntent().getData(), new LoadCallback() {
				@Override
				public void onError(Throwable e) {
					mLayoutProgress.setVisibility(View.GONE);
				}

				@Override
				public void onSuccess() {
					mLayoutProgress.setVisibility(View.GONE);
				}

			});
		}
	}

	private boolean mayRequestStorageNCamera() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}

		int permissionWrite = mActivity.checkSelfPermission(WRITE_EXTERNAL_STORAGE);
		int permissionCamera = mActivity.checkSelfPermission(CAMERA);

		if (permissionWrite == PackageManager.PERMISSION_GRANTED && permissionCamera == PackageManager.PERMISSION_GRANTED) {
			return true;
		}

		List<String> listPermissionsNeeded = new ArrayList<>();
		if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(WRITE_EXTERNAL_STORAGE);
		}
		if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(CAMERA);
		}
		if (!listPermissionsNeeded.isEmpty()) {
			mActivity.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
			return false;
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				checkIntentData();
			}
		}

		if (requestCode == REQUEST_CAMERA) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				checkIntentData();
			}
		}

		if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
			checkIntentData();
		}
	}


	@OnClick({R.id.activty_image_cropping_btn_rotate_left, R.id.activty_image_cropping_btn_rotate_right,
			R.id.activty_image_cropping_btn_fit_image, R.id.activty_image_cropping_btn_1_1, R.id.activty_image_cropping_btn_16_9,
			R.id.activty_image_cropping_btn_4_3, R.id.activty_image_cropping_btn_free, R.id.activty_image_cropping_btn_circle,
			R.id.activty_image_cropping_btn_grid})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.activty_image_cropping_btn_rotate_left:
				mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
				break;
			case R.id.activty_image_cropping_btn_rotate_right:
				mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
				break;
			case R.id.activty_image_cropping_btn_fit_image:
				previousSelected.setSelected(false);
				mBtnFitImage.setSelected(true);
				previousSelected = mBtnFitImage;
				mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
				break;
			case R.id.activty_image_cropping_btn_1_1:
				previousSelected.setSelected(false);
				mBtn11.setSelected(true);
				previousSelected = mBtn11;
				mCropView.setCropMode(CropImageView.CropMode.SQUARE);
				break;
			case R.id.activty_image_cropping_btn_16_9:
				previousSelected.setSelected(false);
				mBtn169.setSelected(true);
				previousSelected = mBtn169;
				mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
				break;
			case R.id.activty_image_cropping_btn_4_3:
				previousSelected.setSelected(false);
				mBtn43.setSelected(true);
				previousSelected = mBtn43;
				mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
				break;
			case R.id.activty_image_cropping_btn_free:
				previousSelected.setSelected(false);
				mBtnFree.setSelected(true);
				previousSelected = mBtnFree;
				mCropView.setCropMode(CropImageView.CropMode.FREE);
				break;
			case R.id.activty_image_cropping_btn_circle:
				previousSelected.setSelected(false);
				mBtnCircle.setSelected(true);
				previousSelected = mBtnCircle;
				mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
				break;
			case R.id.activty_image_cropping_btn_grid:
				if (mBtnGrid.isSelected()) {
					mBtnGrid.setSelected(false);
					mCropView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);
				} else {
					mBtnGrid.setSelected(true);
					mCropView.setGuideShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
				}
				break;
		}
	}
}
