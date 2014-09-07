package ee.tools.componentcalculator;


import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.model.Component;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class InventoryListItemView extends RelativeLayout 
implements OnCheckedChangeListener, OnLongClickListener{

	 	private LinearLayout layout;
	 	
		protected LinearLayout schematic_layout;
		
		private CheckBox check_box;
		
		private NumberIncrementorView incrementor;
		
		//The adapter that this view belongs to, if there is one...
		public BaseAdapter adapter = null;
		
		ComponentViewInterface component_view;
		Schematic schematic;
		boolean checked = false;
		private String tag = "InventoryListItemView";
		
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
			
			layout = (LinearLayout)this.findViewById(R.id.inventory_list_item_layout);
			
			layout.setOnLongClickListener(this);
			
			check_box = (CheckBox) this.findViewById(R.id.inventory_check_box);
			
			check_box.setOnCheckedChangeListener(this);
			
			schematic_layout  = (LinearLayout) this.findViewById(R.id.inventory_schematic_holder);
			
			LinearLayout number_incrementor_holder
				= (LinearLayout) this.findViewById(R.id.number_incrementor_holder);
			
			incrementor = new NumberIncrementorView(context)
			{
				public void onChange(int val)
				{
					if (val == 0) check_box.setChecked(false);
					else check_box.setChecked(true);
				}
			};
			
			incrementor.setLayoutParams(params);
			
			number_incrementor_holder.addView(incrementor);
			
			if (this.component_view instanceof Component)
			{
				Component c = (Component)this.component_view;
				
				incrementor.setComponent(c);
			}		
			
			schematic = new Schematic(context, null);
			
			schematic.setSeries(component_view);
						
			int set_width = (int)component_view.getWidth();
			int set_height = (int)component_view.getHeight();
			
			android.widget.LinearLayout.LayoutParams s_params = new android.widget.LinearLayout.LayoutParams
					(set_width, set_height);
		
			s_params.gravity = android.view.Gravity.LEFT;
			
			schematic.setLayoutParams(s_params);
			
			schematic.setEnabled(false);
		
			schematic_layout.addView(schematic);
			
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
			
			if (this.component_view instanceof Component)
			{
				Component comp = (Component)this.component_view;
				this.incrementor.setComponent(comp);
			}		
			
			int set_width = (int)component_view.getWidth();
			int set_height = (int)component_view.getHeight();
			
			int schematic_width = schematic.getWidth();
			int schematic_height = schematic.getHeight();
			
			int width_margin = Math.abs(schematic_width-set_width);
			int height_margin = Math.abs(schematic_height-set_height);
			
			if (5 < width_margin || 5 < height_margin)
			{
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams
						(set_width, set_height);
			
				params.gravity = android.view.Gravity.LEFT;
				
				schematic.setLayoutParams(params);
			}
			
			invalidate();
		}
		
		@Override
		public void invalidate()
		{
			super.invalidate();
			this.schematic.invalidate();
			if (this.adapter != null) this.adapter.notifyDataSetChanged();
		}
		
		public String toString()
		{
			return component_view.toString();
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (this.incrementor.getValue() == 0) buttonView.setChecked(false);
			else if (this.incrementor.getValue() == -1) buttonView.setChecked(true);
		}

		@Override
		public boolean onLongClick(View v) {
			android.os.Vibrator vibrator = (Vibrator) this.getContext().getSystemService(this.getContext().VIBRATOR_SERVICE);
			vibrator.vibrate(50);
			schematic_layout.removeAllViews();
			InventoryDialog dialog = new InventoryDialog(this.getContext(),schematic){
				public void dismiss()
				{
					super.dismiss();
					schematic_layout.addView(schematic);
					
				}
			};
			dialog.show();
			return true;
		}
		
		class InventoryDialog extends Dialog implements OnClickListener
		{
			LinearLayout schematic_holder;
			Schematic schematic;
			Button delete;
			LinearLayout accessory_holder;
			
			public InventoryDialog(Context context, Schematic schematic) {
				super(context);
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				this.schematic = schematic;
				((LinearLayout.LayoutParams) this.schematic.getLayoutParams()).gravity = android.view.Gravity.CENTER;
				this.setContentView(R.layout.inventory_list_item_dialog);
				this.setTitle("Settings");
				delete = (Button) this.findViewById(R.id.inventory_list_item_dialog_button_delete);
				delete.setOnClickListener(this);
				schematic_holder = (LinearLayout) this.findViewById(R.id.inventory_list_item_dialog_schematic_holder);
				schematic_holder.addView(schematic);
				
				accessory_holder = (LinearLayout) this.findViewById(R.id.inventory_list_item_dialog_accessory_holder);
				
				ComponentViewInterface cvi = schematic.getCurrent();
				if (cvi != null)
				{
					Object accessory = cvi.getAccessory(schematic);
					if (accessory instanceof View)
					{
						accessory_holder.addView((View)accessory);
					}
				}
			}
			
			public void hideDeleteButton(boolean hide)
			{
				if (hide)
					delete.setVisibility(Button.INVISIBLE);
				else
					delete.setVisibility(Button.VISIBLE);
			}
			
			public void onClick(View v)
			{
				if (adapter.getClass() == InventoryListAdapter.class)
				{
					List<ComponentViewInterface> comps
						= ((InventoryListAdapter)adapter).current_components;
					
					for (int i = 0; i < comps.size(); i++) 
					{
						if (comps.get(i) == schematic.getCurrent())
						{
							comps.remove(i);
							adapter.notifyDataSetChanged();
							break;
						}
					}
					this.dismiss();
				}
			}
			
			public void dismiss()
			{
				super.dismiss();
				((LinearLayout.LayoutParams) this.schematic.getLayoutParams()).gravity = android.view.Gravity.LEFT;
				schematic_holder.removeAllViews();
				
				//sort
				if (adapter.getClass() == InventoryListAdapter.class)
				{
					List<ComponentViewInterface> comps
						= ((InventoryListAdapter)adapter).current_components;
					
					Collections.sort(comps, new Comparator<ComponentViewInterface>(){

						@Override
						public int compare(ComponentViewInterface lhs,
								ComponentViewInterface rhs) {
							return lhs.compareTo((Component)rhs);
						}
					});
				}

			}
			
		}
}
