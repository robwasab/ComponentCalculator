package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Approximator;
import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CalculatorFragment extends Fragment implements OnKeyListener,
CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener,
OnClickListener{
	
	Thread approximator_thread;
	
	EditText find_value;
	
	static final int MAX_PROGRESS = 20, MIN_PROGRESS = 1;
	
	double double_value = -1;
	
	double percent_error_value;
	
	Button find_combinations;
	
	RadioGroup component_choice;
	
	CheckBox checkbox;
	
	RadioGroup prefer_choice;
	
	SeekBar percent_error_bar;
	
	Activity current_activity;
	
	Context current_context;

	boolean use_qnty = false;
	
	int component_type = Components.RESISTOR;
	
	private String tag = "CalculatorFragment";
	
	private InventoryFragment inventory_fragment;
	
	private SchematicFragment schematic_fragment;
		
	TextView message;
	
	TextView percent_error;
	
	TextView numerical_error;
	
	ProgressBar progress_bar;
	
	Button kill_button;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		current_activity = this.getActivity();
		
		current_context = this.current_activity;
		
		View rootView = inflater.inflate(R.layout.calculator_fragment, container, false);
		
		find_value = (EditText) rootView.findViewById(R.id.calculator_edit_text_find_value);
		
		component_choice = (RadioGroup) rootView.findViewById(R.id.calculator_radio_group);
		
		component_choice.setOnCheckedChangeListener( this);
		
		checkbox = (CheckBox) rootView.findViewById(R.id.calculator_check_box_qnty);
		
		checkbox.setChecked(this.use_qnty);
		
		prefer_choice = (RadioGroup) rootView.findViewById(R.id.calculator_radio_group_prefer_settings);
		
		prefer_choice.setOnCheckedChangeListener(this);
		
		percent_error = (TextView) rootView.findViewById(R.id.calculator_text_view_precision);
		
		numerical_error = (TextView) rootView.findViewById(R.id.calculator_text_view_precision_immediate_value);
		
		percent_error_bar = (SeekBar) rootView.findViewById(R.id.calculator_seek_bar_precision);
		
		percent_error_bar.setMax(MAX_PROGRESS);
		
		percent_error_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{	
				//seekBar.setProgress(MIN_PROGRESS);
				refresh();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
		});
			
		percent_error.setText("");
		
		numerical_error.setText("");
		
		find_combinations = (Button) rootView.findViewById(R.id.calculator_button_find_combinations);
		
		message = (TextView) rootView.findViewById(R.id.calculator_text_view_console);
		
		progress_bar = (ProgressBar) rootView.findViewById(R.id.calculator_progress_bar);
		
		progress_bar.setIndeterminate(false);
		
		progress_bar.setVisibility(ProgressBar.INVISIBLE);
		
		kill_button = (Button) rootView.findViewById(R.id.calculator_button_kill);
		
		kill_button.setVisibility(Button.INVISIBLE);
		
		kill_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (approximator_thread != null)
				{
					approximator_thread.interrupt();
				}
			}
			
		});
		
		init_listeners();
		
		refresh();
		
		return rootView;
	}

	private void init_listeners()
	{
		find_value.setOnKeyListener(this);
		
		checkbox.setOnCheckedChangeListener(this);
		
		find_combinations.setOnClickListener(this);
	}

	private void refresh()
	{
		if (0.0 <= double_value)
			update_percent_error(this.percent_error_bar.getProgress(), double_value);
		else
		{
			percent_error.setText("Enter Desired Value");
			numerical_error.setText("");
		}
	}
	
	private void update_percent_error(double percent_error, double value)
	{
		String unit;
		if (this.component_type == Components.RESISTOR)
		{
			unit = "½";
		}
		else if (this.component_type == Components.CAPACITOR)
		{
			unit = "F";
		}
		else unit = "";
		
		if (percent_error < 1.0) percent_error = 0.5;
		
		double numerical = value * percent_error / 100.0;
		numerical *= 1000.0;
		numerical = Math.round(numerical);
		numerical /= 1000.0;
		this.numerical_error.setText(Double.toString(numerical) + unit);
		
		percent_error *= 1000.0;
		percent_error = Math.round(percent_error);
		percent_error /= 1000.0;
		
		this.percent_error.setText(Double.toString(percent_error) + "%");
	}
	
	public void setFragments(SchematicFragment sF, InventoryFragment iF)
	{
		this.schematic_fragment = sF;
		this.inventory_fragment = iF;
	}
		
	@Override
	public boolean onKey(View v, int new_key, KeyEvent event) {
		if (new_key != 66) return false;
		if (event.getAction() == KeyEvent.ACTION_UP)
		{
			read_text_view();
		    this.close_key_board(find_value);
		    refresh();
		    return true;
		}
		return false;
	}
	
	private boolean read_text_view()
	{
	  String value = find_value.getText().toString();
	  try
	   {
		   Double d = Double.valueOf(value);
		   double_value = d.doubleValue();
		   Log.d(tag, "FIND THE VALUE: " + d.toString());
		   return true;
	   }
	   catch (Exception e)
	   {
		   find_value.setText("Invalid Entry");
		   double_value = -1;
		   return false;
	   }
	}
	
	private void close_key_board(EditText edit_text)
	{
		InputMethodManager imm = (InputMethodManager) current_activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.use_qnty = isChecked;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == this.component_choice)
		{
			switch(checkedId)
			{
			case R.id.calculator_radio_button_resistor:
				component_type = Components.RESISTOR;
				Log.d(tag, "Resistor chosen...");
				break;
			case R.id.calculator_radio_button_capacitor:
				component_type = Components.CAPACITOR;
				Log.d(tag, "Capacitor chosen...");
				break;
			}
		}
		else if (group == this.prefer_choice)
		{
			switch(checkedId)
			{
			case R.id.calculator_radio_button_prefer_shorter:
				Approximator.preference = Approximator.PREFER_SHORTER;
				Log.d(tag, "Prefer Shorter...");
				break;
			case R.id.calculator_radio_button_prefer_accuracy:
				Approximator.preference = Approximator.PREFER_ACCURACY;
				Log.d(tag, "Prefer Accuracy...");
				break;
			}
		}
		refresh();
	}

	@Override
	public void onClick(View v) {
		if (schematic_fragment == null)		
		{
			Log.d(tag, "SchematicFragment is NULL...");
			return;
		}
		
		final Schematic schematic = schematic_fragment.schematic;
		
		if (schematic_fragment == null)		
		{
			Log.d(tag, "Schematic is NULL...");
			return;
		}
		
		if (inventory_fragment == null)
		{
			Log.d(tag, "InventoryFragment is NULL...");
			return;
		}
		
		if (!read_text_view()) return;
		
		this.close_key_board(this.find_value);
		
		refresh();
		
		message.setText("");
				
		final LinkedList<Component> inventory = inventory_fragment.getComponents(component_type);
		
		final double percent_error = percent_error_bar.getProgress();

		final double find_this_value = this.double_value;
		
		final Handler post_handler = new Handler();
		
		progress_bar.setIndeterminate(true);
		
		progress_bar.setVisibility(ProgressBar.VISIBLE);
		
		kill_button.setVisibility(Button.VISIBLE);
		
		approximator_thread = new Thread( new Runnable()
		{
			@Override
			public void run() 
			{
				
				Components combo;
				
				combo = Approximator.approximate
						(inventory, new Component(find_this_value), percent_error);
				
				final Components combo_copy = combo;
				
				post_handler.post(new Runnable()
				{
					public void run()
					{
						progress_bar.setIndeterminate(false);
						
						progress_bar.setVisibility(ProgressBar.INVISIBLE);
						
						kill_button.setVisibility(Button.INVISIBLE);
						
						if (combo_copy == null)
						{
							message.setText(Approximator.last_message);
							Log.d(tag, "Approximator returned null, combination not possible...");
							return;
						}
					
						Log.d(tag, combo_copy.toString());
						
						LinkedList<Integer> base_serial = new LinkedList<Integer>();
					
						base_serial.add(0);
					
						ComponentsView first_comp = new ComponentsView(
								schematic,
								base_serial,
								combo_copy.components,
								combo_copy.operation,
								component_type);
						
						schematic.setSeries(first_comp);
						
						schematic.invalidate();
					}
				});
			}
		});
		approximator_thread.start();
	}	
}
