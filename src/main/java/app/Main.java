package app;

import app.config.ApplicationConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("test");

        ApplicationConfig config = ApplicationConfig.getInstance();
        config.startServer(7072);


    }

}