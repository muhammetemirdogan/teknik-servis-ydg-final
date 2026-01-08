package com.example.teknikservis.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Senaryo2SeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Senaryo 2: Musteriye gore filtreleme calismali")
    void musteriye_gore_filtreleme_calismali() {
        driver.get(baseUrl + "/api/servis-kayitlari");

        String pageSource = driver.getPageSource();

        assertTrue(
                pageSource.contains("Ali Musteri"),
                "Musteri bazli kayitlarda 'Ali Musteri' bilgisi bulunamadi!"
        );
    }
}
