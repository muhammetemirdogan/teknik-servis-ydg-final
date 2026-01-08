package com.example.teknikservis.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo3SeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("H2 veritabani konsoluna erisilebilmeli")
    void h2_console_aciliyor_mu() {
        driver.get(baseUrl + "/h2-console");

        String pageSource = driver.getPageSource();

        assertTrue(
                pageSource.contains("H2 Console") || pageSource.contains("Login"),
                "H2 konsol sayfasina ulasilamadi!"
        );
    }
}
