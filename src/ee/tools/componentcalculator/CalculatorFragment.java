package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Approximator;
import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

public class CalculatorFragment extends Fragment implements OnKeyListener,
CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener,
OnClickListener{
	
	EditText find_value;
	
	double double_value;
	
	Button find_combinations;
	
	RadioGroup component_choice;
	
	CheckBox checkbox;
	
	Activity current_activity;
	
	Context current_context;

	boolean use_qnty = false;
	
	int component_type = Components.RESISTOR;
	
	private String tag = "CalculatorFragment";
	
	private InventoryFragment inventory_fragment;
	
	private SchematicFragment schematic_fragment;
	
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
		
		find_combinations = (Button) rootView.findViewById(R.id.calculator_button_find_combinations);
		
		init_listeners();
		
		return rootView;
	}

	private void init_listeners()
	{
		find_value.setOnKeyListener(this);
		
		checkbox.setOnCheckedChangeListener(this);
		
		find_combinations.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		if (schematic_fragment == null)		
		{
			Log.d(tag, "SchematicFragment is NULL...");
			return;
		}
		
		Schematic schematic = schematic_fragment.schematic;
		
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
		
		LinkedList<Component> inventory = inventory_fragment.getComponents();
		
		Components combo = Approximator.approximate(inventory, new Component(this.double_value));
		
		if (combo == null)
		{
			Log.d(tag, "Approximator returned null, combination not possible...");
			return;
		}
		
		Log.d(tag, combo.toString());
		
		LinkedList<Integer> base_serial = new LinkedList<Integer>();
		
		base_serial.add(0);
		
		ComponentsView first_comp = new ComponentsView(
				schematic,
				base_serial,
				combo.components,
				combo.operation,
				Components.RESISTOR);
		
		schematic.setSeries(first_comp);
		
	}
}
