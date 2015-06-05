package dk.mortennp.lab8b_dailyselfie_as;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;


public class DailySelfieFullsceenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie_fullsceen);

        String thumbPath = getIntent().getStringExtra("fullPath");
        ImageView fullView = (ImageView) findViewById(R.id.fullView);
        Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
        fullView.setImageBitmap(bitmap);
    }

}
