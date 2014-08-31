package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentView;
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
	
	LinkedList<ComponentViewInterface> views;

	private SchematicFragment schematic_fragment;

	private CalculatorFragment calculator_fragment;
	
	@Override
	public void onActivityCreated(Bundle saved)
	{
		super.onActivityCreated(saved);
		
		LinkedList<Component> values = new LinkedList<Component>();
		
		values.add(new Component(10));

		values.add(new Component(47));

		for (double i = 1; i <= 10; i += 1)
		{
			Component c = new Component((int)(i * 100));
			values.add(c);
		}
		for (double i = 1; i <= 5; i += 1)
		{
			Component c = new Component((int)(i * 2000));
			values.add(c);
		}
		views = ComponentViewFactory.makeViews(ComponentsView.RESISTOR, values);
		
		InventoryListAdapter adapter = new InventoryListAdapter(this.getActivity(), views);
		
		this.setListAdapter(adapter);
		
		//this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		//this.registerForContextMenu(this.getListView());
	}
	
	public LinkedList<Component> getComponents()
	{
		LinkedList<Component> ret = new LinkedList<Component>();
		
		for (int i = 0; i < views.size(); i++)
		{
			if (views.get(i) instanceof Component)
			{
				//ret.add((Component)views.get(i));
				ret.add(new Component(((Component)views.get(i)).getValue()));
			}
		}
		return ret;
	}
	
	public void setFragments(SchematicFragment sF, CalculatorFragment cF)
	{
		this.schematic_fragment = sF;
		this.calculator_fragment = cF;
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
