package ee.tools.componentcalculator;

import java.util.LinkedList;
import java.util.List;

import ee.tools.componentcalculator.components_toolbox.Body;
import ee.tools.componentcalculator.components_toolbox.CapacitorBody;
import ee.tools.componentcalculator.components_toolbox.ComponentView;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ResistorBody;
import ee.tools.componentcalculator.components_toolbox.ResistorException;
import ee.tools.model.*;

public class ComponentViewFactory {

	public static LinkedList<ComponentViewInterface> makeViews(int type, List<Component> comps)
	{
		LinkedList<ComponentViewInterface> ret = new LinkedList<ComponentViewInterface>();
		
		LinkedList<Integer> serial = new LinkedList<Integer>();
		serial.add(0);
		
		for (Component comp: comps)
		{
			Body b = null;
			
			if (type == ComponentsView.RESISTOR)
			{
				int tolerance = 0;
				try {
					b = new ResistorBody(serial, comp.getValue(), tolerance);
				} catch (ResistorException e) {
					e.printStackTrace();
				}
			}
			else if (type == ComponentsView.CAPACITOR)
			{
			   b = new CapacitorBody(serial);
			}
			
			ComponentView cv = new ComponentView(serial, b, comp.getValue(), comp.getQnty());
			ret.add(cv);
		}
		return ret;
	}
}
