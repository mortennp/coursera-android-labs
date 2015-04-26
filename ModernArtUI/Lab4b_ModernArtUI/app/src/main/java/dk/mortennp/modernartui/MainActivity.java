package dk.mortennp.modernartui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    //region Types

    private class OffsetableColor implements Serializable {

        private static final int COLOR_MAX = 255;
        private int r, g, b;

        OffsetableColor(Random rnd)
        {
            r = rnd.nextInt(COLOR_MAX + 1);
            g = rnd.nextInt(COLOR_MAX + 1);
            b = rnd.nextInt(COLOR_MAX + 1);
        }

        int offset(int rOffset, int gOffset, int bOffset)
        {
            return Color.argb(
                    COLOR_MAX,
                    offset(r,rOffset),
                    offset(g,gOffset),
                    offset(b,bOffset));
        }

        private int offset(int color, int offset)
        {
            return (color + offset) % COLOR_MAX;
        }
    }

    private class InstanceState implements Serializable
    {
        private static final String KEY = "MAIN_ACTIVITY";
        private OffsetableColor oc1, oc2, oc4, oc5;
        private int offset;

        InstanceState(Random rnd)
        {
            oc1 = new OffsetableColor(rnd);
            oc2 = new OffsetableColor(rnd);
            oc4 = new OffsetableColor(rnd);
            oc5 = new OffsetableColor(rnd);
        }

        OffsetableColor getOc1() { return oc1; }

        OffsetableColor getOc2() { return oc2; }

        OffsetableColor getOc4() { return oc4; }

        OffsetableColor getOc5() { return oc5; }

        int getOffset() { return offset; }

        void setOffset(int value) { offset = value; }
    }

    //endregion

    //region Fields

    static private final String URL = "http://www.moma.org";

    InstanceState instanceState;

    private TextView tv1;
    private TextView tv2;
    private TextView tv4;
    private TextView tv5;
    //endregion

    //region Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Find widgets
        tv1 = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                instanceState.setOffset(progress);
                setColors();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            instanceState = (InstanceState) savedInstanceState.getSerializable(InstanceState.KEY);
        } else {
            Random rnd = new Random();
            instanceState = new InstanceState(rnd);
        }
        setColors();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable(InstanceState.KEY, instanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setColors() {
        int offset = instanceState.getOffset();
        tv1.setBackgroundColor(instanceState.getOc1().offset(offset, 0, 0));
        tv2.setBackgroundColor(instanceState.getOc2().offset(0, offset, 0));
        tv4.setBackgroundColor(instanceState.getOc4().offset(0, 0, offset));
        tv5.setBackgroundColor(instanceState.getOc5().offset(offset, offset, offset));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more_info:
                popDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void popDialog()
    {
        new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle("Inspired by pret aporter!")
                .setMessage("Click below to learn more?")
                .setPositiveButton("Visit MOMA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        browseMoma();
                    }
                })
                .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void browseMoma()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(intent);
    }

    //endregion
}
