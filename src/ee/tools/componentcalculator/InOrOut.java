package ee.tools.componentcalculator;

import java.util.List;

public class InOrOut {


	public static double polyX(List<Complex> points, int index)
	{
		return points.get(index).re;
	}
	
	public static double polyY(List<Complex> points, int index)
	{
		return points.get(index).im;
	}
	
	public static boolean inOrOut(List<Complex> points, Complex test)
	{
		int polySides  =  points.size();
	
	 	double x = test.re;
    	double y = test.im;
    	
    	int   i, j=polySides-1;
	
		boolean  oddNodes = false;

		for (i=0; i<polySides; i++) 
		{
			if (   (polyY(points,i)< y && polyY(points,j)>=y  
					||  polyY(points,j) < y && polyY(points,i)>=y) 
					&&  (polyX(points,i)<=x || polyX(points,j)<=x) ) 
			{
				oddNodes^=(polyX(points,i)+(y-polyY(points,i))/(polyY(points,j)-polyY(points,i))
						*(polyX(points,j)-polyX(points,i))<x); 
			}
			j=i; 
		}
		return oddNodes; 
	}
	
}

/*Original Source Code found here:
http://alienryderflex.com/polygon/
//Globals which should be set before calling this function:
//
//int    polySides  =  how many corners the polygon has
//float  polyX[]    =  horizontal coordinates of corners
//float  polyY[]    =  vertical coordinates of corners
//float  x, y       =  point to be tested
//
//(Globals are used in this example for purposes of speed.  Change as
//desired.)
//
//The function will return YES if the point x,y is inside the polygon, or
//NO if it is not.  If the point is exactly on the edge of the polygon,
//then the function may return YES or NO.
//
//Note that division by zero is avoided because the division is protected
//by the "if" clause which surrounds it.

bool pointInPolygon() {

int   i, j=polySides-1 ;
bool  oddNodes=NO      ;

for (i=0; i<polySides; i++) {
if ((polyY[i]< y && polyY[j]>=y
||   polyY[j]< y && polyY[i]>=y)
&&  (polyX[i]<=x || polyX[j]<=x)) {
  oddNodes^=(polyX[i]+(y-polyY[i])/(polyY[j]-polyY[i])*(polyX[j]-polyX[i])<x); }
j=i; }

return oddNodes; }
*/