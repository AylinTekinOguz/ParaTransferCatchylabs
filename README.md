# Catchylabs - Para Transfer Uygulaması Test Otomasyon Projesi

Bu proje, Java diliyle Selenium, Gauge Framework ve Maven yapısı kullanılarak geliştirilmiş bir test otomasyon framework'üdür. Web uygulamalarını otomatik olarak test etmek için kullanılır.

## 📌  Proje Yapısı

📂 project-root

|── 📂 specs

|── ├── 📂 Concepts # Tekrar eden senaryo adımları

├── |── 📂 Specs    # Gauge senaryo dosyaları

├── 📂 src       

│   ├── 📂 test

│   │   ├── 📂 java

│   │   │   ├── 📂 base    # Driver yönetimi

│   │   │   ├── 📂 steps   # Step implementation dosyaları

│   │   │   ├── 📂 utils   

│   │   │   ├── ElementInfo.java

│   │   ├── 📂 resources

│   │   │   ├── 📂 elementValues    # Element dosyaları

├── 📂 reports  # Test raporları

├── pom.xml  # Maven bağımlılıkları

├── log4j.properties  # Log yapılandırma dosyası

├── README.md  # Proje açıklaması

## 📌  Versiyon Bilgileri
* Java: 11+

* Maven: 3.8.1+

* Selenium: 4.0.0+

* Gauge Framework: 1.0.7

* ChromeDriver: 133.0.6943.54+

* Log4j : 2.17.2


## 🚀 Kurulum

1. Gereksinimler

Java 11 veya üstü

Maven (https://maven.apache.org/download.cgi)

Gauge Framework (https://docs.gauge.org/getting_started/installing-gauge.html)

Google Chrome ve ChromeDriver (https://sites.google.com/chromium.org/driver/downloads)

2. Projeyi Çalıştırma

# Depoyu klonlayın
$ git clone https://github.com/kullanici/proje-adi.git
$ cd proje-adi

# Bağımlılıkları yükleyin
$ mvn clean install

# Testleri çalıştırın
$ gauge run specs

# Testleri tag yapısına göre çalıştırın
$ gauge run specs --tags "regresyon"

## 📝 Test Senaryoları Yazma

Gauge, test senaryolarını .spec dosyaları içinde yazmamıza olanak tanır.
Örnek bir Gauge test senaryosu:

## 📊 Test Raporları

Test raporları, reports klasörüne kaydedilir. Aşağıdaki komut ile Gauge raporlarını görüntüleyebilirsiniz:

gauge run specs --report html

📌 Faydalı Komutlar

Gauge testlerini çalıştır: mvn test

Gauge testlerini belirli bir etiketle çalıştır: mvn test -Dtags="@Login"

Gauge raporlarını oluştur: mvn test -Dgauge_reports_dir=reports