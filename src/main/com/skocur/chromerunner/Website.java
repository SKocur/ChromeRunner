package com.skocur.chromerunner;

import java.util.Arrays;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * <h1>Website</h1>
 * This class is responsible for opening web browser with given IP, PORT and URL.
 *
 * @author Szymon Kocur
 */
class Website implements Runnable {

    private String url;
    private String PROXY;
    private String PORT;


    public Website(String url, String proxy, String port) {
        this.url = url;
        this.PROXY = proxy;
        this.PORT = port;
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
