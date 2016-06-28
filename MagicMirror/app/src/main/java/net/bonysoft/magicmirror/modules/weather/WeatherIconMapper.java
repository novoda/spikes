package net.bonysoft.magicmirror.modules.weather;

import net.bonysoft.magicmirror.R;


public class WeatherIconMapper {
	
	public static int getWeatherResource(String id) {
		if (id.equals("01d"))
			return R.drawable.w01d;
		else if (id.equals("01n"))
			return R.drawable.w01n;
		else if (id.equals("02d") || id.equals("02n"))
			return R.drawable.w02d;
		else if (id.equals("03d") || id.equals("03n"))
			return R.drawable.w03d;
		else if (id.equals("03d") || id.equals("03n"))
			return R.drawable.w03d;
        else if (id.equals("04d") || id.equals("04n"))
            return R.drawable.w04d;
		else if (id.equals("09d") || id.equals("09n"))
			return R.drawable.w500d;
		else if (id.equals("10d") || id.equals("10n"))
			return R.drawable.w501d;
		else if (id.equals("11d") || id.equals("11n"))
			return R.drawable.w212d;
		else if (id.equals("13d") || id.equals("13n"))
			return R.drawable.w13d;
		else if (id.equals("50d") || id.equals("50n"))
			return R.drawable.w50d;
		
		return R.drawable.w01d;
		
	}

}
