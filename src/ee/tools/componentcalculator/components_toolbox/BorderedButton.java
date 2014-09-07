package ee.tools.componentcalculator.components_toolbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

public class BorderedButton extends Button{

	Paint border = new Paint();
	static final float boarder_width = 10.0f;
	
	public BorderedButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BorderedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BorderedButton(Context context) {
		super(context);
		init();
	}

	private void init()
	{
		border.setStrokeWidth(10.0f);
	}
	public void onDraw(Canvas c)
	{
		super.onDraw(c);
		int width = this.getWidth();
		int height = this.getHeight();
		border.setColor(Color.BLACK);
		c.drawRect(0, 0, width, height, border);
		border.setColor(Color.WHITE);
		c.drawRect(boarder_width/2.0f, boarder_width/2.0f, width-boarder_width/2.0f, height-boarder_width/2.0f, border);
	}
}
