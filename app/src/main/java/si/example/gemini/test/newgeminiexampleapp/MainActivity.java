package si.example.gemini.test.newgeminiexampleapp;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView sendQuery, logo, appIcon;
    FloatingActionButton btnShowDialog;
    private ProgressBar progressBar;
    private LinearLayout chatResponse;
    private ChatFutures chatModel;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText = findViewById(R.id.edit_text);
        progressBar = findViewById(R.id.progressBar);
        chatResponse = findViewById(R.id.geminiResponse);
        appIcon = findViewById(R.id.geminiImg);

        sendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString();
                if (!query.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    chatResponse.setVisibility(View.GONE);
                    editText.setText("");

                    chatBody("me", query, getDrawable(R.drawable.user_icon));

                    Gemini.getResponse(chatModel, query, new ResponseCallBack() {
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
            }
        });
    }

    private void chatBody(String me, String query, Drawable drawable) {
    }
}