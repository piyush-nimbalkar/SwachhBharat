package com.sbm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sbm.spotfixrequest.SpotfixRequestActivity;

public class MainActivity extends Activity {
	private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setActionBar(getActionBar());
    }

    public ActionBar getActionBar() {
		return actionBar;
	}
	public void setActionBar(ActionBar actionBar) {
		this.actionBar = actionBar;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
//	    switch (item.getItemId()) {
//	        case R.id.action_add_request:
//	        	Intent addRequestIntent = new Intent(this, SpotfixRequestActivity.class);
//	        	addRequestIntent.putExtra("key", "value"); //Optional parameters
//	        	startActivity(addRequestIntent);
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
		if (item.getItemId() == R.id.action_add_request) {
        	Intent addRequestIntent = new Intent(this, SpotfixRequestActivity.class);
        	addRequestIntent.putExtra("key", "value"); //Optional parameters
        	startActivity(addRequestIntent);
            return true;
		}
        return super.onOptionsItemSelected(item);
	}
}