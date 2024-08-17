import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
}
