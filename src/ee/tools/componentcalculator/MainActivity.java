package ee.tools.componentcalculator;

import java.util.LinkedList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
, ViewPager.OnPageChangeListener{

	SchematicFragment schematic;
	InventoryFragment inventory;
	ViewPager pager;
	
	String[] tab_names = {"Schematic", "Inventory" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		pager = (ViewPager) this.findViewById(R.id.pager);
		
		TabPagerAdapter tab_adapter = new TabPagerAdapter(getSupportFragmentManager());
		
		if (savedInstanceState == null)
		{
			schematic = new SchematicFragment();
			inventory = new InventoryFragment();
		}
		else
		{
			FragmentManager man = this.getSupportFragmentManager();
			schematic = (SchematicFragment) man.getFragment(savedInstanceState, "schematic_fragment");
			inventory = (InventoryFragment) man.getFragment(savedInstanceState, "inventory_fragment");
		}
		
		pager.setAdapter(tab_adapter);
		
		this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for (String tab_name : tab_names)
		{
			this.getActionBar().addTab(getActionBar().newTab().setText(tab_name).setTabListener(this));
		}
		
		pager.setOnPageChangeListener(this);		
	}
	
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		schematic.onSaveInstanceState(state);
		inventory.onSaveInstanceState(state);
		FragmentManager man = this.getSupportFragmentManager();
		man.putFragment(state, "schematic_fragment", schematic);
		man.putFragment(state, "inventory_fragment", inventory);
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
			return schematic.backPressedAction();
		}
		
    	return super.onOptionsItemSelected(item);
	}

	class TabPagerAdapter extends FragmentPagerAdapter
	{

		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int index) {
		 
			switch (index) {
				case 0:
				return schematic;
		        
				case 1:
				// Games fragment activity
				return inventory;
			}
			return null;
		}
		 
		@Override
		public int getCount() {
			return 2;
		}
	}		
	
	/*
	 * ActionBar.TabListener Implemenation
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		this.pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	/*
	 * OnPageChangeListener Implementation!
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int arg0) {
		getActionBar().setSelectedNavigationItem(arg0);
	}
}
