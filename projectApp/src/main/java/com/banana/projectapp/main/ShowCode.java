package com.banana.projectapp.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.R;

public class ShowCode extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_code);
        TextView text = (TextView) findViewById(R.id.code_text);

        Bundle b = getIntent().getExtras();
        String calling_activity = b.getString("calling_activity");

        if (calling_activity.equals("geo"))
            text.setText(getString(R.string.geo_code));
        else if (calling_activity.equals("shopping"))
            text.setText(getString(R.string.shop_code));
        text.invalidate();

        TextView codeView = (TextView)findViewById(R.id.code);
        codeView.setText(DataHolder.getCode());
        codeView.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_show_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
