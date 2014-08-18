package ee.tools.componentcalculator;

import android.graphics.Canvas;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;

public class ComponentDrawingUtility {

	public static void draw(ComponentViewInterface comp, Canvas c, boolean fit)
	{
		GraphPaperDrawer.draw(c);
		
		if (comp != null)
		{
			if (fit == false)
			{
				comp.draw(c);
			}
			else 
			{
				comp.resetShrink();
				do
				{
					if (comp.getClass() == ComponentsView.class)
					{
						ComponentsView csv = (ComponentsView) comp;
						if (csv.getOrientation() == ComponentsView.SERIES)
							csv.easy_series_arrange(c);
						else
							csv.setXY(0, csv.getHeight()/2);
					}
					else
						comp.setXY(0, comp.getHeight()/2);

					float lowest_y = comp.get_lowest_y();
					
					if (lowest_y < c.getHeight())
					{
						break;
					}
				} while(comp.shrink());
				
				comp.draw(c);
			}
		}

	}
}
