import com.fasterxml.jackson.databind.ObjectMapper;

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

public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String city = req.getParameter("city");
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = null;
        try {
            String endPoint = "https://api.worldweatheronline.com/premium/v1/weather.ashx";
            String parameters = "?key=bc092a2766b9459fa39120259232712&num_of_days=1&day=today&extra=utcDateTime&format=json&showlocaltime=yes&q=" + city;
            URI uri = URI.create(endPoint + parameters);
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            System.out.println("Status Code: " + response.statusCode());
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(response.body(), Object.class);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(response.body());
            printWriter.close();
        } else throw new ServletException("response from weather API is null");
    }
}
