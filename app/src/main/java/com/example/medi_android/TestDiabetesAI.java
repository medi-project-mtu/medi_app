package com.example.medi_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medi_android.ml.Model;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestDiabetesAI extends AppCompatActivity {

    private Button diabetesAI;
    private Interpreter interpreter;
    private TextView ai_result_tv;
    private float result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_diabetes_ai);
//        getDiabetesModel();
//        3,111,90,12,78,28.4,0.495,29, pred: 0.987
//        17,163,72,41,114,40.9,0.817,47, 1.0
//        2,100,64,23,0,29.7,0.368,21
//        0,131,88,0,0,31.6,0.743,32
//        1,153,82,42,485,40.6,0.687,23
//        1,136,74,50,204,37.4,0.399,24, should be 0, pred: 0.975
//        10,108,66,0,0,32.4,0.272,42 should be 1, pred: 0.99999999917


//        2,108,52,26,63,32.5,0.318,22,0, pred: 0.987
//        4,154,62,31,284,32.8,0.237,23,0 pred: 0.987

//        3,170,64,37,225,34.5,0.356,30,1, pred: 0.985
        ai_result_tv = findViewById(R.id.ai_result);
        diabetesAI = findViewById(R.id.diabetes_ai);
        diabetesAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                doAIMagic();
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 4);
                    byteBuffer.putFloat(0,4);
                    byteBuffer.putFloat(1,154f);
                    byteBuffer.putFloat(2,62f);
                    byteBuffer.putFloat(3,31);
                    byteBuffer.putFloat(4,284f);
                    byteBuffer.putFloat(5,32.8f);
                    byteBuffer.putFloat(6,0.237f);
                    byteBuffer.putFloat(7,23);




                    Model model = Model.newInstance(TestDiabetesAI.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 8}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
//                    float[] outputFeature0 = outputs.getOutputFeature0AsTensorBuffer().getFloatArray();
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    System.out.println(outputFeature0.toString());
                    System.out.println(outputFeature0.getFloatValue(0));
                    System.out.println(Arrays.toString(outputFeature0.getShape()));
//                    System.out.println(outputFeature0[0]);
//                    ai_result_tv.setText(Float.toString(outputFeature0[0]));

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }

        });
    }




    private float doAIMagic() {
        float[] input1 = new float[] {6,0,0,0,0,31.2f,0.00382f,0};
        float[] input2 = new float[] {1f,130f,60f,23f,170f,28.6f,0.692f,21f};
        float[] input3 = new float[] {2f,84f,50f,23f,76f,30.4f,0.968f,21f};
        float[] input4 = new float[] {8f,120f,78f,0f,0f,25f,0.409f,64f};
        float[] input5 = new float[] {6,102,82,0,0,30.8f,0.18f,36};
        float[] input6 = new float[] {0,131,88,0,0,31.6f,0.743f,32};
        float[] input7 = new float[] {8,179,72,42,130,32.7f,0.719f,36,1};
        float[] input8 = new float[] {0,129,110,46,130,67.1f,0.319f,26,1};

        float[][] output1 = new float[1][1];
        float[][] output2 = new float[1][1];
        float[][] output3 = new float[1][1];
        float[][] output4 = new float[1][1];
        float[][] output5 = new float[1][1];
        float[][] output6 = new float[1][1];
        float[][] output7 = new float[1][1];
        float[][] output8 = new float[1][1];

        interpreter.run(input1, output1);
        interpreter.run(input2, output2);
        interpreter.run(input3, output3);
        interpreter.run(input4, output4);
        interpreter.run(input5, output5);
        interpreter.run(input6, output6);
        interpreter.run(input7, output7);
        interpreter.run(input8, output8);
        System.out.println(Arrays.deepToString(output2));
        Log.i("test1------------",Float.toString(output1[0][0]));
        Log.i("test2------------",Float.toString(output2[0][0]));
        Log.i("test3------------",Float.toString(output3[0][0]));
        Log.i("test4------------",Float.toString(output4[0][0]));
        Log.i("test5------------",Float.toString(output5[0][0]));
        Log.i("test6------------",Float.toString(output6[0][0]));
        Log.i("test7------------",Float.toString(output7[0][0]));
        Log.i("test8------------",Float.toString(output8[0][0]));
//        Map<String, Float> inputs = new HashMap<String, Float>();
//        inputs.put("Pregnancies", 2f);
//        inputs.put("Glucose", 138f);
//        inputs.put("BloodPressure", 62f);
//        inputs.put("SkinThickness", 35f);
//        inputs.put("Insulin", 0f);
//        inputs.put("BMI", 33.6f);
//        inputs.put("DiabetesPedigreeFunction", 0.127f);
//        inputs.put("Age", 47f);
//
//        Map<String, Float> outputs = new HashMap<String, Float>();
//        outputs.put("Outcome", 0f);
//
//        interpreter.run(inputs, outputs);
//        interpreter.runForMultipleInputsOutputs();

//        System.out.println(outputs);

        return 1.2f;
    }


    private void getDiabetesModel() {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("Diabetes-Prediction", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                            System.out.println(modelFile.getAbsoluteFile());
                        }
//                        Toast.makeText(TestDiabetesAI.this, "diabetesAI", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}