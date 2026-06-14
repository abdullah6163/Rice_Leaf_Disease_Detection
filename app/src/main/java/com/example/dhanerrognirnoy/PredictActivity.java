package com.example.dhanerrognirnoy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnPredict, btnChooseAgain;
    LinearLayout loadingOverlay;

    Uri selectedImageUri;
    String source = "gallery";

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<String> cameraPermissionLauncher;
    ActivityResultLauncher<CropImageContractOptions> cropLauncher;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        imgPreview = findViewById(R.id.imgPreview);
        btnPredict = findViewById(R.id.btnPredict);
        btnChooseAgain = findViewById(R.id.btnChooseAgain);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        source = getIntent().getStringExtra("source");
        if (source == null) source = "gallery";

        setupLaunchers();

        if (source.equals("camera")) checkCameraPermissionAndOpen();
        else openGallery();

        btnChooseAgain.setOnClickListener(v -> {
            if (source.equals("camera")) checkCameraPermissionAndOpen();
            else openGallery();
        });

        btnPredict.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                sendImageToApi(selectedImageUri);
            } else {
                Toast.makeText(this, "Select image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================
    // LAUNCHERS
    // =========================
    private void setupLaunchers() {

        cropLauncher = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                selectedImageUri = result.getUriContent();
                imgPreview.setImageURI(selectedImageUri);
            } else {
                Toast.makeText(this, "Crop cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (selectedImageUri != null) startCrop(selectedImageUri);
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) startCrop(uri);
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openCamera();
                    else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // =========================
    // CROP
    // =========================
    private void startCrop(Uri uri) {

        CropImageOptions options = new CropImageOptions();

        options.guidelines = CropImageView.Guidelines.ON;
        options.fixAspectRatio = true;
        options.aspectRatioX = 1;
        options.aspectRatioY = 1;

        options.activityTitle = "Crop Image";
        options.cropMenuCropButtonTitle = "DONE";
        options.toolbarColor = 0xFF2E7D32;

        CropImageContractOptions cropOptions =
                new CropImageContractOptions(uri, options);

        cropLauncher.launch(cropOptions);
    }

    // =========================
    // CAMERA / GALLERY
    // =========================
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File imageFile = File.createTempFile("rice_leaf_", ".jpg", getCacheDir());

            selectedImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    imageFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
            cameraLauncher.launch(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // =========================
    // MAIN API CALL (RESNET FIRST)
    // =========================
    private void sendImageToApi(Uri imageUri) {

        try {
            loadingOverlay.setVisibility(View.VISIBLE);

            File file = FileUtil.uriToFile(this, imageUri);

            RequestBody requestFile =
                    RequestBody.create(file, MediaType.parse("image/*"));

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // 🔥 FIRST TRY RESNET
            ApiService apiService = ApiClient.getResNetClient().create(ApiService.class);
            Call<PredictionResponse> call = apiService.uploadImage(body);

            call.enqueue(new Callback<PredictionResponse>() {

                @Override
                public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        goToResult(response.body(), "ResNet50");
                    } else {
                        callMobileNet(body);
                    }
                }

                @Override
                public void onFailure(Call<PredictionResponse> call, Throwable t) {
                    callMobileNet(body);
                }
            });

        } catch (Exception e) {
            loadingOverlay.setVisibility(View.GONE);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // =========================
    // FALLBACK → MOBILENET
    // =========================
    private void callMobileNet(MultipartBody.Part body) {

        ApiService apiService = ApiClient.getMobileNetClient().create(ApiService.class);
        Call<PredictionResponse> call = apiService.uploadImage(body);

        call.enqueue(new Callback<PredictionResponse>() {

            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {

                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    goToResult(response.body(), "MobileNetV2 (Fallback)");
                } else {
                    Toast.makeText(PredictActivity.this, "Both models failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(PredictActivity.this, "Both models failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // RESULT NAVIGATION
    // =========================
    private void goToResult(PredictionResponse result, String modelUsed) {

        loadingOverlay.setVisibility(View.GONE);

        String finalModel = result.modelUsed != null && !result.modelUsed.isEmpty()
                ? result.modelUsed
                : modelUsed;

        String imageUriText = selectedImageUri != null
                ? selectedImageUri.toString()
                : "";

        Intent intent = new Intent(PredictActivity.this, ResultActivity.class);

        intent.putExtra("disease", result.disease);
        intent.putExtra("confidence", result.confidence);
        intent.putExtra("model", finalModel);
        intent.putExtra("inference_time", result.inferenceTime);
        intent.putExtra("imageUri", imageUriText);

        intent.putExtra("is_valid_input", result.isValidInput);
        intent.putExtra("warning", result.warning);
        intent.putExtra("green_ratio", result.greenRatio);
        intent.putExtra("model_agreement", result.modelAgreement);

        intent.putExtra("resnet_prediction", result.resnetPrediction);
        intent.putExtra("resnet_confidence", result.resnetConfidence);

        intent.putExtra("vgg16_prediction", result.vgg16Prediction);
        intent.putExtra("vgg16_confidence", result.vgg16Confidence);

        intent.putExtra("xception_prediction", result.xceptionPrediction);
        intent.putExtra("xception_confidence", result.xceptionConfidence);

        intent.putExtra("mobilenet_prediction", result.mobilenetPrediction);
        intent.putExtra("mobilenet_confidence", result.mobilenetConfidence);

        startActivity(intent);
    }
}