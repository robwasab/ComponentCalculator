package ee.tools.componentcalculator;

import java.util.List;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class InventoryListAdapter extends BaseAdapter {

	private Context context;
	private List<ComponentViewInterface> components;
	
	public InventoryListAdapter(Context context, List<ComponentViewInterface> components) {
		super();
		this.context = context;
		this.components = components;
	}
	
	@Override
	public int getCount() {
		return components.size();
	}

	@Override
	public Object getItem(int position) {
		return components.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			InventoryListItemView item = new InventoryListItemView(context, this.components.get(position));
			item.setAdapter(this);
			return item;
		}
		else
		{
			if (convertView.getClass() == InventoryListItemView.class)
			{
				InventoryListItemView edit_view = (InventoryListItemView) convertView;
				edit_view.setComponentViewInterface(this.components.get(position));
				return edit_view;
			}
			return convertView;
		}
	}	
}
