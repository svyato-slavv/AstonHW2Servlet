import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class MyServletTest {
    @Test
    void doGet() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String city = "Moscow";
        request.setParameter("key", "bc092a2766b9459fa39120259232712");
        request.setParameter("num_of_days", "1");
        request.setParameter("day", "today");
        request.setParameter("extra", "utcDateTime");
        request.setParameter("format", "json");
        request.setParameter("showlocaltime", "yes");
        request.setParameter("city", city);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MyServlet servlet = new MyServlet();
        servlet.doGet(request, response);
        assertAll("Test doGet() for Moscow",
                () -> assertEquals(200, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains(city)));
    }
}


