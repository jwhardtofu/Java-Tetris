package tetris;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private static final Point[][][] Tetraminos = {
        // I-Piece
        {
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
        },

        // J-Piece
        {
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
        },

        // L-Piece
        {
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
                { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
        },

        // O-Piece
        {
                { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
        },

        // S-Piece
        {
                { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
        },

        // T-Piece
        {
                { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
        },

        // Z-Piece
        {
                { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
        }
    };

    private static final Color[] tetraminoColors = {
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private static final int PERIOD_INTERVAL = 500;

    private boolean isPaused;
    private int idx, rotate, score;
    private Color[][] board;
    private Random rand;
    private Point pieceOrigin;
    private Color background;
    private Timer timer;
    private int speed;

    public Tetris() {
        // System.out.println("in contructor");
        init();
        // System.out.println("ppoint 2");
    }

    private void init() {
        rand = new Random();
        score = 0;
        board = new Color[12][24];
        background = Color.WHITE;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    board[i][j] = Color.BLACK;
                } else {
                    board[i][j] = background;
                }
            }
        }
        if(!isCollided(5, 2, 0)) {
            gameStart();
        }
    }

    private void gameStart() {
        speed = 500;
        timer = new Timer(speed, new GameCycle());
        timer.start();
        // System.out.println(timer);
    }

    private void newPiece() {
        idx = rand.nextInt(7);
        rotate = 0;
        pieceOrigin = new Point(5, 2);
        if (isCollided(pieceOrigin.x, pieceOrigin.y, rotate)) {
            timer.stop();
        }
    }

    private void rotate(int i) {
        int newRotate = rotate + i < 0 ? 3 : (rotate + i) % 4;

        if (!isCollided(pieceOrigin.x, pieceOrigin.y, newRotate)) {
            rotate = newRotate;
            repaint();
        }
        // System.out.println(newRotate + " " + rotate);
    }

    private void move(int i) {
        if (!isCollided(pieceOrigin.x + i, pieceOrigin.y, rotate)) {
            pieceOrigin.x += i;
            repaint();
        }
        // System.out.println(i);
    }

    private boolean isCollided(int x, int y, int newRotate) {
        for (Point p : Tetraminos[idx][newRotate]) {
            if (board[p.x + x][p.y + y] != background) return true;
        }
        return false;
    }

    private void oneLineDown() {
        if (pieceOrigin == null) {
            newPiece();
        }
        if (!isCollided(pieceOrigin.x, pieceOrigin.y + 1, rotate)) {
            pieceOrigin.y++;
        }
        else {
            fixToBottom();
        }
        repaint();
    }

    private void dropDown() {
        oneLineDown();
    }

    private void pause() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        }
        else {
            timer.restart();
        }
    }

    private void fixToBottom() {
        for (Point p : Tetraminos[idx][rotate]) {
            board[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[idx];
        }
        clear();
        newPiece();
    }

    private void clear() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (board[i][j] == background) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        // calculate scores
        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }

        if (numClears > 0 && score >= 100 && speed >= 80) {
            speed *= Math.pow(0.9, numClears);
            timer.setDelay(speed);
        }
    }

    public void deleteRow(int row) {
        for (int j = row-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                board[i][j+1] = board[i][j];
            }
        }
    }

    // keep the game running
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameCycle();
            // System.out.println(timer.getDelay());
        }
    }

    private void gameCycle() {
        oneLineDown();
    }


    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[idx]);
        for (Point p : Tetraminos[idx][rotate]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        // Paint the board
        g.fillRect(0, 0, 26*12, 26*23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(board[i][j]);
                g.fillRect(26*i, 26*j, 25, 25);
            }
        }

        // Display the score
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 19*10, 25);

        // Draw the currently falling piece
        if (pieceOrigin != null) drawPiece(g);
    }

    public static void main(String[] args) {
        Tetris game = new Tetris();
        JFrame f = new JFrame("Tetris");
        f.add(game);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*26+10, 26*23+25);
        f.setVisible(true);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        // game.rotate(+1);
                        game.dropDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.dropDown();
                        break;

                    case KeyEvent.VK_P:
                        game.pause();
                        break;

                    //restart
                    case KeyEvent.VK_R:
                        game.init();
                        f.add(game);
                        break;
                    //fulldropdown
                    case KeyEvent.VK_C:
                    case KeyEvent.VK_V:
                        game.dropDown();
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
    }

}
