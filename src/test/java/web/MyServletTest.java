package web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class MyServletTest {
    private MyServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        servlet = new MyServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void doGet() throws ServletException, IOException {
        String city = "Moscow";
        request.setParameter("key", "bc092a2766b9459fa39120259232712");
        request.setParameter("num_of_days", "1");
        request.setParameter("day", "today");
        request.setParameter("extra", "utcDateTime");
        request.setParameter("format", "json");
        request.setParameter("showlocaltime", "yes");
        request.setParameter("city", city);
        servlet.doGet(request, response);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Moscow, Russia")));
    }
}


