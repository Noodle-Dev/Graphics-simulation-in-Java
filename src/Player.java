import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

import javax.swing.*;

public class Player extends JPanel implements ActionListener, KeyListener{
    // Window Count
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;
        int startX;
        int startY;

        char direction = 'U';
        int velocity_X = 0;
        int velocity_Y = 0;


        public Block(Image image, int x, int y, int height, int width){
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;
        }
        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocity_X;
            this.y += this.velocity_Y;
            for(Block wall : walls){
                if(collision(this, wall)){
                    this.x -= this.velocity_X;
                    this.y -= this.velocity_Y;
                    this.direction = prevDirection;
                    updateVelocity();
                    break;
                }
            } 
        }
        void updateVelocity(){
            if(this.direction == 'U'){
                this.velocity_X = 0;
                this.velocity_Y = -tileSize/4;
            }
            else if(this.direction == 'D'){
                this.velocity_X = 0;
                this.velocity_Y = tileSize/4;
            }
            else if(this.direction == 'L'){
                this.velocity_X = -tileSize/4;
                this.velocity_Y = 0;
            }
            else if(this.direction == 'R'){
                this.velocity_X = tileSize/4;
                this.velocity_Y = 0;
            }
        }
        
    }
    
    //Rows and columns
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    //General Assets
    private Image wallImg;
    private Image enemieImg;

    //Player directions assets
    private Image playerUpImg;
    private Image playerDownImg;
    private Image playerLeftImg;
    private Image playerRightImg;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X       b P       X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X                 X",
        "X              b  X",
        "X                 X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    }; //Los plebes andan ondeados


    HashSet<Block> walls;
    HashSet<Block> powers;
    HashSet<Block> enemies;
    Block player;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //Up Down Left Right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    

    Player() {
        //Player instace
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //Load Assets
        
        //Assets images Loaded
        wallImg = new ImageIcon(getClass().getResource("./assets/wall.png")).getImage();
        enemieImg = new ImageIcon(getClass().getResource("./assets/enemie.png")).getImage();

        //Player directions loaded
        playerUpImg = new ImageIcon(getClass().getResource("./assets/playerUp.png")).getImage();
        playerDownImg = new ImageIcon(getClass().getResource("./assets/playerDown.png")).getImage();
        playerLeftImg = new ImageIcon(getClass().getResource("./assets/playerLeft.png")).getImage();
        playerRightImg = new ImageIcon(getClass().getResource("./assets/playerRight.png")).getImage();

        LoadMap();
        for(Block enemie : enemies){
            char newDirection = directions[random.nextInt(4)];
            enemie.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this); // 50 ms per frame (20fps)
        gameLoop.start();

    }
    public void LoadMap(){
        //Load the map using the string XXXXXXXX NIGGA
        walls = new HashSet<Block>();
        powers = new HashSet<Block>();
        enemies = new HashSet<Block>();

        //Detect chars from the tileset X E b
        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < columnCount; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if(tileMapChar == 'X'){ // Draw walls on the screen
                    Block wall = new Block(wallImg, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar == 'b'){ // Draw enemies on the screen
                    Block enemie = new Block(enemieImg, x, y, tileSize, tileSize);
                    enemies.add(enemie);
                }
                else if(tileMapChar == 'P'){
                    player = new Block(playerRightImg, x, y, tileSize, tileSize);
                }
                //Add power ups later in
            }
        }
    }
    //Paint components
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    //Draw graphics
    public void draw(Graphics g){
        g.drawImage(player.image, player.x, player.y, player.width, player.height, null);

        for(Block enemie : enemies){
            g.drawImage(enemie.image, enemie.x, enemie.y, enemie.width, enemie.height, null);
        }
        for(Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }   
    }

    //Move player and detect collisions
    public void move(){
        player.x += player.velocity_X;
        player.y += player.velocity_Y;
        
        //Check for collisions
        for(Block wall : walls){
            if(collision(player, wall)){
                player.x -= player.velocity_X;
                player.y -= player.velocity_Y;
                break;
            }
        }

        //Move enemies
        for(Block enemie : enemies){
            enemie.x += enemie.velocity_X;
            enemie.y += enemie.velocity_Y;
            if(enemie.y == tileSize*9 && enemie.direction != 'U' && enemie.direction != 'D'){
                enemie.updateDirection('D');
            }
            for(Block wall : walls){
            if(collision(enemie, wall) || enemie.x <= 0 || enemie.x + enemie.width >= boardWidth){
                    enemie.x -= enemie.velocity_X;
                    enemie.y -= enemie.velocity_Y;
                    char newDirection = directions[random.nextInt(4)];
                    enemie.updateDirection(newDirection);
                    break;
                }
            }
        }
    }
    public boolean collision(Block a, Block b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }
    //Actions listener method
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    //KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP){
            player.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            player.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            player.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.updateDirection('R');
        }

        if(player.direction == 'U'){
            player.image = playerUpImg;
        }
        else if(player.direction == 'D'){
            player.image = playerDownImg;
        }
        else if(player.direction == 'L'){
            player.image = playerLeftImg;
        }
        else if(player.direction == 'R'){
            player.image = playerRightImg;
        }
    }

    

}
