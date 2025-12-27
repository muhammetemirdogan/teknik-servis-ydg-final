# Teknik Servis ve Arıza Kayıt Sistemi (YDG Final Projesi)

Bu proje, **Yazılım Doğrulama ve Geçerleme (YDG)** dersi final proje kriterlerini birebir karşılayacak şekilde hazırlanmıştır.

## 1) Proje Özeti
- Spring Boot (REST API) + H2 (in-memory) + JPA
- CI/CD: Jenkins Pipeline (**Jenkinsfile repoda**)
- Testler:
  - **Birim testleri** (Mockito + JUnit 5): **/*UnitTest.java
  - **Entegrasyon testleri** (SpringBootTest + H2): **/*IT.java
  - **Sistem/E2E testleri** (Selenium): `Senaryo1..Senaryo10SeleniumTest`

> Not: Uygulama çalışınca `data.sql` otomatik yüklenir. Selenium senaryoları bu seed verileri üzerinden kontrol yapar.

## 2) API Endpoint'leri
- Tüm servis kayıtları: `GET /api/servis-kayitlari`
- Müşteriye göre filtre: `GET /api/servis-kayitlari/musteri/{musteriId}`
- Yeni servis kaydı ekleme: `POST /api/servis-kayitlari`
- Healthcheck: `GET /actuator/health`
- H2 Console: `/h2-console`

## 3) Seed Veri (data.sql)
Uygulama ilk açılışta aşağıdaki örnek verileri yükler:
- Kullanıcı: **Ali Musteri**
- Teknisyen: **Veli Teknisyen**
- Cihaz: **Samsung A50 - SN123**
- Servis Kaydı açıklaması: **"Ekran kirik"**

Bu sayede Selenium senaryolarında JSON çıktısı içinde beklenen metinler doğrulanır.

## 4) Jenkins Pipeline Aşamaları (Rubrik ile 1:1)
Jenkinsfile içinde aşağıdaki stage'ler vardır:

1. **Checkout from GitHub (5p)**
2. **Build (5p)**
3. **Unit Tests + Rapor (15p)**
4. **Integration Tests + Rapor (15p)**
5. **Docker Build & Run (5p)**
6. **Çalışan sistem üzerinde Selenium senaryoları + Rapor (55p)**
   - Senaryo 1..10 ayrı stage olarak koşturulur (ek senaryolar için puan avantajı)

### Test Raporlama
- Unit test raporları: `target/surefire-reports-unit/`
- E2E raporları: `target/surefire-reports-e2e-s*/`
- Integration raporları: `target/failsafe-reports-it/`

Jenkins `junit` adımı bu XML raporlarını otomatik toplar ve build ekranında gösterir.

## 5) Lokal Çalıştırma
### 5.1) Build
```bash
./mvnw -DskipTests clean package
```

### 5.2) Uygulamayı çalıştır
```bash
./mvnw spring-boot:run
```

### 5.3) Unit test
```bash
./mvnw -DskipUnitTests=false test
```

### 5.4) Integration test
```bash
./mvnw -DskipUnitTests=true verify
```

### 5.5) Docker ile çalıştır
```bash
docker build -t teknik-servis-image .
docker run --rm -p 8081:8081 --name teknik-servis-container teknik-servis-image
```

## 6) Selenium Senaryoları
Selenium testleri, çalışan uygulamaya tarayıcı ile gidip (JSON/H2 console) içerik doğrulaması yapar.

Jenkins üzerinde **Docker Selenium Standalone Chrome** (RemoteWebDriver) kullanılır.
Lokal çalıştırmada ise sistemde Chrome yüklüyse local ChromeDriver ile de koşabilir.

## 7) GitHub Push ile Tetikleme
Jenkinsfile içinde `githubPush()` trigger vardır.
- Jenkins job tarafında GitHub webhook / GitHub plugin ayarlarının yapılması gerekir.

---

**Hazırlayan:** Muhammet Emir Doğan
