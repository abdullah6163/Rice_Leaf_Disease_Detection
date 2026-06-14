package com.example.dhanerrognirnoy;

import com.google.gson.annotations.SerializedName;

public class PredictionResponse {

    public boolean success;

    public String disease;

    public double confidence;

    @SerializedName("class_index")
    public int classIndex;

    public String error;

    @SerializedName("model_used")
    public String modelUsed;

    @SerializedName("inference_time")
    public double inferenceTime;

    // =========================
    // INPUT VALIDATION
    // =========================

    @SerializedName("is_valid_input")
    public boolean isValidInput;

    public String warning;

    @SerializedName("green_ratio")
    public double greenRatio;

    @SerializedName("model_agreement")
    public int modelAgreement;

    // =========================
    // RESNET
    // =========================

    @SerializedName("resnet_prediction")
    public String resnetPrediction;

    @SerializedName("resnet_confidence")
    public double resnetConfidence;

    // =========================
    // VGG16
    // =========================

    @SerializedName("vgg16_prediction")
    public String vgg16Prediction;

    @SerializedName("vgg16_confidence")
    public double vgg16Confidence;

    // =========================
    // XCEPTION
    // =========================

    @SerializedName("xception_prediction")
    public String xceptionPrediction;

    @SerializedName("xception_confidence")
    public double xceptionConfidence;

    // =========================
    // MOBILENET
    // =========================

    @SerializedName("mobilenet_prediction")
    public String mobilenetPrediction;

    @SerializedName("mobilenet_confidence")
    public double mobilenetConfidence;
}