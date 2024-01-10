package web;

import org.json.JSONObject;
import util.DbUtil;

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
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;

public class MyServlet extends HttpServlet {

    private static final String WEATHER_URL = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=bc092a2766b9459fa39120259232712&num_of_days=1&day=today&extra=utcDateTime&format=json&showlocaltime=yes&q=";
    private static final String SELECT_SQL_BY_DATE_AND_CITY = "select * from weather where city_name=? AND date = ?";
    private static final String INSERT_SQL = "insert into weather(city_name,date,max_temperature,min_temperature) values(?, ?, ?,?)";

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
            JSONObject weather = (JSONObject) data.getJSONArray("weather").get(0);
            JSONObject city = (JSONObject) data.getJSONArray("request").get(0);
            String cityRequest = city.getString("query");
            int maxTemperature = Integer.parseInt(weather.getString("maxtempC"));
            int minTemperature = Integer.parseInt(weather.getString("mintempC"));

            printWriter.write("Погодна на сегодня для локации: " + cityRequest + ":<br/>"
                    + "Максимальная температура: " + maxTemperature + "\u2103" + "<br/>"
                    + "Минимальная температура: " + minTemperature + "\u2103" + "<br/>");


            try {
                Class.forName(DbUtil.driver);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Connection connection = DriverManager.getConnection(DbUtil.url, DbUtil.user, DbUtil.password);

                PreparedStatement statementCheckToday =
                        connection.prepareStatement(SELECT_SQL_BY_DATE_AND_CITY);
                statementCheckToday.setString(1, cityRequest);
                statementCheckToday.setDate(2, Date.valueOf(LocalDate.now()));

                ResultSet resultSet = statementCheckToday.executeQuery();
                if (!resultSet.next()) {

                    PreparedStatement statement =
                            connection.prepareStatement(INSERT_SQL);
                    statement.setString(1, cityRequest);
                    statement.setDate(2, Date.valueOf(LocalDate.now()));
                    statement.setInt(3, maxTemperature);
                    statement.setInt(4, minTemperature);

                    statement.executeUpdate();
                    statement.close();
                }
                resultSet.close();
                statementCheckToday.close();
                PreparedStatement statementCheckYesterday =
                        connection.prepareStatement(SELECT_SQL_BY_DATE_AND_CITY);
                statementCheckYesterday.setString(1, cityRequest);
                statementCheckYesterday.setDate(2, Date.valueOf(LocalDate.now().minusDays(1)));
                ResultSet resultSet1 = statementCheckYesterday.executeQuery();
                if (resultSet1.next()) {
                    int maxTempYesterday = resultSet1.getInt(4);
                    int tempDiffer = maxTemperature - maxTempYesterday;
                    if (maxTemperature > maxTempYesterday) {
                        printWriter.write("Сегодня теплее, чем вчера на " + tempDiffer + "\u2103");
                    } else {
                        printWriter.write("Сегодня холоднее, чем вчера на " + (-tempDiffer) + "\u2103");
                    }
                }
                resultSet1.close();
                statementCheckYesterday.close();
                connection.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            printWriter.close();
        }
    }
}
