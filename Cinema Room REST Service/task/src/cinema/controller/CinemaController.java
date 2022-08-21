package cinema.controller;

import cinema.model.Cinema;
import cinema.model.Seat;
import cinema.model.Stats;
import cinema.response.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class CinemaController {
    Cinema cinema = new Cinema();
    Stats stats = new Stats();
    ConcurrentHashMap<UUID, Seat> purchasedTickets = new ConcurrentHashMap<>();

    @GetMapping("/seats")
    public Cinema getCinema() {
        return cinema;
    }

    @PostMapping("/purchase")
    public ResponseEntity purchaseSeat(@RequestBody Seat seat) {
        if (seat.getRow() < 1 || seat.getRow() > cinema.getTotalRows() ||
                seat.getColumn() < 1 || seat.getColumn() > cinema.getTotalColumns()) {
            return ResponseEntity.badRequest().body(
                    new ConcurrentHashMap<>(Map.of("error", "The number of a row or a column is out of bounds!")));
        } else if (!cinema.getAvailableSeats().contains(seat)) {
            return ResponseEntity.badRequest().body(
                    new ConcurrentHashMap<>(Map.of("error", "The ticket has been already purchased!"))
            );
        }
        UUID token = UUID.randomUUID();
        cinema.deleteFromAvailableSeats(seat);
        purchasedTickets.put(token, seat);
        int income = stats.getCurrentIncome() + seat.getPrice();
        int purchasedTickets = stats.getNumberOfPurchasedTickets() + 1;
        stats.setCurrentIncome(income);
        stats.setNumberOfPurchasedTickets(purchasedTickets);
        return ResponseHandler.generateResponse(token, HttpStatus.OK, seat);
    }

    @PostMapping("/return")
    public ResponseEntity returnTicket(@RequestBody String stringToken) {
        UUID token = UUID.fromString(stringToken.split("\"")[3]);
        if (!purchasedTickets.containsKey(token)){
            return ResponseEntity.badRequest().body(
                    new ConcurrentHashMap<>(Map.of("error", "Wrong token!")));
        }
        Seat seat = purchasedTickets.get(token);
        purchasedTickets.remove(token);
        cinema.addToAvailableSeats(seat);
        int income = stats.getCurrentIncome() - seat.getPrice();
        int purchasedTickets = stats.getNumberOfPurchasedTickets() - 1;
        stats.setCurrentIncome(income);
        stats.setNumberOfPurchasedTickets(purchasedTickets);
        return ResponseEntity.ok().body(
                new ConcurrentHashMap<>(Map.of("returned_ticket", seat))
        );
    }

    @PostMapping("/stats")
    public ResponseEntity getStats(@RequestParam(name = "password", required = false) String password) {
        if (password == null || !password.equals("super_secret")){
            return new ResponseEntity<>(
                    new ConcurrentHashMap<>(Map.of("error", "The password is wrong!")),
                    HttpStatus.UNAUTHORIZED);
        }
        stats.setNumberOfAvailableSeats(cinema.getAvailableSeats().size());
        return new ResponseEntity<>(new ConcurrentHashMap<>(Map.of(
                "current_income", stats.getCurrentIncome(),
                "number_of_available_seats", stats.getNumberOfAvailableSeats(),
                "number_of_purchased_tickets", stats.getNumberOfPurchasedTickets())),
                HttpStatus.OK);
    }
}
