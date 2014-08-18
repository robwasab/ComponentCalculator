package ee.tools.componentcalculator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class GraphPaperDrawer {

	static int square_size = 50;
	
	public static void draw(Canvas c)
	{
		Paint line_paint = new Paint();
		int thin_line_thickness = 1;
		int thick_line_thickness = 3;
		
		line_paint.setColor(Color.CYAN);
		line_paint.setStrokeWidth(thin_line_thickness);
		
		int width = c.getWidth();
		int height = c.getHeight();
		
		Paint background = new Paint();
		background.setStyle(Style.FILL);
		background.setColor(Color.WHITE);
		Rect r = new Rect(0, 0, width, height);
		c.drawRect(r, background);
		
		int current_x = 0;
		for (; current_x < width; current_x += square_size)
		{
			if (current_x % (5 * square_size) == 0) line_paint.setStrokeWidth(thick_line_thickness);
			else line_paint.setStrokeWidth(thin_line_thickness);
			c.drawLine(current_x, 0, current_x, height, line_paint);
		}
		
		int current_y = 0;
		for (; current_y < height; current_y += square_size)
		{
			if (current_y % (5 * square_size) == 0) line_paint.setStrokeWidth(thick_line_thickness);
			else line_paint.setStrokeWidth(thin_line_thickness);
			c.drawLine(0, current_y, width, current_y, line_paint);
		}

	}
}
