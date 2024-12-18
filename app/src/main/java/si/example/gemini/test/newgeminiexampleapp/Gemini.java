package si.example.gemini.test.newgeminiexampleapp;

import android.graphics.Bitmap;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.concurrent.Executor;

public class Gemini {
    public static void getResponse(ChatFutures chatModel, String message, Bitmap image, ResponseCallBack responseCallBack) {
        Content.Builder userMessage = new Content.Builder();
        userMessage.setRole("user");
        userMessage.addText(message);
        if( image != null ) {
            userMessage.addImage(image);
        }
        Content userMessageContent = userMessage.build();

        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = chatModel.sendMessage(userMessageContent);
        Futures.addCallback(response, new com.google.common.util.concurrent.FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                responseCallBack.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                responseCallBack.onError(t.getMessage());
            }
        }, executor);
    }

    public GenerativeModelFutures getModel() {
        String apiKey = BuildConfig.apiKey;
        SafetySetting safeFromHarassment = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE);
        GenerationConfig.Builder configBilder = new GenerationConfig.Builder();
        configBilder.temperature = 0.9f;
        configBilder.topK = 16;
        configBilder.topP = 0.1f;
        GenerationConfig generationConfig = configBilder.build();
        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash", apiKey, generationConfig, Collections.singletonList(safeFromHarassment));

        return GenerativeModelFutures.from(generativeModel);
    }
}
