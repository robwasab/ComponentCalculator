package ee.tools.componentcalculator;

import java.util.LinkedList;

public class Log {
	public static LinkedList<String> blackList = new LinkedList<String>();
	public static void d(String tag, String s)
	{
		for (int i = 0; i < blackList.size(); i++)
		{
			if (tag.equals(blackList.get(i))) return;
		}
		android.util.Log.d(tag, s);
	}
}
