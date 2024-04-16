package com.esen.bookstore.shell;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Book related commands")
@RequiredArgsConstructor
public class BookHandler {
    private final BookService bookService;
    @ShellMethod(key = "create book", value = "Create a book")
    void createBook(String title, String author, String publisher, Double price) {
        bookService.save(Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .price(price)
                .build());
    }

    @ShellMethod(key = "list books", value = "List all books")
    String listBooks() {
        return bookService.findAll().stream()
                .map(book -> "ID: %d, Publisher: %s, Author: %s, Title: %s, Price: %f Ft".formatted(
                        book.getId(),
                        book.getPublisher(),
                        book.getAuthor(),
                        book.getTitle(),
                        book.getPrice()
                )).collect(Collectors.joining(System.lineSeparator()));
    }
}
