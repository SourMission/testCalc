import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// 実行: javac Tetris.java && java Tetris  (←→移動 ↑回転 ↓落下 Space即落下)
public class Tetris extends JPanel implements ActionListener {
    static final int W = 10, H = 20, S = 30;
    static final int[][][] P = {
        {{0,0},{1,0},{2,0},{3,0}}, {{0,0},{1,0},{0,1},{1,1}}, {{1,0},{0,1},{1,1},{2,1}},
        {{1,0},{2,0},{0,1},{1,1}}, {{0,0},{1,0},{1,1},{2,1}}, {{0,0},{0,1},{1,1},{2,1}},
        {{2,0},{0,1},{1,1},{2,1}}};
    static final Color[] C = {Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE};
    int[][] b = new int[H][W];
    int[][] cur; int type, x, y, score;
    boolean over;
    Random r = new Random();
    Timer t = new Timer(500, this);

    Tetris() {
        setPreferredSize(new Dimension(W * S, H * S));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (over) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> move(-1, 0, cur);
                    case KeyEvent.VK_RIGHT -> move(1, 0, cur);
                    case KeyEvent.VK_DOWN -> drop();
                    case KeyEvent.VK_UP -> move(0, 0, rotate());
                    case KeyEvent.VK_SPACE -> { while (move(0, 1, cur)); drop(); }
                }
                repaint();
            }
        });
        spawn();
        t.start();
    }

    void spawn() {
        type = r.nextInt(7);
        cur = new int[4][];
        for (int i = 0; i < 4; i++) cur[i] = P[type][i].clone();
        x = 3; y = 0;
        if (!fits(x, y, cur)) { over = true; t.stop(); }
    }

    int[][] rotate() {
        int[][] n = new int[4][2];
        for (int i = 0; i < 4; i++) { n[i][0] = 1 - (cur[i][1] - 1); n[i][1] = cur[i][0]; }
        return type == 1 ? cur : n;
    }

    boolean fits(int nx, int ny, int[][] p) {
        for (int[] c : p) {
            int px = nx + c[0], py = ny + c[1];
            if (px < 0 || px >= W || py >= H || (py >= 0 && b[py][px] != 0)) return false;
        }
        return true;
    }

    boolean move(int dx, int dy, int[][] p) {
        if (!fits(x + dx, y + dy, p)) return false;
        x += dx; y += dy; cur = p;
        return true;
    }

    void drop() {
        if (move(0, 1, cur)) return;
        for (int[] c : cur) if (y + c[1] >= 0) b[y + c[1]][x + c[0]] = type + 1;
        int n = 0;
        for (int row = H - 1; row >= 0; row--) {
            boolean full = true;
            for (int v : b[row]) if (v == 0) full = false;
            if (full) { System.arraycopy(b, 0, b, 1, row); b[0] = new int[W]; row++; n++; }
        }
        score += new int[]{0, 100, 300, 500, 800}[n];
        spawn();
    }

    public void actionPerformed(ActionEvent e) { drop(); repaint(); }

    void cell(Graphics g, int cx, int cy, int v) {
        g.setColor(C[v - 1]);
        g.fillRect(cx * S, cy * S, S - 1, S - 1);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < H; i++) for (int j = 0; j < W; j++) if (b[i][j] != 0) cell(g, j, i, b[i][j]);
        if (!over) for (int[] c : cur) cell(g, x + c[0], y + c[1], type + 1);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 5, 15);
        if (over) g.drawString("GAME OVER", W * S / 2 - 30, H * S / 2);
    }

    public static void main(String[] a) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Tetris");
            f.add(new Tetris());
            f.pack();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);
            f.setVisible(true);
        });
    }
}
