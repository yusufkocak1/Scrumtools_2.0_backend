# Scrum Tools Backend

Scrum takımları için kapsamlı bir araç seti sunan Spring Boot tabanlı backend uygulaması.

## 🚀 Özellikler

- **Takım Yönetimi**: Scrum takımlarını oluşturma ve yönetme
- **Planning Poker**: Sprint planlama için story point tahmin aracı
- **Retrospektif**: Sprint retrospektif oturumları ve aksiyon öğeleri yönetimi
- **Gerçek Zamanlı İletişim**: WebSocket tabanlı anlık güncellemeler
- **Kullanıcı Kimlik Doğrulama**: JWT tabanlı güvenli authentication
- **Takım Rolleri**: Admin ve üye rolleri ile yetki yönetimi

## 🛠 Teknolojiler

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security** - Kimlik doğrulama ve yetkilendirme
- **Spring Data JPA** - Veritabanı işlemleri
- **WebSocket** - Gerçek zamanlı iletişim
- **JWT** - Token tabanlı authentication
- **PostgreSQL** - Veritabanı (varsayılan)
- **Docker** - Containerization

## 📋 Önkoşullar

- Java 21+
- Maven 3.6+
- PostgreSQL 12+ (veya Docker)
- Docker (opsiyonel)

## 🚀 Kurulum ve Çalıştırma

### Docker ile Çalıştırma (Önerilen)

```bash
# Projeyi klonlayın
git clone <repository-url>
cd scrum-tools-backend

# Docker Compose ile çalıştırın
docker-compose up -d
```

### Manuel Kurulum

1. **Veritabanını Kurun**
   ```bash
   # PostgreSQL'i kurun ve çalıştırın
   createdb scrum_tools
   ```

2. **Uygulama Ayarlarını Yapılandırın**
   ```bash
   # src/main/resources/application.properties dosyasını düzenleyin
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

3. **Uygulamayı Çalıştırın**
   ```bash
   # Maven ile çalıştırın
   ./mvnw spring-boot:run
   
   # Veya JAR dosyası oluşturup çalıştırın
   ./mvnw clean package
   java -jar target/scrum-tools-backend-0.0.1-SNAPSHOT.jar
   ```

## 🔧 Yapılandırma

### Uygulama Profilleri

- `local` - Yerel geliştirme ortamı
- `dev` - Geliştirme ortamı
- `prod` - Üretim ortamı

### Veritabanı Migrasyonları

Proje, aşağıdaki SQL dosyalarıyla veritabanı şemasını kurmanızı sağlar:

- `init.sql` - Temel tablo yapısı
- `team_invite_migration.sql` - Takım davet sistemi
- `team_roles_migration.sql` - Takım rolleri
- `poker_migration.sql` - Planning poker özellikleri
- `retrospective_migration.sql` - Retrospektif özellikleri
- `test_data_setup.sql` - Test verileri

## 📖 API Dokümantasyonu

### Kimlik Doğrulama Endpoints

```
POST /api/auth/signup    - Yeni kullanıcı kaydı
POST /api/auth/login     - Kullanıcı girişi
```

### Takım Yönetimi Endpoints

```
GET    /api/teams                    - Kullanıcının takımlarını listele
POST   /api/teams                    - Yeni takım oluştur
GET    /api/teams/{id}               - Takım detaylarını getir
POST   /api/teams/{id}/members       - Takıma üye ekle
PUT    /api/teams/{id}/approve       - Üye onaylama
```

### Planning Poker Endpoints

```
GET    /api/poker/sessions           - Poker oturumlarını listele
POST   /api/poker/sessions           - Yeni poker oturumu oluştur
POST   /api/poker/vote               - Oy ver
GET    /api/poker/results/{id}       - Oturum sonuçlarını getir
```

### Retrospektif Endpoints

```
GET    /api/retrospective/sessions   - Retrospektif oturumlarını listele
POST   /api/retrospective/sessions   - Yeni retrospektif oturumu oluştur
POST   /api/retrospective/items      - Retrospektif öğesi ekle
POST   /api/retrospective/actions    - Aksiyon öğesi oluştur
```

## 🔌 WebSocket Endpoints

- `/ws/poker` - Planning poker için gerçek zamanlı güncellemeler

## 🧪 Test

```bash
# Tüm testleri çalıştır
./mvnw test

# Belirli bir test sınıfını çalıştır
./mvnw test -Dtest=ScrumToolsBackendApplicationTests
```

## 📁 Proje Yapısı

```
src/
├── main/
│   ├── java/com/kocak/scrumtoolsbackend/
│   │   ├── config/          # Yapılandırma sınıfları
│   │   ├── controller/      # REST kontrolcüleri
│   │   ├── dto/            # Veri transfer objeleri
│   │   ├── entity/         # JPA entity'leri
│   │   ├── repository/     # Veritabanı repository'leri
│   │   ├── service/        # İş mantığı servisleri
│   │   └── util/           # Yardımcı sınıflar
│   └── resources/
│       ├── application.properties
│       └── application-{profile}.properties
└── test/                   # Test sınıfları
```

## 🤝 Katkıda Bulunma

1. Projeyi fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje MIT Lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 📞 İletişim

Proje hakkında sorularınız için issue açabilirsiniz.

## 🔄 Versiyon Geçmişi

- **v0.0.1-SNAPSHOT** - İlk geliştirme versiyonu
  - Temel takım yönetimi
  - Planning poker özellikleri
  - Retrospektif sistemi
  - JWT authentication
  - WebSocket desteği
