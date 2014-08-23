package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Component;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class InventoryFragment extends ListFragment {
	
	InventoryListAdapter component_adapter;
	
	@Override
	public void onActivityCreated(Bundle saved)
	{
		super.onActivityCreated(saved);
		
		LinkedList<Component> values = new LinkedList<Component>();
		for (double i = 1.1; i <= 5.5; i += 0.1)
		{
			Component c = new Component((int)(i * 100));
			values.add(c);
		}
		LinkedList<ComponentViewInterface> views = ComponentViewFactory.makeViews(ComponentsView.RESISTOR, values);
		
		InventoryListAdapter adapter = new InventoryListAdapter(this.getActivity(), views);
		
		this.setListAdapter(adapter);
		
		//this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		//this.registerForContextMenu(this.getListView());
	}
	
	InventoryListItemView last_item = null;
	private String tag = "InventoryFragment";
	
	 @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	    String item_string = getListAdapter().getItem(position).toString();
	    Toast.makeText(this.getActivity(), item_string + " selected", Toast.LENGTH_LONG).show();
	    /*
	    	if (v.getClass() == InventoryListItemView.class)
		    {
	    		Log.d(tag, "Found InventoryListItemView");
	    		
		    	InventoryListItemView item = (InventoryListItemView) v;
		    	item.schematic.setEnabled(true);
		    	last_item = item;
		    }
		    if (last_item != null) last_item.schematic.setEnabled(false);
		*/
	   }
}
