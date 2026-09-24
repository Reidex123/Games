import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    class Block {
        private int x;
        private int y;
        private int width;
        private int height;

        private int startX;
        private int startY;

        private Image image;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        public Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x += this.velocityX;
                    this.y += this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if (this.direction == 'U') {
                velocityX = 0;
                velocityY = -tileSize / 4;
            }
            else if (this.direction == 'D') {
                velocityX = 0;
                velocityY = tileSize / 4;
            }
            else if (this.direction == 'R') {
                velocityX = tileSize / 4;
                velocityY = 0;
            }
            else if (this.direction == 'L') {
                velocityX = -tileSize / 4;
                velocityY = 0;
            }
        }
    }

    private int rowCount;
    private int columnCount;
    private int tileSize;
    private int boardWidth;
    private int boardHeight;

    private Image wallImage;

    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' };
    Random random = new Random();

    //Ghosts: b = blue, o = orange, p = pink, r = red
    //X = wall, O = skip, P = pac man, ' ' = food
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    public PacMan(int rowCount, int columnCount, int tileSize, int boardWidth, int boardHeight) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.tileSize = tileSize;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        this.wallImage = new ImageIcon(getClass().getResource("./imgs/wall.png")).getImage();

        this.blueGhostImage = new ImageIcon(getClass().getResource("./imgs/blueGhost.png")).getImage();
        this.orangeGhostImage = new ImageIcon(getClass().getResource("./imgs/orangeGhost.png")).getImage();
        this.redGhostImage = new ImageIcon(getClass().getResource("./imgs/redGhost.png")).getImage();
        this.pinkGhostImage = new ImageIcon(getClass().getResource("./imgs/pinkGhost.png")).getImage();

        this.pacmanUpImage = new ImageIcon(getClass().getResource("./imgs/pacmanUp.png")).getImage();
        this.pacmanDownImage = new ImageIcon(getClass().getResource("./imgs/pacmanDown.png")).getImage();
        this.pacmanLeftImage = new ImageIcon(getClass().getResource("./imgs/pacmanLeft.png")).getImage();
        this.pacmanRightImage = new ImageIcon(getClass().getResource("./imgs/pacmanRight.png")).getImage();

        this.loadMap();

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        this.walls = new HashSet<>();
        this.foods = new HashSet<>();
        this.ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = this.tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * this.tileSize;
                int y = r * this.tileSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(this.wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check for collision
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    break;
                }
            }
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}
