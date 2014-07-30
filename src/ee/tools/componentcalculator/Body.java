package ee.tools.componentcalculator;

import android.graphics.Canvas;
import android.os.Bundle;

public interface Body {
		public float getWidth();
		public float getHeight();
		public void setX(float x);
		public void setY(float y);
		public void setOrigin(Complex c);
		public void draw(Canvas c);
		public void setAngle(double radians);
		public void saveInstanceState(Bundle state);
		public void restoreInstanceState(Bundle saved);
}
