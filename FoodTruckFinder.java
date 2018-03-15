

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.*;

public class FoodTruckFinder {

	private static int getDay(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	private static int convert(String input)
	{
		String[] temp = input.split(":");
		return Integer.parseInt(temp[0]);
	}
	private static int gethour(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	private static boolean valid(JSONObject jsonObject, Date date)
	{
		try {
			int start = convert(jsonObject.getString("start24"));
			int end = convert(jsonObject.getString("end24"));
			String day = jsonObject.getString("dayofweekstr");
			DateFormatSymbols dfs = new DateFormatSymbols();
			String curday = dfs.getWeekdays()[getDay(date)];
			String preday = getDay(date)==1? dfs.getWeekdays()[7] : dfs.getWeekdays()[getDay(date)-1];
			int curhour = gethour(date);
			if(curday.equals(day))
			{
				if(start>=end)
				{
					end = end +24;
				}
				if(curhour>=start && curhour<end)
					return true;
			}else if(preday.equals(day)&&start>=end)   //start>end
			{
				if(curhour<end)
					return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Set<String> getFoodTruck(String input, Date date)
	{
		Set<String> result = new HashSet<>();
		try {
			JSONArray jsonArray = new JSONArray(input);
			for(int i=0; i<jsonArray.length(); i++)
			{
				Object value = jsonArray.get(i);
				if(value instanceof JSONObject)
				{
					if(valid((JSONObject)value, date) && !result.contains(((JSONObject) value).getString("applicant")))
					{
						result.add(((JSONObject) value).getString("applicant"));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void show(List<String> res)
	{
		if(res.size()>10) {
			int page = 0;
			while ((res.size() - 10 * page) > 10) {
				for (int i = 0; i < 10; i++) {
					System.out.println(res.get(10 * page + i));
				}
				System.out.println("press N then enter to continue");
				Scanner sc = new Scanner(System.in);
				while (true) {

					String c = sc.nextLine();
					if (c.toLowerCase().equals("n")) {
						break;
					} else {
						System.out.println("enter N or n");

					}
				}
				page++;
			}
			for (int i = 10 * page; i < res.size(); i++) {
				System.out.println(res.get(i));
			}
		}
		else {
			res.forEach((p)->System.out.println(p));
		}
	}


	public static void main(String[] args) {
   		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL("http://data.sfgov.org/resource/bbb8-hzi6.json");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			String json = result.toString();
			Date cur = new Date();
			Set<String> res = getFoodTruck(json, cur);
			List<String> fres = new ArrayList<>();
			fres.addAll(res);
			show(fres);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

// to run:
// $ javac FoodTruckFinder.java && java FoodTruckFinder
