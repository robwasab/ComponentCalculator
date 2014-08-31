package ee.tools.componentcalculator;


import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.model.Component;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InventoryListItemView extends RelativeLayout implements Checkable, OnCheckedChangeListener{

		private TextView text_view;
		private LinearLayout layout;
		
		private CheckBox check_box;
		
		private NumberIncrementorView incrementor;
		
		//The adapter that this view belongs to, if there is one...
		private BaseAdapter adapter = null;
		
		ComponentViewInterface component_view;
		Schematic schematic;
		boolean checked = false;
		private String tag = "InventoryListItemView";
		private int qnty;
		
		public InventoryListItemView(Context context, ComponentViewInterface component_view) 
		{
			// NOTE: Once you have made JokeView extend the appropriate ViewGroup
			// 		 un-comment this next line.
			super(context);
					
			this.component_view = component_view;
			
			LayoutParams params = new LayoutParams
					(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			//This class is the root view of our xml file joke_view.xml
			//Need to attach this instance of a relativelayout as the root view
			//of all the inflated widgets in joke_view.xml
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(
					  Context.LAYOUT_INFLATER_SERVICE);
			
			ViewGroup root = this;
			boolean attach_to_root = true;
			
			inflater.inflate(R.layout.inventory_list_item, root, attach_to_root);
			
			text_view = (TextView) this.findViewById(R.id.text_view);	
			
			text_view.setText(component_view.toString()); 
			
			check_box = (CheckBox) this.findViewById(R.id.inventory_check_box);
			
			check_box.setOnCheckedChangeListener(this);
			
			layout  = (LinearLayout) this.findViewById(R.id.inventory_schematic_holder);
			
			LinearLayout number_incrementor_holder
				= (LinearLayout) this.findViewById(R.id.number_incrementor_holder);
			
			incrementor = new NumberIncrementorView(context);
			
			incrementor.setLayoutParams(params);
			
			number_incrementor_holder.addView(incrementor);
			
			if (this.component_view instanceof Component)
			{
				Component c = (Component)this.component_view;
				
				incrementor.setComponent(c);
			}		
			
			schematic = new Schematic(context, layout);
			
			schematic.setSeries(component_view);
			
			schematic.setLayoutParams(params);
			
			int set_width = (int)component_view.getWidth();
			int set_height = (int)component_view.getHeight();
			
			schematic.setLayoutParams(new android.view.ViewGroup.LayoutParams(set_width, set_height));
			
			schematic.setEnabled(false);
		
			layout.addView(schematic);
			
		}
		
		public void setMediaPlayer(MediaPlayer button_sound_player)
		{
			this.incrementor.setMediaPlayer(button_sound_player);
		}
		
		public void setAdapter(BaseAdapter adapter)
		{
			this.adapter = adapter;
		}

		public void setComponentViewInterface(ComponentViewInterface cvi)
		{
			this.component_view = cvi;
			this.schematic.setSeries(cvi);
			this.text_view.setText(cvi.toString()); 
			
			if (this.component_view instanceof Component)
			{
				Component comp = (Component)this.component_view;
				this.incrementor.setComponent(comp);
			}		
	
			invalidate();
		}
		
		@Override
		public void invalidate()
		{
			super.invalidate();
			this.schematic.invalidate();
			this.text_view.invalidate();
			if (this.adapter != null) this.adapter.notifyDataSetChanged();
		}
		
		private void expandView() {
			text_view.setEllipsize(null);
			text_view.setMaxLines(Integer.MAX_VALUE);
			schematic.setVisibility(View.GONE);
			invalidate();
		}

		private void collapseView() {
			text_view.setLines(2);
			text_view.setEllipsize(TruncateAt.END);
			schematic.setVisibility(View.VISIBLE);
			invalidate();
		}

		@Override
		public void setChecked(boolean checked) {
			this.checked = checked;
			if (checked) expandView();
			else         collapseView();
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void toggle() {
			this.checked ^= true;
			setChecked(this.checked);
		}
		
		public String toString()
		{
			return component_view.toString();
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			
		}
}
