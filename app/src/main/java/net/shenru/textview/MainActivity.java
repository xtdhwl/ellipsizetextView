package net.shenru.textview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;

import net.shenru.mylibrary.EllipsizeTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void submitClick(View view) {
        EllipsizeTextView ellipsizeTextView = findViewById(R.id.contentView);
        EditText editText = findViewById(R.id.inputView);

        // SpannableString sp = new SpannableString("...全文");
        SpannableString sp = new SpannableString("...全文");
        sp.setSpan(new ForegroundColorSpan(Color.RED), 0, sp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        ellipsizeTextView.setText(editText.getText(), sp);
    }
}