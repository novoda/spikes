package com.amazonaws.cognito.devauthsample;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

        try {
            FileInputStream serviceAccount = new FileInputStream("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl("https://" + Configuration.FIREBASE_DATABASE + ".firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
