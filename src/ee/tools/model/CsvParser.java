package ee.tools.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class CsvParser {

	static final int FIND_TYPE = 1, FIND_VALUE = 2, FIND_QNTY = 3, DONE = 4;
	public static LinkedList<Component> parseFile(String f) 
			throws CsvParserException
	{
		LinkedList<Component> ret = new LinkedList<Component>();
		
		FileInputStream fis;
		
		try {	fis = new FileInputStream(f);	} 
		catch (FileNotFoundException fnfe) { throw new CsvParserException("File: " + f + " Not Found!"); }
		
		Scanner row_scanner = new Scanner(fis);
		
		row_scanner.useDelimiter("\r");
		
		while (row_scanner.hasNext())
		{
			String row = row_scanner.next();
			
			Scanner col_scanner = new Scanner(row);
			
			col_scanner.useDelimiter(",");
			
			int state = FIND_TYPE;
			
			char type = '?';
			
			double val = -1;
			
			int qnty = -1;
			
			while (col_scanner.hasNext() && state != DONE)
			{
				
				String scan = col_scanner.next();
			
				switch (state)
				{
				case FIND_TYPE:
					scan = scan.toLowerCase();
					if (scan.equals("r"))
					{
						type = 'r';
					}
					else if (scan.equals("c"))
					{
						type = 'c';
					}
					else { throw new CsvParserException("Unknown Component Type: " + scan); }
					
					state = FIND_VALUE;
					break;
				case FIND_VALUE:
					//if in eng notation, need to convert back to normal
					val = EngNot.convert(scan);
					state = FIND_QNTY;
					break;
				case FIND_QNTY:
					qnty = Integer.parseInt(scan);
					state = DONE;
					break;
				}
			}
		    
			switch(type)
			{
			case 'r':
				ret.add(new Component(val));
				System.out.println("Resistor\t" + val + "\tQnty: " + qnty);
				break;
			case 'c':
				ret.add(new Component(val));
				System.out.println("Capacitor\t" + val + "\tQnty: " + qnty);
				break;
			}
		}
		return ret;
	}
		
	public static void main(String[] args) throws Exception 
	{
		CsvParser.parseFile("/Users/robwasab/Documents/android/PermutationCalculator/src/parts2.csv");
	}
}
