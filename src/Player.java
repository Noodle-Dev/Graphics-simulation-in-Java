import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Player extends JPanel implements ActionListener, KeyListener {
    // Clase para representar bloques (jugador, enemigos, paredes)
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        int startX;
        int startY;

        char direction = 'U'; // Dirección actual
        int velocity_X = 0;   // Velocidad en X
        int velocity_Y = 0;   // Velocidad en Y

        public Block(Image image, int x, int y, int height, int width) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            // Calcular nueva posición
            int newX = this.x + this.velocity_X;
            int newY = this.y + this.velocity_Y;

            // Verificar colisión antes de mover
            boolean collided = false;
            for (Block wall : walls) {
                if (newX < wall.x + wall.width && newX + this.width > wall.x &&
                    newY < wall.y + wall.height && newY + this.height > wall.y) {
                    collided = true;
                    break;
                }
            }

            // Mover solo si no hay colisión
            if (!collided) {
                this.x = newX;
                this.y = newY;
            } else {
                this.direction = prevDirection;
                updateVelocity();
            }

            // Cambiar la imagen según la dirección
            switch (direction) {
                case 'U':
                    this.image = playerUpImg;
                    break;
                case 'D':
                    this.image = playerDownImg;
                    break;
                case 'L':
                    this.image = playerLeftImg;
                    break;
                case 'R':
                    this.image = playerRightImg;
                    break;
            }
        }

        void updateVelocity() {
            // Actualizar velocidad según la dirección
            if (this.direction == 'U') {
                this.velocity_X = 0;
                this.velocity_Y = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocity_X = 0;
                this.velocity_Y = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocity_X = -tileSize / 4;
                this.velocity_Y = 0;
            } else if (this.direction == 'R') {
                this.velocity_X = tileSize / 4;
                this.velocity_Y = 0;
            }
        }
    }

    // Configuración del juego
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    // Assets del juego
    private Image wallImg;
    private Image enemieImg;
    private Image playerUpImg;
    private Image playerDownImg;
    private Image playerLeftImg;
    private Image playerRightImg;

    // Mapa del juego
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X     X     X     X",
        "X XXX X XXX X XXX X",
        "X X   X   X X   X X",
        "X X XXXXX X XXXXX X",
        "X X       X       X",
        "X XXXXXXXXX XXXXXXX",
        "X                 X",
        "X XXX XXXXX XXX XXX",
        "X   X       X     X",
        "Xb  X   P   X  b  X",
        "X   X       X     X",
        "X XXX XXXXX XXX XXX",
        "X                 X",
        "X XXXXXXXXX XXXXXXX",
        "X X       X       X",
        "X X XXXXX X XXXXX X",
        "X X   X   X   X   X",
        "X XXX X XXX X XXX X",
        "X     X     X     X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    // Conjuntos para almacenar objetos del juego
    HashSet<Block> walls;
    HashSet<Block> powers;
    HashSet<Block> enemies;
    Block player;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; // Direcciones posibles
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    // Conjunto para rastrear teclas presionadas
    private HashSet<Integer> keysPressed = new HashSet<>();

    // Constructor
    Player() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        setDoubleBuffered(true); // Habilita el doble búfer

        // Cargar imágenes
        wallImg = new ImageIcon(getClass().getResource("./assets/wall.png")).getImage();
        enemieImg = new ImageIcon(getClass().getResource("./assets/enemie.png")).getImage();
        playerUpImg = new ImageIcon(getClass().getResource("./assets/playerUp.png")).getImage();
        playerDownImg = new ImageIcon(getClass().getResource("./assets/playerDown.png")).getImage();
        playerLeftImg = new ImageIcon(getClass().getResource("./assets/playerLeft.png")).getImage();
        playerRightImg = new ImageIcon(getClass().getResource("./assets/playerRight.png")).getImage();

        // Cargar el mapa
        LoadMap();

        // Iniciar el bucle del juego
        gameLoop = new Timer(45, this); // 60 FPS
        gameLoop.start();
    }

    // Cargar el mapa
    public void LoadMap() {
        walls = new HashSet<>();
        powers = new HashSet<>();
        enemies = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    walls.add(new Block(wallImg, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'b') {
                    Block enemie = new Block(enemieImg, x, y, tileSize, tileSize);
                    enemies.add(enemie);
                    char newDirection = directions[random.nextInt(4)];
                    enemie.updateDirection(newDirection);
                } else if (tileMapChar == 'P') {
                    player = new Block(playerRightImg, x, y, tileSize, tileSize);
                }
            }
        }
    }

    // Dibujar el juego
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Dibujar todos los elementos
    public void draw(Graphics g) {
        g.drawImage(player.image, player.x, player.y, player.width, player.height, null);
        for (Block enemie : enemies) {
            g.drawImage(enemie.image, enemie.x, enemie.y, enemie.width, enemie.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
    }

    // Mover jugador y enemigos
    public void move() {
        if (keysPressed.contains(KeyEvent.VK_UP)) {
            player.updateDirection('U');
        } else if (keysPressed.contains(KeyEvent.VK_DOWN)) {
            player.updateDirection('D');
        } else if (keysPressed.contains(KeyEvent.VK_LEFT)) {
            player.updateDirection('L');
        } else if (keysPressed.contains(KeyEvent.VK_RIGHT)) {
            player.updateDirection('R');
        }

        // Mover enemigos
        for (Block enemie : enemies) {
            enemie.x += enemie.velocity_X;
            enemie.y += enemie.velocity_Y;

            // Verificar colisión con paredes
            for (Block wall : walls) {
                if (collision(enemie, wall)) {
                    enemie.x -= enemie.velocity_X;
                    enemie.y -= enemie.velocity_Y;
                    char newDirection = directions[random.nextInt(4)];
                    enemie.updateDirection(newDirection);
                    break;
                }
            }
        }
    }

    // Verificar colisión entre dos bloques
    public boolean collision(Block a, Block b) {
        return !(a.x + a.width <= b.x || a.x >= b.x + b.width || a.y + a.height <= b.y || a.y >= b.y + b.height);
    }

    // Bucle del juego
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    // Manejo de teclas
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    // Método principal
    public static void main(String[] args) {
        JFrame frame = new JFrame("Juego de Laberinto");
        Player game = new Player();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}