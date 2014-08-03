package ee.tools.componentcalculator;

import ee.tools.model.Component;
import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class ComponentViewSettings extends LinearLayout implements OnKeyListener{

	Activity current_activity;
	Context  current_context;
	
	EditText value_entry;
	
	Component comp;
	
	Schematic call_back;
	
	public ComponentViewSettings(Schematic schematic, Component comp) 
	{
		super(schematic.getContext());
		super.setOrientation(HORIZONTAL);
		LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		super.setLayoutParams(params);
		
		current_activity = (Activity)schematic.getContext();
		current_context  = schematic.getContext();
		
		this.call_back = schematic;
		
		value_entry = new EditText(schematic.getContext());
		
		value_entry.setLayoutParams(params);
		
		this.addView(value_entry);
		
		this.comp = comp;
		
		value_entry.setEms(10);
		
		value_entry.setInputType(0x00002002);
		
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
			   Double d = Double.valueOf(value);
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

}
