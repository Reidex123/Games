import javax.swing.JFrame;

public class MainWindow {
    final static int ROW_COUNT = 21;
    final static int TILE_SIZE = 32;
    final static int COLUMN_COUNT = 19;
    public static void main(String[] args){
        final int boardWidth = COLUMN_COUNT * TILE_SIZE;
        final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;

        JFrame frame = new JFrame("Pac Man");

        frame.setSize(boardWidth, BOARD_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan(ROW_COUNT, COLUMN_COUNT, TILE_SIZE, boardWidth, BOARD_HEIGHT);
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }

}
