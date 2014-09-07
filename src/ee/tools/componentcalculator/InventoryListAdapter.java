package ee.tools.componentcalculator;

import java.util.LinkedList;
import java.util.List;

import ee.tools.componentcalculator.InventoryListItemView.InventoryDialog;
import ee.tools.componentcalculator.components_toolbox.Body;
import ee.tools.componentcalculator.components_toolbox.CapacitorBody;
import ee.tools.componentcalculator.components_toolbox.ComponentView;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ResistorBody;
import ee.tools.componentcalculator.components_toolbox.ResistorException;
import ee.tools.model.Components;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class InventoryListAdapter extends BaseAdapter {

	private Context context;
	private List<ComponentViewInterface> resistor_components;
	private List<ComponentViewInterface> capacitor_components;
	public List<ComponentViewInterface> current_components;
	
	private MediaPlayer button_sound_player;
	private int type;
	
	public InventoryListAdapter(Context context, 
			List<ComponentViewInterface> resistor_components,
				List<ComponentViewInterface> capacitor_components) 
	{
		super();
		this.context = context;
		this.resistor_components = resistor_components;
		this.capacitor_components = capacitor_components;
		this.current_components = resistor_components;
		this.type = Components.RESISTOR;
		this.button_sound_player = MediaPlayer.create(context, R.raw.button_sound);
	}
	
	public void setMode(int res_or_cap)
	{
		if (res_or_cap == Components.RESISTOR)
		{
			this.type = res_or_cap;
			this.current_components = resistor_components;
			this.notifyDataSetChanged();
		}
		else if (res_or_cap == Components.CAPACITOR)
		{
			this.type = res_or_cap;
			this.current_components = capacitor_components;
			this.notifyDataSetChanged();
		}
	}
	
	public void addComponent()
	{
		Body b = null;
		LinkedList<Integer> serial = new LinkedList<Integer>();
		serial.add(0);
		int tolerance = 0;
		double value = 0;
		
		if (type == Components.RESISTOR)
		{
			value = 123;
			try { b = new ResistorBody(serial, 123, tolerance); }
			catch(ResistorException rbe) {}
		}
		else if (type == Components.CAPACITOR)
		{
			value = 123E-6;
			b = new CapacitorBody(serial);
		}
		if (b != null)
		{
			ComponentView cv = new ComponentView(serial, b, value, 0);
			current_components.add(cv);
		
			/*HACK:
			 * Override override override...
			 */
			InventoryListItemView temp = new InventoryListItemView(context, cv){
				class NewDialog extends InventoryDialog
				{
					public NewDialog(Context context, Schematic schematic) {
						super(context, schematic);
						this.hideDeleteButton(true);
					}				
					public void dismiss()
					{
						super.dismiss();
						adapter.notifyDataSetChanged();
					}

				}
				public boolean onLongClick(View v)
				{
					this.schematic_layout.removeAllViews();
					NewDialog new_dialog = new NewDialog(this.getContext(), this.schematic);
					new_dialog.show();
					return true;
				}
			};
			temp.setAdapter(this);
			temp.onLongClick(null);
		}
		
	}
	
	@Override
	public int getCount() {
		return current_components.size();
	}

	@Override
	public Object getItem(int position) {
		return current_components.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			InventoryListItemView item = new InventoryListItemView(context, this.current_components.get(position));
			item.setAdapter(this);
			item.setMediaPlayer(button_sound_player);
			return item;
		}
		else
		{
			if (convertView.getClass() == InventoryListItemView.class)
			{
				InventoryListItemView edit_view = (InventoryListItemView) convertView;
				edit_view.setComponentViewInterface(this.current_components.get(position));
				return edit_view;
			}
			return convertView;
		}
	}	
}
