import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        // Window Count
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        //Window Setup
        JFrame frame = new JFrame("Graphic Sim");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Player playerGame = new Player();
        frame.add(playerGame);
        frame.pack();
        playerGame.requestFocus();
        frame.setVisible(true);
    }
}
