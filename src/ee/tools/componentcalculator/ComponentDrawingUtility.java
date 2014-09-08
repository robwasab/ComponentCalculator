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
			comp.draw(c);
		}
	}
}
