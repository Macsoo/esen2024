package com.esen.bookstore.service;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.model.Bookstore;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final BookstoreRepository bookstoreRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void removeBookFromInventories(Book book) {
        bookstoreRepository.findAll()
                .forEach(bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                });
    }

    public void save(String location, Double priceModifier, Double moneyInCashRegister) {
        bookstoreRepository.save(Bookstore.builder()
                .location(location)
                .priceModifier(priceModifier)
                .moneyInCashRegister(moneyInCashRegister)
                .build());
    }

    public List<Bookstore> findAll() {
        return bookstoreRepository.findAll();
    }

    public Map<Book, Integer> getInventoryById(Long id) {
        var bookstore = bookstoreRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find bookstore by id: %d".formatted(id)));
        return bookstore.getInventory();
    }

    public void deleteBook(Long id) {
        var bookstore = bookstoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find bookstore by id: %d".formatted(id)));
        bookstoreRepository.delete(bookstore);
    }

    public void updateBook(Long id, String location, Double priceModifier, Double moneyInCashRegister) {
        if (Stream.of(location, priceModifier, moneyInCashRegister).allMatch(Objects::isNull))
            throw new UnsupportedOperationException("There's nothing to update");
        var bookstore = bookstoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find bookstore by id: %d".formatted(id)));
        if (location != null) bookstore.setLocation(location);
        if (priceModifier != null) bookstore.setPriceModifier(priceModifier);
        if (moneyInCashRegister != null) bookstore.setMoneyInCashRegister(moneyInCashRegister);
        bookstoreRepository.save(bookstore);
    }

    public Map<Bookstore, Double> findPrices(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A book with the id %d doesn't exist".formatted(id)));
        Double bookPrice = book.getPrice();
        return findAll().stream().collect(Collectors.toMap(
                Function.identity(),
                bookstore -> bookstore.getPriceModifier() * bookPrice
        ));
    }

    public void updateInventory(Long bookstoreId, Long bookId, Integer amount) {
        if (amount < 0) throw new IllegalArgumentException("The book amount can't be negative");
        var bookstore = bookstoreRepository.findById(bookstoreId)
                .orElseThrow(() -> new RuntimeException("A bookstore with the id %d doesn't exist".formatted(bookstoreId)));
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("A book with the id %d doesn't exist".formatted(bookId)));
        var inventory = bookstore.getInventory();
        if (amount == 0) inventory.remove(book);
        else inventory.put(book, amount);
        bookstore.setInventory(inventory);
        bookstoreRepository.save(bookstore);
    }
}
