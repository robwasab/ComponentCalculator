package ee.tools.model;

public class Component implements Comparable<Component>{
	
 	double val;
	int qnty = 0;
		
	public Component(double val) {this.val = val;}
	public Component(double val, int qnty) {this.val = val; this.qnty = qnty; }
	
	public double getValue() { return this.val; }

	public void   setValue(double val) { this.val = val; }
	
	public boolean equals(Object o)
	{
		if (o == null) return false;
		if (o.getClass() != Component.class) return false;
		if (   ((Component)o).getValue() != this.getValue()   ) return false;
		if (   ((Component)o).getQnty()  != this.getQnty()    ) return false;
		return true;
	}
	
	public Component clone() { return new Component(this.val, this.qnty); }
	
	public Component add(Component c) 
	{
		double val = this.val;
		val += c.getValue();
		return new Component(val);
	}
	
	public Component subtract(Component c)	
	{
		double val = this.val;
		val -= c.getValue();
		return new Component(val);
	}

	public Component multiply(Component c) 
	{
		double val = this.val;
		val *= c.getValue();
		return new Component(val);
	}

	public Component divide(Component c) 
	{
		double val = this.val;
		val /= c.getValue();
		return new Component(val);
	}

	public boolean lessThan(Component c) {return this.val < c.getValue();}

	public boolean greaterThan(Component c) {return this.val > c.getValue();}
	
	public String toString() {return Double.toString(this.val);}
	
 	public int getQnty() {	return qnty;	}

 	public void setQnty(int qnty) {	this.qnty = qnty;	}
	
	public int compareTo(Component c) 
	{
		return (int) (this.val * 1000 - c.getValue() * 1000);
	}
}
