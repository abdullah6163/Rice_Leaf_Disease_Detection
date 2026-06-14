package com.example.dhanerrognirnoy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView txtDisease, txtConfidence, txtModel, txtInferenceTime;
    TextView txtAgreement, txtWarning;
    TextView txtResNet, txtVGG16, txtXception, txtMobileNet;
    ImageView imgResult;
    LinearLayout warningCard;
    Button btnAdvice;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtDisease = findViewById(R.id.txtDisease);
        txtConfidence = findViewById(R.id.txtConfidence);
        txtModel = findViewById(R.id.txtModel);
        txtInferenceTime = findViewById(R.id.txtInferenceTime);
        txtAgreement = findViewById(R.id.txtAgreement);
        txtWarning = findViewById(R.id.txtWarning);

        txtResNet = findViewById(R.id.txtResNet);
        txtVGG16 = findViewById(R.id.txtVGG16);
        txtXception = findViewById(R.id.txtXception);
        txtMobileNet = findViewById(R.id.txtMobileNet);

        imgResult = findViewById(R.id.imgResult);
        warningCard = findViewById(R.id.warningCard);
        btnAdvice = findViewById(R.id.btnAdvice);

        String disease = getIntent().getStringExtra("disease");
        double confidence = getIntent().getDoubleExtra("confidence", 0);
        String model = getIntent().getStringExtra("model");
        double inferenceTime = getIntent().getDoubleExtra("inference_time", 0);
        String imageUriString = getIntent().getStringExtra("imageUri");

        boolean isValidInput = getIntent().getBooleanExtra("is_valid_input", true);
        String warning = getIntent().getStringExtra("warning");
        int modelAgreement = getIntent().getIntExtra("model_agreement", 0);

        String resnetPrediction = getIntent().getStringExtra("resnet_prediction");
        double resnetConfidence = getIntent().getDoubleExtra("resnet_confidence", 0);

        String vgg16Prediction = getIntent().getStringExtra("vgg16_prediction");
        double vgg16Confidence = getIntent().getDoubleExtra("vgg16_confidence", 0);

        String xceptionPrediction = getIntent().getStringExtra("xception_prediction");
        double xceptionConfidence = getIntent().getDoubleExtra("xception_confidence", 0);

        String mobilenetPrediction = getIntent().getStringExtra("mobilenet_prediction");
        double mobilenetConfidence = getIntent().getDoubleExtra("mobilenet_confidence", 0);

        if (disease == null) disease = "Unknown";
        if (model == null) model = "Unknown";
        if (warning == null) warning = "";

        txtDisease.setText(disease.replace("_", " ").toUpperCase());
        txtConfidence.setText(String.format("Confidence: %.2f%%", confidence));
        txtModel.setText(model);
        txtInferenceTime.setText(String.format("%.3f sec", inferenceTime));
        txtAgreement.setText(modelAgreement + " / 4 models agree");

        if (!isValidInput || !warning.isEmpty()) {
            warningCard.setVisibility(View.VISIBLE);
            txtWarning.setText(warning);
        } else {
            warningCard.setVisibility(View.GONE);
        }

        if (imageUriString != null && !imageUriString.isEmpty()) {
            imgResult.setImageURI(Uri.parse(imageUriString));
        }

        txtResNet.setText(String.format(
                "ResNet50: %s (%.2f%%)",
                safeText(resnetPrediction),
                resnetConfidence
        ));

        txtVGG16.setText(String.format(
                "VGG16: %s (%.2f%%)",
                safeText(vgg16Prediction),
                vgg16Confidence
        ));

        txtXception.setText(String.format(
                "Xception: %s (%.2f%%)",
                safeText(xceptionPrediction),
                xceptionConfidence
        ));

        txtMobileNet.setText(String.format(
                "MobileNetV2: %s (%.2f%%)",
                safeText(mobilenetPrediction),
                mobilenetConfidence
        ));

        String finalDisease = disease;

        btnAdvice.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, ChatActivity.class);
            intent.putExtra(
                    "autoMessage",
                    "Explain symptoms, causes, prevention and treatment of "
                            + finalDisease + " in rice plants."
            );
            startActivity(intent);
        });
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Unknown";
        }
        return value;
    }
}