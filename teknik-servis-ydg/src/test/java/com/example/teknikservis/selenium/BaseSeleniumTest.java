package com.example.teknikservis.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseSeleniumTest {

    protected static WebDriver driver;
    protected static String baseUrl;
    protected static String seleniumRemoteUrl;

    @BeforeAll
    static void setupDriver() {
        // baseUrl: önce -DbaseUrl, yoksa env BASE_URL, yoksa default
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = System.getProperty("baseUrl");
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = System.getenv("BASE_URL");
            }
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = "http://localhost:8081";
            }
        }

        // headless: önce -Dheadless, yoksa env SELENIUM_HEADLESS, yoksa true
        String headlessStr = System.getProperty("headless");
        if (headlessStr == null || headlessStr.isBlank()) {
            headlessStr = System.getenv("SELENIUM_HEADLESS");
        }
        boolean headless = (headlessStr == null || headlessStr.isBlank())
                ? true
                : Boolean.parseBoolean(headlessStr);

        // Remote Selenium URL: önce -DseleniumRemoteUrl, yoksa env SELENIUM_URL, yoksa null
        if (seleniumRemoteUrl == null || seleniumRemoteUrl.isBlank()) {
            seleniumRemoteUrl = System.getProperty("seleniumRemoteUrl");
            if (seleniumRemoteUrl == null || seleniumRemoteUrl.isBlank()) {
                seleniumRemoteUrl = System.getenv("SELENIUM_URL");
            }
        }

        // Driver'ı sadece 1 kere oluştur (tüm senaryolar aynı JVM'de koşar)
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();

            // Jenkins/CI için stabil argümanlar
            if (headless) options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");

            // 1) Remote Selenium varsa RemoteWebDriver ile çalış
            if (seleniumRemoteUrl != null && !seleniumRemoteUrl.isBlank()) {
                try {
                    driver = new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
                    return;
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Geçersiz seleniumRemoteUrl: " + seleniumRemoteUrl, e);
                }
            }

            // 2) Aksi halde local ChromeDriver
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            if (driver != null) driver.quit();
        } catch (Exception ignored) {
        } finally {
            driver = null;
        }
    }
}
