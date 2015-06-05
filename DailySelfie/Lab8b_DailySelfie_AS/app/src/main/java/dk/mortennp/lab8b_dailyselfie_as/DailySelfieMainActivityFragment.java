package dk.mortennp.lab8b_dailyselfie_as;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;

public class DailySelfieMainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int URL_LOADER = 0;
    static final int THUMB_PATH_TAG = 0;

    private SimpleCursorAdapter mAdapter;

    public DailySelfieMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View localView = inflater.inflate(R.layout.fragment_daily_selfie_main, container, false);

        String[] from = {MediaStore.MediaColumns.TITLE, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.DATA};
        int[] to = {R.id.filename, R.id.thumbView, R.id.fullpath};
        mAdapter = new SimpleCursorAdapter(
                localView.getContext(),
                R.layout.row_daily_selfie_main, // android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()  {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.thumbView) {
                    String thumbPath = cursor.getString(columnIndex);
                    Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
                    ((ImageView) view).setImageBitmap(bitmap);
                    return true;
                } else if (view.getId() == R.id.filename) {
                    String title = cursor.getString(columnIndex);
                    ((TextView) view).setText(title);
                    return true;
                }
                else if (view.getId() == R.id.fullpath)
                {
                    String fullPath = cursor.getString(columnIndex);
                    view.setTag(fullPath);
                    return true;
                }
                return false;
            }
        });

        ListView lv = (ListView) localView.findViewById(R.id.listView);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object tag = view.findViewById(R.id.fullpath).getTag();
                if (null != tag) {
                    String fullPath = (String) tag;
                    Intent fullscreen =  new Intent(getActivity(), DailySelfieFullsceenActivity.class);
                    fullscreen.putExtra("fullPath", fullPath); //Optional parameters
                    startActivity(fullscreen);
                }
            }
        });

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }

        return localView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle)
    {
    /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case URL_LOADER:
                return new CursorLoader(
                    getActivity(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.TITLE);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}