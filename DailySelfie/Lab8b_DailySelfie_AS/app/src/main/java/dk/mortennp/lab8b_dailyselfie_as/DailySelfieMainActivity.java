package dk.mortennp.lab8b_dailyselfie_as;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailySelfieMainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie_main);
    }

    private boolean isIntentAvailable(String action) {
        final Intent intent = new Intent(action);
        return intent.resolveActivity(getPackageManager()) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isIntentAvailable(MediaStore.ACTION_IMAGE_CAPTURE))
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_daily_selfie_main, menu);
        }
        return true;
    }

    public static File getAlbumDir() {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/dailyselfie/");

        if (storageDir != null) {
            if (! storageDir.mkdirs()) {
                if (! storageDir.exists()){
                    Log.v("Daily Selfie", "Failed to create album directory");
                    return null;
                }
            }
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get dir
        File storageDir = getAlbumDir();

        // Create file and return
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                File f = createImageFile();
                mImageUri = Uri.fromFile(f);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        else {
            Log.v(getString(R.string.app_name), "Image capture not supported on device.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(mImageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
        }
    }
}
