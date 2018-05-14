package gov.nist.swidval.webapp;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

public class MyWebApplicationInitializer implements WebApplicationInitializer {
	private static final Logger log = LogManager.getLogger();

    @Override
    public void onStartup(ServletContext container) {
        ServletRegistration.Dynamic registration = container.addServlet("swidval", new DispatcherServlet());
        registration.setLoadOnStartup(1);
        Set<String> mappings = registration.addMapping("/");
        log.info("Servlet mappings: "+mappings);
    }

}