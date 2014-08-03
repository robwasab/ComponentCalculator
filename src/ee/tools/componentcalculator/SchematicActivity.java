package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.os.Bundle;

public class SchematicActivity extends Activity
{
	Context current_context;
	Activity current_activity;
	LinearLayout root_layout;
	String tag = "FragmentSchematic";

	LinearLayout settings;
	
	Schematic schematic;
	
	LinkedList<Integer> master_serial;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_schematic);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		current_context  = this;
		current_activity = this;
		
		root_layout = (LinearLayout) findViewById(R.id.schematic_layout);
		
		settings = new LinearLayout(current_context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		settings.setLayoutParams(params);
		
		master_serial = new LinkedList<Integer>();
		
		master_serial.add(0);
		
		schematic = new Schematic(current_context, settings);
		
		LinkedList<Component> small_series_ll = new LinkedList<Component>();
		
		small_series_ll.add(new Component(456));
		small_series_ll.add(new Component(789));
		
		ComponentsView small_series = new ComponentsView(
				schematic,
				master_serial,
				small_series_ll,
				Components.SUM,
				Components.RESISTOR);
		
		LinkedList<Component> parallel_ll = new LinkedList<Component>();
		
		parallel_ll.add(small_series);
		parallel_ll.add(new Component(1230000));
		parallel_ll.add(new Component(567000));
		
		ComponentsView parallel = new ComponentsView(
				schematic,
				master_serial,
				parallel_ll,
				Components.INVERSE_INVERSE_SUM,
				Components.RESISTOR);
		
		LinkedList<Component> s = new LinkedList<Component>();
		
		s.add(new Component(123));
		s.add(parallel);
		s.add(new Component(47000));
		s.add(new Component(1000));
		
		ComponentsView series = new ComponentsView(
				schematic,
				master_serial,
				s,
				Components.SUM,
				Components.RESISTOR);
	
		schematic.setSeries(series);
		
		root_layout.addView(settings);
		
		root_layout.addView(schematic);
		
		if (savedInstanceState != null)
		{
			schematic.restoreInstanceState(savedInstanceState);
		}
	}
	
	private void close_key_board(EditText edit_text)
	{
		InputMethodManager imm = (InputMethodManager) current_activity.getSystemService(current_activity.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
	}

	public void onSaveInstanceState(Bundle state)
	{
		schematic.saveInstanceState(state);
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
		return super.onOptionsItemSelected(item);
	}

}
