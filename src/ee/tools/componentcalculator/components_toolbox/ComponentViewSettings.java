package ee.tools.componentcalculator.components_toolbox;

import ee.tools.componentcalculator.R;
import ee.tools.componentcalculator.Schematic;
import ee.tools.model.Component;
import ee.tools.model.Components;
import ee.tools.model.EngNot;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

public class ComponentViewSettings extends LinearLayout 
implements OnKeyListener, OnClickListener{

	Activity current_activity;
	Context  current_context;
	
	EditText value_entry;
	
	Component comp;
	
	Schematic call_back;
	
	Button[] color_buttons;
	Button kilo, mega;
	
	Button[] power_buttons;
	
	int type;
	
	public ComponentViewSettings(Schematic schematic, Component comp, int type) 
	{
		super(schematic.getContext());
		super.setOrientation(HORIZONTAL);
		
		this.type = type;
		
		LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		super.setLayoutParams(params);
		
		current_activity = (Activity)schematic.getContext();
		
		current_context  = schematic.getContext();
		
		this.call_back = schematic;
		
		LayoutInflater inflater = current_activity.getLayoutInflater();
		
		if (type == Components.RESISTOR)
		{
			inflater.inflate(R.layout.resistor_accessory, this, true);
		
			value_entry = (EditText) this.findViewById(R.id.resistor_accessory_edit_text);
			
			color_buttons = new Button[10];
		
			color_buttons[0] = (Button)this.findViewById(R.id.resistor_accessory_button_black);
			color_buttons[1] = (Button)this.findViewById(R.id.resistor_accessory_button_brown);
			color_buttons[2] = (Button)this.findViewById(R.id.resistor_accessory_button_red);
			color_buttons[3] = (Button)this.findViewById(R.id.resistor_accessory_button_orange);
			color_buttons[4] = (Button)this.findViewById(R.id.resistor_accessory_button_yellow);
			color_buttons[5] = (Button)this.findViewById(R.id.resistor_accessory_button_green);
			color_buttons[6] = (Button)this.findViewById(R.id.resistor_accessory_button_blue);
			color_buttons[7] = (Button)this.findViewById(R.id.resistor_accessory_button_violet);
			color_buttons[8] = (Button)this.findViewById(R.id.resistor_accessory_button_gray);
			color_buttons[9] = (Button)this.findViewById(R.id.resistor_accessory_button_white);
			kilo = (Button)this.findViewById(R.id.resistor_accessory_button_K);
			mega = (Button)this.findViewById(R.id.resistor_accessory_button_M);
		
			for (int i = 0; i < color_buttons.length; i++)
			{
				color_buttons[i].setOnClickListener(this);
			}
			kilo.setOnClickListener(this);
			mega.setOnClickListener(this);
		}
		else if (type == Components.CAPACITOR)
		{
			inflater.inflate(R.layout.capacitor_accessory, this, true);
			
			value_entry = (EditText) this.findViewById(R.id.capacitor_accessory_edit_text);
		
			power_buttons = new Button[4];
			power_buttons[0] = (Button)this.findViewById(R.id.capacitor_accessory_button_milli);
			power_buttons[1] = (Button)this.findViewById(R.id.capacitor_accessory_button_micro);
			power_buttons[2] = (Button)this.findViewById(R.id.capacitor_accessory_button_nano);
			power_buttons[3] = (Button)this.findViewById(R.id.capacitor_accessory_button_pico);
			for (int i = 0; i < power_buttons.length; i++)
			{
				power_buttons[i].setOnClickListener(this);
			}
		}
		
		this.comp = comp;
		
		value_entry.setEms(10);
		
		//value_entry.setInputType(0x00002002);
		
		String value = EngNot.toEngNotation(comp.getValue());
		
		value_entry.setText(value);
		
		value_entry.setOnKeyListener(this);
	}

	private void close_key_board(EditText edit_text)
	{
		InputMethodManager imm = (InputMethodManager) current_activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
	}

	@Override
	public boolean onKey(View v, int new_key, KeyEvent event) {
		//Log.d(v.toString() , "Key Code " + new_key);
		//Log.d(v.toString(), event.toString() + " " + event.getAction());
		
		//Log.d(tag, Integer.toString(new_key));
		if (new_key != 66) return false;
		if (event.getAction() == KeyEvent.ACTION_UP)
		{
		   this.close_key_board(value_entry);
		   String value = value_entry.getText().toString();
		   try
		   {
			   Double d = EngNot.convert(value);
			   comp.setValue(d);
			   call_back.invalidate();
		   }
		   catch (Exception e)
		   {
			   value_entry.setText("Invalid Entry");
		   }
		   return true;
		}
		
		return false;
	}

	@Override
	public void onClick(View v) 
	{
		int id = v.getId();

		if (this.type == Components.RESISTOR)
		{		
			for (int i = 0; i < color_buttons.length; i++)
			{
				if (color_buttons[i].getId() == id)
				{
					Editable text = this.value_entry.getText();
					text.append(Integer.toString(i));
					return;
				}
			}
		
			double multiply = 1.0;
			boolean keep_going = false;
		
			if (kilo.getId() == id)
			{
				multiply = 1000.0;
				keep_going = true;
			}
			else if (mega.getId() == id)
			{
				multiply = 1E6;
				keep_going = true;
			}
			if (!keep_going) return;
		
			String value = value_entry.getText().toString();
			Double d = null;
			try { 
				d = EngNot.convert(value);
				value_entry.setText(EngNot.toEngNotation(d * multiply));
			}
			catch (Exception e) { }
			return;
		}
		
		else if (this.type == Components.CAPACITOR)
		{
			for (int i = 0; i < power_buttons.length; i++)
			{
				if (power_buttons[i].getId() == id)
				{
					double multiply = Math.pow(10, (i+1) * -3);
					
					String value = value_entry.getText().toString();
					Double d = null;
					try { 
						d = EngNot.convert(value);
						value_entry.setText(EngNot.toEngNotation(d * multiply));
					}
					catch (Exception e) { }
					return;
				}
			}
		}
		return;
	}
}
