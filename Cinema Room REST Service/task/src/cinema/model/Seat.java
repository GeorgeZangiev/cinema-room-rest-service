package cinema.model;

import lombok.Data;

@Data
public class Seat {

    private int row;
    private int column;
    private int price;

    public Seat(int row, int column) {
        this.row = row;
        this.column = column;
        this.price = row <= 4 ? 10 : 8;
    }
}
