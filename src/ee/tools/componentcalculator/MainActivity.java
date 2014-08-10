package ee.tools.componentcalculator;

import java.util.LinkedList;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends FragmentActivity{

	SchematicFragment base;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null)
		{
			base = new SchematicFragment();
			fm.beginTransaction()
			.replace(R.id.main_content, base).commit();		
		}
		else
		{
			FragmentManager man = this.getSupportFragmentManager();
			base = (SchematicFragment) man.getFragment(savedInstanceState, "base_fragment");
		}
	}
	
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		base.onSaveInstanceState(state);
		FragmentManager man = this.getSupportFragmentManager();
		man.putFragment(state, "base_fragment", base);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {
			return true;
		}
		
		else if (id == android.R.id.home)
		{
			return base.backPressedAction();
		}
		
    	return super.onOptionsItemSelected(item);
	}
}
