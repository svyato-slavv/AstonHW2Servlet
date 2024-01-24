package web;

import DAO.WeatherDAO;
import model.Weather;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

public class MyServlet extends HttpServlet {

    private static final String WEATHER_URL = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=bc092a2766b9459fa39120259232712&num_of_days=1&day=today&extra=utcDateTime&format=json&showlocaltime=yes&q=";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cityName = req.getParameter("city");
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response;
        try {
            URI uri = URI.create(WEATHER_URL + cityName);
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = resp.getWriter();
        if (response.body().contains("error")) {
            printWriter.write("Я не смог найти погоду для данной локации: " + cityName + ".");
        } else {
            JSONObject json = new JSONObject(response.body());
            JSONObject data = json.getJSONObject("data");
            JSONObject weatherJSON = (JSONObject) data.getJSONArray("weather").get(0);
            JSONObject cityJSON = (JSONObject) data.getJSONArray("request").get(0);
            String cityRequest = cityJSON.getString("query");
            int maxTemperature = Integer.parseInt(weatherJSON.getString("maxtempC"));
            int minTemperature = Integer.parseInt(weatherJSON.getString("mintempC"));

            printWriter.write("Погодна на сегодня для локации: " + cityRequest + ":<br/>"
                    + "Максимальная температура: " + maxTemperature + "\u2103" + "<br/>"
                    + "Минимальная температура: " + minTemperature + "\u2103" + "<br/>");
            WeatherDAO weatherDAO = new WeatherDAO();
            if (weatherDAO.getByDateAndCity(LocalDate.now(), cityRequest.toLowerCase()) == null) {
                Weather weather = new Weather(LocalDate.now(), maxTemperature, minTemperature);
                weatherDAO.saveWeather(weather, cityRequest.toLowerCase());
            }

            Weather checkYesterdayWeather = weatherDAO.getByDateAndCity(LocalDate.now().minusDays(1), cityRequest.toLowerCase());
            if (checkYesterdayWeather != null) {
                int maxTempYesterday = checkYesterdayWeather.getMaxTemperature();
                int tempDiffer = maxTemperature - maxTempYesterday;
                if (maxTemperature > maxTempYesterday) {
                    printWriter.write("Сегодня теплее, чем вчера на " + tempDiffer + "\u2103");
                } else {
                    printWriter.write("Сегодня холоднее, чем вчера на " + (-tempDiffer) + "\u2103");
                }
            }
        }
        printWriter.close();
    }
}
