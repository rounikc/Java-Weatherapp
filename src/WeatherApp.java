import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/*ret weather data from API
* this backend logic will fetch latest weather
* data from the external API and return it.
* The GUI will then display this data to the user
*/
public class WeatherApp {
    //fetch weather data of location
    public static JSONObject getWeatherData(String locationName) {
        //get location coords using geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract lat and long data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude =  (double) location.get("latitude");
        double longitude =  (double) location.get("longitude");

        //build API req URL with location coords
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,precipitation,weather_code,windspeed_10m&timezone=Asia%2FBangkok";

        try{
            //call api to get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check respons status
            //200 - connection success
            if (conn.getResponseCode() != 200) {
                System.out.println("Error! Could not connect to API");
                return null;
            }

            //store json data
            StringBuilder resultJson = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());
            while(sc.hasNext()) {
                //read and store into the string builder
                resultJson.append(sc.nextLine());
            }

            //close scanner
            sc.close();

            //url close
            conn.disconnect();

            //parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //need the current hour's data, need the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weather_code = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertweather_code((long) weather_code.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build weather json data obj access in frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed_10m", windspeed);

            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //ret geographic coords for given location name
    public static JSONArray getLocationData(String locationName) {
        //replace whitespaces to + to adhere API req format
        locationName = locationName.replace(" ", "+");

        //build API url with loc params
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            //call api to get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means success

            if(conn.getResponseCode() != 200) {
                System.out.println("Error: Could not  connect to API - " + conn.getResponseCode());
                return null;
            }else {
                //store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());

                //read and store resulting json into a string builder
                while(sc.hasNext()) {
                    resultJson.append(sc.nextLine());
                }

                //close scanner
                sc.close();

                //close url
                conn.disconnect();

                //parse json string to json obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get list location data the API generated from loc name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        //loc not found
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try{
            //attempt to create conn
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set req method to get
            conn.setRequestMethod("GET");

            //connect to API
            return conn;
        }catch(IOException e) {
            e.printStackTrace();
        }

        //cant make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches current
        for(int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);

            if(time.equalsIgnoreCase(currentTime)) {
                //return index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format to: 2024-08-18T00:00 (API reads like this)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print current date and time
        String formattedDateandTime = currentDateTime.format(formatter);

        return formattedDateandTime;
    }

    private static String convertweather_code(long weather_code) {
        String weatherCondition = "";
        if(weather_code == 0L) {
            weatherCondition = "Clear";
        }else if(weather_code > 0L && weather_code <= 3L) {
            weatherCondition = "Cloudy";
        }else if((weather_code >= 51L && weather_code <= 67L)
                ||
                (weather_code >= 80L && weather_code <= 99L)) {
            weatherCondition = "Rain";
        }else if(weather_code >= 71L && weather_code <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
