package com.example.teknikservis.unit;

import com.example.teknikservis.entity.Cihaz;
import com.example.teknikservis.entity.Kullanici;
import com.example.teknikservis.entity.ServisKaydi;
import com.example.teknikservis.repository.CihazRepository;
import com.example.teknikservis.repository.KullaniciRepository;
import com.example.teknikservis.repository.ServisKaydiRepository;
import com.example.teknikservis.service.impl.ServisKaydiServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServisKaydiServiceImplUnitTest {

    @Mock
    private ServisKaydiRepository servisKaydiRepository;

    @Mock
    private KullaniciRepository kullaniciRepository;

    @Mock
    private CihazRepository cihazRepository;

    @InjectMocks
    private ServisKaydiServiceImpl servisKaydiService;

    @Test
    void createServisKaydi_musteriYoksa_hataVermeli() {
        when(kullaniciRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servisKaydiService.createServisKaydi(1L, 10L, null, "aciklama", LocalDateTime.now())
        );

        assertTrue(ex.getMessage().toLowerCase().contains("musteri"));
        verify(cihazRepository, never()).findById(anyLong());
        verify(servisKaydiRepository, never()).save(any());
    }

    @Test
    void createServisKaydi_cihazBaskaMusteriyeAitse_hataVermeli() {
        Kullanici musteri = user(1L);
        Kullanici baskaMusteri = user(2L);

        Cihaz cihaz = cihaz(10L, baskaMusteri);

        when(kullaniciRepository.findById(1L)).thenReturn(Optional.of(musteri));
        when(cihazRepository.findById(10L)).thenReturn(Optional.of(cihaz));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                servisKaydiService.createServisKaydi(1L, 10L, null, "aciklama", LocalDateTime.now())
        );

        assertTrue(ex.getMessage().toLowerCase().contains("ait"));
        verify(servisKaydiRepository, never()).save(any());
    }

    @Test
    void createServisKaydi_ayniGunAyniCihazIcinAcikKayitVarsa_hataVermeli() {
        Kullanici musteri = user(1L);
        Cihaz cihaz = cihaz(10L, musteri);

        when(kullaniciRepository.findById(1L)).thenReturn(Optional.of(musteri));
        when(cihazRepository.findById(10L)).thenReturn(Optional.of(cihaz));
        when(servisKaydiRepository.existsByCihazAndDurumAndAcilisTarihiBetween(
                eq(cihaz),
                eq(ServisKaydi.Durum.ACIK),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                servisKaydiService.createServisKaydi(1L, 10L, null, "aciklama", LocalDateTime.now())
        );

        assertTrue(ex.getMessage().toLowerCase().contains("zaten"));
        verify(servisKaydiRepository, never()).save(any());
    }

    @Test
    void createServisKaydi_basariliOlusturupKaydetmeli() {
        Kullanici musteri = user(1L);
        Cihaz cihaz = cihaz(10L, musteri);
        LocalDateTime now = LocalDateTime.of(2025, 12, 27, 12, 0);

        when(kullaniciRepository.findById(1L)).thenReturn(Optional.of(musteri));
        when(cihazRepository.findById(10L)).thenReturn(Optional.of(cihaz));
        when(servisKaydiRepository.existsByCihazAndDurumAndAcilisTarihiBetween(
                eq(cihaz),
                eq(ServisKaydi.Durum.ACIK),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(false);

        when(servisKaydiRepository.save(any(ServisKaydi.class))).thenAnswer(inv -> {
            ServisKaydi k = inv.getArgument(0);
            k.setId(99L);
            return k;
        });

        ServisKaydi kayit = servisKaydiService.createServisKaydi(1L, 10L, null, "aciklama", now);

        assertNotNull(kayit.getId());
        assertEquals(ServisKaydi.Durum.ACIK, kayit.getDurum());
        assertEquals("aciklama", kayit.getAciklama());
        assertEquals(now, kayit.getAcilisTarihi());
        assertEquals(cihaz, kayit.getCihaz());

        // Save'e giden objeyi yakala
        ArgumentCaptor<ServisKaydi> captor = ArgumentCaptor.forClass(ServisKaydi.class);
        verify(servisKaydiRepository).save(captor.capture());
        assertEquals(ServisKaydi.Durum.ACIK, captor.getValue().getDurum());
    }

    @Test
    void cancelServisKaydi_baskaMusteriIptalEdemez() {
        Kullanici musteri = user(1L);
        Cihaz cihaz = cihaz(10L, musteri);

        ServisKaydi kayit = new ServisKaydi();
        kayit.setId(100L);
        kayit.setCihaz(cihaz);
        kayit.setDurum(ServisKaydi.Durum.ACIK);

        when(servisKaydiRepository.findById(100L)).thenReturn(Optional.of(kayit));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                servisKaydiService.cancelServisKaydi(100L, 999L)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("yetki"));
        verify(servisKaydiRepository, never()).save(any());
    }

    private static Kullanici user(Long id) {
        Kullanici k = new Kullanici();
        k.setId(id);
        k.setAd("Test");
        k.setEmail("test" + id + "@mail.com");
        k.setSifre("123");
        k.setRol(Kullanici.Rol.MUSTERI);
        return k;
    }

    private static Cihaz cihaz(Long id, Kullanici musteri) {
        Cihaz c = new Cihaz();
        c.setId(id);
        c.setMusteri(musteri);
        c.setMarka("Marka");
        c.setModel("Model");
        c.setSeriNo("SN" + id);
        return c;
    }
}
