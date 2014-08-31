package ee.tools.model;

import java.util.LinkedList;
import java.util.List;

public class Components extends Component {

	public static final int INVERSE_INVERSE_SUM = 1, SUM = 2, RESISTOR = 1, CAPACITOR = 2;
	public int operation = SUM;
	public int type = RESISTOR;
	public static final int SERIES = 1, PARALLEL = 2;
	public int orientation = SERIES;
	public LinkedList<Component> components;

	
	public Components(List<Component> comps, int operation) 
	{
		super(0, 0);
		super.setQnty(1);
		
		components = new LinkedList<Component>();
		
		if (operation != INVERSE_INVERSE_SUM && operation != SUM) operation = SUM;
		
		this.operation = operation;
		
		if (comps != null)
		{
		   for (Component c : comps) { components.add(c); }
		}
		calculate();
		setType(RESISTOR);
	}

	public Components(List<Component> comps, int operation, int type) 
	{
		super(0, 0);
		super.setQnty(1);
		
		components = new LinkedList<Component>();
		
		if (operation != INVERSE_INVERSE_SUM && operation != SUM) operation = SUM;
		
		this.operation = operation;
		
		if (comps != null)
		{
		   for (Component c : comps) { components.add(c); }
		}
		calculate();
		setType(type);
	}

	
	public static double inverse_inverse_sum(List<Component> comps)
	{
		double val = 0;
		if (comps.size() < 1) return -1;
		
		for (Component c: comps)
		{
			val += 1.0 / c.getValue();
		}
	
		val = 1.0 / val;
		
		return val;
	}
	
	public int getLength()
	{
		int length = 0;
		for (int i = 0; i < components.size(); i++)
		{
			if (components.get(i) instanceof Components)
			{
				length += ((Components)components.get(i)).getLength();
			}
			else length += 1;
		}
		return length;
	}
	
	public static double sum(List<Component> comps)
	{
		double val = 0;
		if (comps.size() < 1) return -1;
		
		for (Component c: comps)
		{
			val += c.getValue();
		}
		
		return val;
	}

	private void calculate()
	{
		double value;
		switch(operation)
		{
		case INVERSE_INVERSE_SUM:
			value = inverse_inverse_sum(components);
			super.setValue(value);
			break;
		case SUM:
			value = sum(components);
			super.setValue(value);			
			break;	
		}

	}
	//@Override 
	public Component add(Component c)
	{
		components.add(c);
		calculate();
		return this;
	}
	
	//@Override
	public Component subtract(Component c)
	{
		int index = components.indexOf(c);
		if (index == -1)
		{
			return null;
		}
		Component ret = components.remove(index);
		calculate();
		return ret;
	}
	
	//@Override
	public double getValue()
	{
		calculate();
		return super.getValue();
	}
	
	//@Override
	public void setValue(double val)
	{
		return;
	}
	
	public String toString() { return to_string(3); }
	
	public String toString(int num_indent) { return to_string(num_indent); }
	
	public String to_string(int num_indent)
	{
		String ret = null;
		
		String indent = "";
		
		for (int i = 0; i < num_indent; i++) indent += " ";
		
		switch(operation)
		{
		case SUM:
			ret = indent + "SUM: " + this.getValue() + "\n";
			break;
		case INVERSE_INVERSE_SUM:
			ret = indent + "INVERSE INVERSE SUM: " + this.getValue() + "\n";;
			break;
		}
		
		int i = 1;
		for (Component c: components)
		{
			ret = ret + indent + i + ". " + c.toString() + "\n";
			i++;
		}
		return ret + "\n";
	}
	
	public int getType() { return type; }

	public void setType(int type) 
	{
		if (type != RESISTOR && type != CAPACITOR) type = RESISTOR;
		else this.type = type; 
		if (type == RESISTOR)
		{
			if (this.operation == this.SUM) { this.orientation = SERIES; }
			else if (this.operation == this.INVERSE_INVERSE_SUM) { this.orientation = PARALLEL; }
		}
		else if (type == CAPACITOR)
		{
			if (this.operation == this.SUM) { this.orientation = PARALLEL; }
			else if (this.operation == this.INVERSE_INVERSE_SUM) { this.orientation = SERIES; }			
		}
	}
	
	public void setOperation(int operation) { this.operation = operation; }
	
	public int getOperation() { return this.operation; }
	
	public int getOrientation() { return this.orientation; }
	
	public static void main(String[] args) 
	{
		LinkedList<Component> ll = new LinkedList<Component>();
		ll.add(new Component(100));
		ll.add(new Component(100));
		Components test1 = new Components(ll, Components.INVERSE_INVERSE_SUM);
		Components test2 = new Components(ll, Components.SUM);
		
		if (test1.getValue() != 50) System.out.println("1. Fail");
		
		if (test2.getValue() != 200) System.out.println("2. Fail");
		
		test1.add(new Component(100));
		
		test2.add(new Component(100));
		
		if (test1.getValue() != 100.0/3.0) System.out.println("3. Fail " + test2.getValue() + " " + 100.0/3);
		
		if (test2.getValue() != 300) System.out.println("4. Fail");
		
		test1.subtract(ll.get(0));
		
		if (test1.getValue() != 50) System.out.println("5. Fail " + test1.getValue());
		
		Components test3 = new Components(null, Components.SUM);
		
		System.out.println(test3.getValue());
		
		test3.add(new Component(11));

		System.out.println(test3.getValue());

	}
}
