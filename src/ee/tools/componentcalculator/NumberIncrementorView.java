package ee.tools.componentcalculator;

import ee.tools.model.Component;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberIncrementorView extends LinearLayout{
	
	ImageButton decrement, increment;
	TextView number;
	int val = 0;
	Component component;
	Context context;
	
	public NumberIncrementorView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public NumberIncrementorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public NumberIncrementorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	public void init()
	{
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(
				  Context.LAYOUT_INFLATER_SERVICE);
		
		ViewGroup root = this;
		
		boolean attachToRoot = true;
		
		inflater.inflate(R.layout.number_incrementor, root, attachToRoot);
		
		decrement = (ImageButton)   this.findViewById(R.id.image_button_decrement);
		increment = (ImageButton)   this.findViewById(R.id.image_button_increment);
		number    = (TextView) this.findViewById(R.id.text_view_number);
		
		number.setText(Integer.toString(val));
		
		decrement.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				if (val <= 0) return;
				
				val--;
				number.setText(Integer.toString(val));
				if (component != null) component.setQnty(val);
				if (val <= 0)
				{
					decrement.setVisibility(INVISIBLE);
				}
			}
		});
		increment.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				val++;
				number.setText(Integer.toString(val));
				if (component != null) component.setQnty(val);
				if (0 < val) 
				{
					decrement.setVisibility(VISIBLE);
				}
			}
		});

	}
	public void setComponent(Component c)
	{
		this.component = c;
		this.setValue(c.getQnty());
	}
	
	public int getValue() { return val; }
	
	public void setValue(int val)
	{
		if (val < 0)
			return;
		
		this.val = val; 
		number.setText(Integer.toString(val));
		
		if (val <= 0)
		{
			decrement.setVisibility(INVISIBLE);
		}
		else
		{
			decrement.setVisibility(VISIBLE);
		}
	}
}
