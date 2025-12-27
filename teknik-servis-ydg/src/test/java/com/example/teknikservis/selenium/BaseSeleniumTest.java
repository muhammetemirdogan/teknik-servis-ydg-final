package com.example.teknikservis.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected String baseUrl() {
        String s = System.getProperty("baseUrl");
        if (s != null && !s.isBlank()) return s;
        String env = System.getenv("APP_BASE_URL");
        return (env != null && !env.isBlank()) ? env : "http://localhost:8082";
    }

    protected String seleniumUrl() {
        String s = System.getProperty("seleniumRemoteUrl");
        if (s != null && !s.isBlank()) return s;
        String env = System.getenv("SELENIUM_URL");
        return (env != null && !env.isBlank()) ? env : "http://localhost:4445/wd/hub";
    }

    protected boolean headless() {
        String s = System.getProperty("headless");
        if (s == null) s = System.getenv("HEADLESS");
        return s == null || s.isBlank() || Boolean.parseBoolean(s);
    }

    @BeforeEach
    void setUp() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1280,900");
        if (headless()) options.addArguments("--headless=new");

        // ✅ Mutlaka RemoteWebDriver
        driver = new RemoteWebDriver(new URL(seleniumUrl()), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
