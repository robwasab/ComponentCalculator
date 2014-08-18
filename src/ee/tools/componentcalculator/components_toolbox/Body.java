package ee.tools.componentcalculator.components_toolbox;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.os.Bundle;

public interface Body {
		public float getWidth();
		public float getHeight();
		public void setX(float x);
		public void setY(float y);
		public void setHeight(float height);
		public void setWidth(float width);
		public void setSerialNumber(LinkedList<Integer> serial);
		public void setOrigin(Complex c);
		public void draw(Canvas c);
		public void setAngle(double radians);
		public boolean isIn(Complex pnt);
		public void saveInstanceState(Bundle state);
		public void restoreInstanceState(Bundle saved);
		public void setValue(double val);
}
