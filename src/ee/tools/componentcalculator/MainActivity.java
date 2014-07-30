package ee.tools.componentcalculator;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends Activity implements OnClickListener{

	Button start_drawing;
	Button start_schematic;
	int current_loaded_fragment;
	final int DRAWING = 1;
	final int SCHEMATIC = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		start_drawing   = (Button) this.findViewById(R.id.drawing_button);
		start_schematic = (Button) this.findViewById(R.id.schematic_button);
		
		start_drawing.setOnClickListener(this);
		start_schematic.setOnClickListener(this);
		
		if (savedInstanceState == null) 
		{
			getFragmentManager().beginTransaction()
					.add(R.id.main_content, new FragmentDrawing()).commit();		
			current_loaded_fragment = DRAWING;
		}
	}

	@Override
	public void onClick(View v) 
	{
		if (v.getClass() == Button.class)
		{
			CharSequence button_name = ((Button)v).getText();
			if (button_name.equals(this.getString(R.string.drawing_button_text)))
			{
				if (current_loaded_fragment != DRAWING)
				{
					getFragmentManager().beginTransaction()
						.replace(R.id.main_content, new FragmentDrawing()).commit();	
					
					current_loaded_fragment = DRAWING;
				}
			}
			
			else if (button_name.equals(this.getString(R.string.schematic_button_text)))
			{
				if (current_loaded_fragment != SCHEMATIC)
				{
					getFragmentManager().beginTransaction()
						.replace(R.id.main_content, new FragmentSchematic()).commit();	
					
					current_loaded_fragment = SCHEMATIC;
				}
			}
		}
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.placeholder_fragment, container,
					false);
			return rootView;
		}
	}

}
