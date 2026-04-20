# 📰 Web News Application

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2741C?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)

Курсовий проєкт з дисципліни **«Основи вебпрограмування»**.  
Виконав студент групи **ТВ-41** НН ІАТЕ КПІ ім. Ігоря Сікорського — **Кривдюк Архип**.

---

## 🚀 Про проєкт
**Web News Application** — це сучасна платформа для агрегації та управління новинним контентом. Система побудована за принципом багаторівневої архітектури, що забезпечує стабільність, безпеку та легку масштабованість.

### ✨ Основні можливості
* **Управління контентом:** Створення, редагування та категоризація новин (CRUD).
* **Інтерактивність:** Система коментарів для авторизованих користувачів.
* **Безпека:** Рольова модель доступу (Admin, User, Guest) на базі Spring Security.
* **Медіа-менеджмент:** Зберігання зображень у хмарі **Cloudinary** (CDN).
* **Зовнішні сервіси:** Динамічний віджет погоди через **OpenWeather API**.
* **Авторизація:** Вхід через Google аккаунт (**OAuth2**).

---

## 🛠 Технологічний стек

| Сфера | Технології |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security |
| **Database** | PostgreSQL, Hibernate ORM, HikariCP |
| **Integration** | Cloudinary API, OpenWeatherMap API, OAuth 2.0 |
| **Tools** | Maven, Lombok, MapStruct, PlantUML |
| **Design** | CSS Grid, Flexbox, Responsive UI |

---

## 🏗 Архітектура системи
Додаток реалізовано за стандартом **Multitier Architecture**:



1.  **Web Layer (`Controller`):** Обробка HTTP-запитів та повернення REST-відповідей.
2.  **Service Layer (`Service`):** Бізнес-логіка, валідація даних та взаємодія з зовнішніми API.
3.  **Data Access Layer (`Repository`):** Взаємодія з БД через абстракції Spring Data JPA.
4.  **Domain Layer (`Model`):** Сутності бази даних та DTO для безпечного обміну даними.

---

## 📂 Структура репозиторію
```text
src/main/java/ua/kpi/newsapp/
├── 📁 config/      # Налаштування Security, Cloudinary та Beans
├── 📁 controller/  # REST-контролери (API Endpoints)
├── 📁 dto/         # Об'єкти передачі даних (Data Transfer Objects)
├── 📁 mapper/      # Мапінг між Entity та DTO (MapStruct)
├── 📁 model/       # Сутності БД (Entities)
├── 📁 repository/  # Інтерфейси доступу до даних
└── 📁 service/     # Класи з бізнес-логікою
