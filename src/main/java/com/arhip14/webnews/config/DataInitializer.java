package com.arhip14.webnews.config;

import com.arhip14.webnews.entity.Category;
import com.arhip14.webnews.entity.News;
import com.arhip14.webnews.entity.User;
import com.arhip14.webnews.repository.CategoryRepository;
import com.arhip14.webnews.repository.NewsRepository;
import com.arhip14.webnews.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CategoryRepository categoryRepo,
                                      UserRepository userRepo,
                                      NewsRepository newsRepo,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Якщо таблиця категорій порожня - значить це перший запуск, наповнюємо БД!
            if (categoryRepo.count() == 0) {

                // 1. Створюємо категорії
                Category tech = new Category(); tech.setName("Технології");
                Category sport = new Category(); sport.setName("Спорт");
                Category politics = new Category(); politics.setName("Політика");
                categoryRepo.saveAll(List.of(tech, sport, politics));

                // 2. Створюємо базового автора (Адміністратора)
                User admin = new User();
                admin.setEmail("admin@kpi.ua");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Головний Редактор");
                admin.setRole(User.Role.ADMIN);
                userRepo.save(admin);

                // 3. Створюємо тестові новини
                News n1 = new News();
                n1.setTitle("🎉 Відкриття нового IT-хабу на базі ІАТЕ!");
                n1.setContent("Сьогодні в Навчально-науковому інституті атомної та теплової енергетики відкрили новий коворкінг для студентів спеціальності 121 (Інженерія програмного забезпечення). Локація обладнана всім необхідним для комфортного кодингу.");
                n1.setCategory(tech);
                n1.setAuthor(admin);

                News n2 = new News();
                n2.setTitle("🏆 Збірна КПІ перемогла у турнірі");
                n2.setContent("Команда нашого університету здобула розгромну перемогу у фіналі всеукраїнського турніру. Вітаємо чемпіонів та бажаємо нових звершень!");
                n2.setCategory(sport);
                n2.setAuthor(admin);

                newsRepo.saveAll(List.of(n1, n2));

                System.out.println("✅ Базові категорії, Адмін та тестові новини успішно додані в БД!");
            }
        };
    }
}