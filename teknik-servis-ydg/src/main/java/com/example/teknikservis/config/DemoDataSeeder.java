package com.example.teknikservis.config;

import com.example.teknikservis.model.*;
import com.example.teknikservis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
            KullaniciRepository kullaniciRepository,
            CihazRepository cihazRepository,
            ServisKaydiRepository servisKaydiRepository
    ) {
        return args -> {
            // Selenium senaryoları için en az 1 kayıt lazım.
            // Tekrarlı ekleme olmasın diye sadece boşsa seed'le.
            if (servisKaydiRepository.count() > 0) return;

            Kullanici ali = new Kullanici();
            ali.setAd("Ali Musteri");
            ali.setEmail("ali@demo.com");
            ali.setSifre("1234");
            ali.setRol(Rol.MUSTERI);

            Kullanici teknisyen = new Kullanici();
            teknisyen.setAd("Veli Teknisyen");
            teknisyen.setEmail("veli@demo.com");
            teknisyen.setSifre("1234");
            teknisyen.setRol(Rol.TEKNISYEN);

            kullaniciRepository.saveAll(List.of(ali, teknisyen));

            Cihaz cihaz = new Cihaz();
            cihaz.setMusteri(ali);
            cihaz.setMarka("Samsung");
            cihaz.setModel("Galaxy");
            cihaz.setSeriNo("SN123");
            cihazRepository.save(cihaz);

            ServisKaydi kayit = new ServisKaydi();
            kayit.setCihaz(cihaz);
            kayit.setTeknisyen(teknisyen);
            kayit.setAciklama("Ekran kirik");   // test stringi ile birebir
            kayit.setDurum(Durum.ACIK);         // testin beklediği
            kayit.setAcilisTarihi(LocalDateTime.now().minusDays(1));

            servisKaydiRepository.save(kayit);
        };
    }
}
