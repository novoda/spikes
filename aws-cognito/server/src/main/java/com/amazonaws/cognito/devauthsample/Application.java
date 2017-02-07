package com.amazonaws.cognito.devauthsample;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.amazonaws.cognito.devauthsample.servlet")
public class Application implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

        if (container instanceof JettyEmbeddedServletContainerFactory) {
            final JettyEmbeddedServletContainerFactory jetty = (JettyEmbeddedServletContainerFactory) container;

            final JettyServerCustomizer customizer = new JettyServerCustomizer() {
                @Override
                public void customize(Server server) {
                    Handler handler = server.getHandler();
                    WebAppContext webAppContext = (WebAppContext) handler;
                    webAppContext.setBaseResource(Resource.newClassPathResource("webroot"));
                }
            };
            jetty.addServerCustomizers(customizer);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
