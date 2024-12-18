package si.example.gemini.test.newgeminiexampleapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button sendQuery;
    private ImageButton addPhotoButton;
    private ImageView logo, appIcon;
    FloatingActionButton btnShowDialog;
    private ProgressBar progressBar;
    private LinearLayout chatResponse;
    private ChatFutures chatModel;
    private ImageView selectedImageView;
    private static final int SELECT_IMAGE_REQUEST_CODE = 1001;
    private Uri selectedImageUri;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sendQuery = findViewById(R.id.send_message);
        logo = findViewById(R.id.geminiImg);
        editText = findViewById(R.id.edit_text);
        progressBar = findViewById(R.id.progressBar);
        chatResponse = findViewById(R.id.geminiResponse);
        appIcon = findViewById(R.id.geminiImg);

        addPhotoButton = findViewById(R.id.add_photo);
        selectedImageView = findViewById(R.id.selectedImageView);
        addPhotoButton.setOnClickListener(v -> openGallery());

        chatModel = getChatModel();

        sendQuery.setOnClickListener(v -> {
            String query = editText.getText().toString();
            if (!query.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                chatResponse.setVisibility(View.GONE);
                editText.setText("");

                chatBody("me", query, getDrawable(R.drawable.user_icon));

                Gemini.getResponse(chatModel, query, selectedImage, new ResponseCallBack() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        chatResponse.setVisibility(View.VISIBLE);
                        chatBody("Gemini", response, getDrawable(R.drawable.gemini_icon));
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.GONE);
                        chatResponse.setVisibility(View.VISIBLE);
                        chatBody("Gemini", "Please try again. " + error, getDrawable(R.drawable.gemini_icon));
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    selectedImage = BitmapFactory.decodeStream(inputStream);
                    selectedImageView.setImageBitmap(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ChatFutures getChatModel() {
        Gemini model = new Gemini();
        GenerativeModelFutures modelFutures = model.getModel();
        return modelFutures.startChat();
    }

    private void chatBody(String you, String query, Drawable drawable) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.message, null);
        TextView name = view.findViewById(R.id.typer);
        TextView message = view.findViewById(R.id.cor_message);
        ImageView imageView = view.findViewById(R.id.userImg);

        name.setText(you);
        message.setText(query);
        imageView.setImageDrawable(drawable);

        chatResponse.addView(view);

        ScrollView scrollView = findViewById(R.id.scrollView2);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}
