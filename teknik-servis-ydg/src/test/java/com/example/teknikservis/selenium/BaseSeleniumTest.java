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

    // 🔥 Senaryoların kullandığı field (compile hatasını çözen şey bu)
    protected String baseUrl;

    protected String seleniumRemoteUrl;
    protected boolean headless;

    private String readSysOrEnv(String sysKey, String envKey, String def) {
        String s = System.getProperty(sysKey);
        if (s != null && !s.isBlank()) return s;
        String e = System.getenv(envKey);
        if (e != null && !e.isBlank()) return e;
        return def;
    }

    @BeforeEach
    void setUp() throws Exception {
        // Jenkinsfile’dan gelen -DbaseUrl ve -DseleniumRemoteUrl değerlerini okur
        baseUrl = readSysOrEnv("baseUrl", "APP_BASE_URL", "http://localhost:8082");
        seleniumRemoteUrl = readSysOrEnv("seleniumRemoteUrl", "SELENIUM_URL", "http://localhost:4445/wd/hub");

        String h = readSysOrEnv("headless", "HEADLESS", "true");
        headless = Boolean.parseBoolean(h);

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1365,768");

        driver = new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        try {
            if (driver != null) driver.quit();
        } catch (Exception ignored) {}
    }
}
