package com.axibase.activemqusageemulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shmagrinsky on 12.03.16.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final String RESOURCES_PROPERTY_URL = "app.properties";

    public static void main(String[] args) {
        try {
            LOG.info("Application started");
            /*
            Get data from properties file
             */
            String proprertiesFileUrl;
            if (args != null && args.length > 1 && args[0].equals("-conf")) {
                proprertiesFileUrl = args[1];
            } else {
                proprertiesFileUrl = Application.class.getResourceAsStream(RESOURCES_PROPERTY_URL).toString();
                LOG.info(proprertiesFileUrl);
            }
            new ActiveMQueueBalancer(getProperties(proprertiesFileUrl)).start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(String fileName) throws FileNotFoundException {
        Properties props = new Properties();
        InputStream is = new FileInputStream(fileName);
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}
