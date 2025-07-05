package purepursitmodel2;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class PPM extends JFrame {

    private JButton jButton1;
    private JLabel jLabel1;
    private DrawPanel drawPanel;

    // Simulation variables
    private int delay = 50;
    private Random rand = new Random();
    private int vf, beta, c;
    private double alpha;
    private double theta;
    private boolean caught = false;
    private int xb, yb, xf, yf;
    private Thread simThread;

    public PPM() {
        initComponents();
        setupSimulation();
    }

    private void initComponents() {
        jLabel1 = new JLabel("PURE PURSUIT MODEL");
        jButton1 = new JButton("START SIMULATION");
        drawPanel = new DrawPanel();

        jButton1.addActionListener(e -> startSimulation());

        JPanel topPanel = new JPanel();
        topPanel.add(jLabel1);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(jButton1);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupSimulation() {
        vf = 3 + rand.nextInt(5);
        beta = 20 + rand.nextInt(51);
        c = 300;
        alpha = 0;
        xf = 50; yf = 100;
        xb = 100; yb = 300;
        theta = 0;
        caught = false;
    }

    private void startSimulation() {
        // Stop any running simulation
        if (simThread != null && simThread.isAlive()) {
            simThread.interrupt();
        }

        setupSimulation();

        simThread = new Thread(() -> {
            for (int i = 0; i < 1000 && !caught && !Thread.currentThread().isInterrupted(); i++) {

                // Bomber movement (wave)
                xb += 2;
                alpha += 0.1;
                yb = (int) (beta * Math.sin(alpha) + c);

                // Fighter movement
                theta = Math.atan2(yb - yf, xb - xf);
                xf += (int) (vf * Math.cos(theta));
                yf += (int) (vf * Math.sin(theta));

                // Catch condition
                int dx = xb - xf;
                int dy = yb - yf;
                if (dx * dx + dy * dy < 400) {
                    caught = true;
                    JOptionPane.showMessageDialog(null, "🎯 Bomber Caught! Fighter Wins!");
                    break;
                }

                if (xb > 700) {
                    caught = true;
                    JOptionPane.showMessageDialog(null, "💨 Bomber Escaped! Fighter Loses!");
                    break;
                }

                drawPanel.repaint();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        simThread.start();
    }

    // Inner class for drawing
    class DrawPanel extends JPanel {
        public DrawPanel() {
            setPreferredSize(new Dimension(800, 500));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Axis
            g.setColor(Color.GRAY);
            g.drawLine(20, 400, 750, 400);
            g.drawLine(40, 60, 40, 450);

            // Buildings
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(100, 350, 50, 50);
            g.fillRect(150, 300, 30, 100);
            g.fillRect(230, 340, 50, 60);
            g.fillRect(300, 370, 50, 30);
            g.fillRect(400, 300, 50, 100);
            g.fillRect(500, 200, 70, 200);

            // Fighter (Blue)
            g.setColor(Color.BLUE);
            drawFighter(g, xf, yf);

            // Bomber (Red)
            g.setColor(Color.RED);
            drawBomber(g, xb, yb);
        }

        private void drawFighter(Graphics g, int x, int y) {
            g.drawLine(x, y, x + 10, y + 10); // diag tail
            g.drawLine(x, y, x, y + 20);      // straight back
            g.drawLine(x + 10, y + 10, x + 30, y + 10);
            g.drawLine(x + 30, y + 10, x + 40, y + 20);  // nose
            g.drawLine(x, y + 20, x + 40, y + 20);       // base
            g.fillRect(x + 6, y + 12, 5, 5);
            g.fillRect(x + 12, y + 12, 5, 5);
            g.fillRect(x + 18, y + 12, 5, 5);
            g.fillRect(x + 24, y + 12, 5, 5);
        }

        private void drawBomber(Graphics g, int x, int y) {
            g.drawLine(x, y, x + 10, y + 10);
            g.drawLine(x, y, x, y + 20);
            g.drawLine(x + 10, y + 10, x + 30, y + 10);
            g.drawLine(x + 30, y + 10, x + 40, y + 20);
            g.drawLine(x, y + 20, x + 40, y + 20);
            g.fillOval(x + 6, y + 12, 5, 5);
            g.fillOval(x + 12, y + 12, 5, 5);
            g.fillOval(x + 18, y + 12, 5, 5);
            g.fillOval(x + 24, y + 12, 5, 5);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PPM().setVisible(true));
    }
}
