public class Board {
    private static Board uniqueBoard;

    // other variables to use in Board()

    private Board() {
        // Code to construct board
    }

    // To create instance of board
    public static Board getBoard() {
        if (uniqueBoard == null) {
            uniqueBoard = new Board();
        }
        return uniqueBoard;
    }
}
