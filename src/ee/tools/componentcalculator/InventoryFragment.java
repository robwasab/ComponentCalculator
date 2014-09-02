package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentView;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Component;
import ee.tools.model.Components;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;

public class InventoryFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
	
	InventoryListAdapter component_adapter;
	
	LinkedList<ComponentViewInterface> resistor_views, capacitor_views;

	private SchematicFragment schematic_fragment;

	private CalculatorFragment calculator_fragment;
	
	ListView list_view;
	
	Activity current_activity;
	Context current_context;
	
	RadioGroup radio_group;

	private InventoryListAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		current_activity = this.getActivity();
		
		current_context = this.current_activity;
		
		View rootView = inflater.inflate(R.layout.inventory_fragment, container, false);
		
		list_view = (ListView) rootView.findViewById(R.id.inventory_list_view);
		
		radio_group = (RadioGroup) rootView.findViewById(R.id.inventory_radio_group);
		
		radio_group.setOnCheckedChangeListener(this);
		
		LinkedList<Component> res_values = new LinkedList<Component>();
		
		res_values.add(new Component(10));

		res_values.add(new Component(47));

		for (double i = 1; i <= 10; i += 1)
		{
			Component c = new Component((int)(i * 100));
			res_values.add(c);
		}
		for (double i = 1; i <= 5; i += 1)
		{
			Component c = new Component((int)(i * 2000));
			res_values.add(c);
		}
		resistor_views = ComponentViewFactory.makeViews(ComponentsView.RESISTOR, res_values);
		
		LinkedList<Component> cap_values = new LinkedList<Component>();
		
		for (double i = 1; i <= 10; i += 1)
		{
			Component c = new Component(i * 1E-6);
			cap_values.add(c);
		}
		capacitor_views = ComponentViewFactory.makeViews(ComponentsView.CAPACITOR, cap_values);
		
		adapter = new InventoryListAdapter(this.getActivity(), 
											resistor_views, capacitor_views);
		
		list_view.setAdapter(adapter);
			
		return rootView;
	}
	
	public LinkedList<Component> getComponents(int type)
	{
		LinkedList<Component> ret = new LinkedList<Component>();
		LinkedList<ComponentViewInterface> source = null;
		
		if (type == Components.RESISTOR) source = resistor_views;
		else if (type == Components.CAPACITOR) source = capacitor_views;
		else return null;
		
		for (int i = 0; i < source.size(); i++)
		{
			if (source.get(i) instanceof Component)
			{
				//ret.add((Component)views.get(i));
				ret.add(new Component(((Component)source.get(i)).getValue()));
			}
		}
		return ret;
	}
	
	public void setFragments(SchematicFragment sF, CalculatorFragment cF)
	{
		this.schematic_fragment = sF;
		this.calculator_fragment = cF;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId)
		{
		case R.id.inventory_radio_button_resistor:
			adapter.setMode(Components.RESISTOR);
			break;
		case R.id.inventory_radio_button_capacitor:
			adapter.setMode(Components.CAPACITOR);
			break;
		}
	}	
}
