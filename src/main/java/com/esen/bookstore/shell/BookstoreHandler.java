package com.esen.bookstore.shell;

import com.esen.bookstore.service.BookService;
import com.esen.bookstore.service.BookstoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Bookstore related commands")
@RequiredArgsConstructor
public class BookstoreHandler {
    private final BookstoreService bookstoreService;
    @ShellMethod(key = "create bookstore", value = "Create a bookstore")
    void createBookstore(String location, Double priceModifier, Double moneyInCashRegister) {
        bookstoreService.save(location, priceModifier, moneyInCashRegister);
    }

    @ShellMethod(key = "list bookstores", value = "List all bookstores")
    String listBookstores() {
        return bookstoreService.findAll().stream()
                .map(bookstore -> "ID: %d, Location: %s, Price modifier: %f, Money in cash register: %f".formatted(
                        bookstore.getId(),
                        bookstore.getLocation(),
                        bookstore.getPriceModifier(),
                        bookstore.getMoneyInCashRegister()
                )).collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "delete bookstore", value = "Delete a bookstore")
    void deleteBookstore(Long id) {
        bookstoreService.deleteBook(id);
    }

    @ShellMethod(key = "update bookstore", value = "Update a specific bookstore")
    void updateBookstore(Long id,
                    @ShellOption(defaultValue = ShellOption.NULL) String location,
                    @ShellOption(defaultValue = ShellOption.NULL) Double priceModifier,
                    @ShellOption(defaultValue = ShellOption.NULL) Double moneyInCashRegister) {
        bookstoreService.updateBook(id, location, priceModifier, moneyInCashRegister);
    }

    @ShellMethod(key = "find prices", value = "Find all the prices for a given book")
    String findPrices(Long id) {
        var priceMap = bookstoreService.findPrices(id);
        return priceMap.entrySet().stream()
                .map(priceEntry -> "Location: %10s, Price: %f".formatted(
                        priceEntry.getKey().getLocation(),
                        priceEntry.getValue()
                )).collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "list inventory", value = "Lists the contents of a bookstore's inventory")
    String listInventory(Long id) {
        var bookstoreInventory = bookstoreService.getInventoryById(id);
        return bookstoreInventory.entrySet().stream()
                .map(inventoryEntry -> "Book ID: %3d, Book Title: %20s, Quantity: %3d".formatted(
                        inventoryEntry.getKey().getId(),
                        inventoryEntry.getKey().getTitle(),
                        inventoryEntry.getValue()
                )).collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(key = "update inventory", value = "Update the contents of a bookstore's inventory")
    void updateInventory(Long bookstoreId, Long bookId, Integer amount) {
        bookstoreService.updateInventory(bookstoreId, bookId, amount);
    }
}
