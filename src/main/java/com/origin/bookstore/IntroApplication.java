package com.origin.bookstore;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Example Book");
            book.setAuthor("Bob");
            book.setDescription("This is a test book");
            book.setPrice(BigDecimal.valueOf(14.99));
            book.setIsbn("987-654-321-0123");

            bookService.save(book);
            bookService.findAll().forEach(System.out::println);
        };
    }
}
