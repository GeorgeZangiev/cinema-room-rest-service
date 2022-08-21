package cinema.model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cinema {

    private int totalRows = 9;
    private int totalColumns = 9;
    private List<Seat> availableSeats = new ArrayList<>();

    {
        for (int i = 1; i <= this.totalRows; i++) {
            for (int j = 1; j <= this.totalColumns; j++) {
                this.availableSeats.add(new Seat(i, j));
            }
        }
    }

    public boolean deleteFromAvailableSeats(Seat seat) {
        return this.availableSeats.remove(seat);
    }

    public boolean addToAvailableSeats(Seat seat) {
        return this.availableSeats.add(seat);
    }
}
