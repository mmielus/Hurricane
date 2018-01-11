package Panels;

import Forest.ForestType;
import Tree.TreeModel;
import Tree.TreeType;
import Components.Vortex;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class SimulatePanel extends JFrame {


    private final static Random generator = new Random();
    public TreeModel forest[][] = new TreeModel[800][600];
    // TODO unmock
    public Vortex hurricane = new Vortex(1, 1, 45, 100, 30, 30, 10);
    long l = System.currentTimeMillis();
    private BufferStrategy bufferstrat = null;
    private Canvas render;
    private int lit = 1;
    private int mnoz = 1;
    public CalibrationPanel test = new CalibrationPanel();


    public SimulatePanel() {

        super();
        setTitle("Hurricane");
        setIgnoreRepaint(true);
        setResizable(false);
        render = new Canvas();
        render.setIgnoreRepaint(true);
        setLayout(new BorderLayout());
        setBounds(50, 50, 800, 600);
        render.setBounds(50, 50, 800, 600);
        add(render, BorderLayout.WEST);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        render.createBufferStrategy(2);
        bufferstrat = render.getBufferStrategy();
    }

    /**
     * Metoda dodajaca drzewo do lasu
     *
     * @param x wspolrzedna
     * @param y wspolrzedna
     */
    public void addTree(int x, int y, ForestType forestType) {
        // TODO odhardkodowac
        forest[x][y] = createTree(x, y, forestType);
    }

    private TreeModel createTree(int x, int y, ForestType type) {
        TreeType treeType;
        switch (type) {
            case PINE:
                treeType = TreeType.PINE;
                break;
            case SPRUCE:
                treeType = TreeType.SPRUCE;
                break;
            default:
                treeType = randomTreeType();
        }

        return new TreeModel(x, y, treeType);
    }

    private TreeType randomTreeType() {
        return generator.nextBoolean() ? TreeType.PINE : TreeType.SPRUCE;
    }

    /**
     * Wyswietla las renderujac kazde drzewo po kolei.
     *
     * @param g2d
     */
    public void renderForest(Graphics2D g2d) {
        TreeModel tree = null;
        for (int i = 0; i < 800; i++)
            for (int j = 0; j < 600; j++)
                if (forest[i][j] != null) {
                    tree = forest[i][j];
                    tree.render(g2d);
                }


    }

    /**
     * Metoda tworzaca las
     */
    public void makeForest() {
        Random generator = new Random();
        int l = 0;
        while (test.getCheck() == false) {
            System.out.println(test.forestDensity);
        }
        while (l < test.getForestDensity()) {
            addTree(generator.nextInt(800), generator.nextInt(600), test.getForestType());
            l++;
        }
    }

    /**
     *
     */
    public void render() {
        do {
            do {
                Graphics2D g2d = (Graphics2D) bufferstrat.getDrawGraphics();
                g2d.fillRect(0, 0, render.getWidth(), render.getHeight());

                renderForest(g2d);

                g2d.dispose();
            } while (bufferstrat.contentsRestored());
            bufferstrat.show();
        } while (bufferstrat.contentsLost());
    }

    /**
     * tymczasowa metoda do przeitorowania po wszystkich drzewach
     * sprawdza ich stan przy okreslonej predkosci wiatru
     */
    public void simulate() {
        makeForest();
        render();

        while (!outOfBounds(hurricane.getCenter())) {

            for (int i = 0; i < 800; i++) {
                for (int j = 0; j < 600; j++) {
                    if (forest[i][j] != null) {
                        forest[i][j].interact(hurricane.getV(i, j));
                    }
                }
            }

            render();
            hurricane.recalculateCenter();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean outOfBounds(Point point) {
        return point.getX() > 800 || point.getY() > 600;
    }

    /**
     *
     */
    public void loop() {
        TreeModel tree = null;
        makeForest();
        while (true) {

/**
 * ta czesc ponizej jest od tego zeby zmienialo zielone kropki na czerwone
 * o ile sie nie myle to w tej czesci bedzie trzeba polaczyc to co napisal epy zeby jakos to symulowalo odpowiednio
 */
            if (l + 500 < System.currentTimeMillis()) {
                l = System.currentTimeMillis();
                mnoz++;
                int a = generator.nextInt(10) * mnoz;
                for (int i = 0; i < 580; i++) {
                    if (forest[a][i + 2] != null) {
                        tree = forest[a][i + 2];
                        tree.interact(test.getWindSpeed());
                    }
                }
            }
            lit++;
            render();

            try {
                Thread.sleep(1000 / 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}