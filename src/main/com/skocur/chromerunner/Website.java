package com.skocur.chromerunner;

import java.util.Arrays;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class Website implements Runnable {

    String url;
    String PROXY;
    String PORT;
    int id;

    public Website(String proxy, String port) {
        this.PROXY = proxy;
        this.PORT = port;
    }

    public Website(String url, String proxy, String port, int counter) {
        this.url = url;
        this.PROXY = proxy;
        this.PORT = port;
        this.id = counter;
    }

    @Override
    public void run() {
        String proxyServer = PROXY + ":" + PORT;

        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyServer);
        proxy.setSslProxy(proxyServer);

        ChromeOptions options = new ChromeOptions();
        options.setCapability("proxy", proxy);
        options.setCapability("chrome.switches", Arrays.asList("--proxy-server=" + proxyServer));

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        System.out.println("Successfully opened");
    }
}
