package com.altherwy.rfidsimulator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
//import javax.swing.border.StrokeBorder;

public class MapEditor {

    public static String currentSelectedButton = "Block";

    public static final int SPACE = 0; // empty/space
    public static final int BLOCK = 1;  // block
    public static final int FIRST_PATH = 2; // first person path ( path point)
    public static final int SECOND_PATH = 3; // second person path ( path point);
    public static final int READER = 4; // second person path ( path point);
    public static final int ms = 5; // second person path ( path point);
    public static final int COMMON_PATH = 6;

    public static final Color SPACE_COLOR = Color.white;
    public static final Color BLOCK_COLOR = Color.darkGray;
    public static final Color FIRST_PATH_COLOR = Color.blue;
    public static final Color SECOND_PATH_COLOR = Color.RED;
    public static final Color READER_COLOR = Color.GREEN;
    public static final Color MS_COLOR = Color.PINK;
    public static final Color Netural = Color.YELLOW;
    

    public static Vector<Vector<Integer>> readers;
    public static Vector<Vector<Integer>> MS;
    public static Vector<Vector<Integer>> person1;
    public static Vector<Vector<Integer>> realPerson1;

    // public static void main(String[] args) {
     //   new MapEditor();
    //}

    public MapEditor(Vector<Vector<Integer>> readers, Vector<Vector<Integer>> MS, Vector<Vector<Vector<Integer>>> trackingResults) {
        MapEditor.readers = readers;
        MapEditor.MS = MS;
        Vector<Vector<Integer>> person1 = new Vector<Vector<Integer>>();
        Vector<Vector<Integer>> realPerson1= new Vector<Vector<Integer>>();
        int resultIndex = 1;
        if(resultIndex == 1){
        person1 = trackingResults.get(2);
    	realPerson1 = trackingResults.get(4);
        }
        else{
        person1 = trackingResults.get(3);
        realPerson1 = trackingResults.get(5);
        }
    	//Vector<Vector<Integer>> realPerson1 = trackingResults.get(3); // for 5 readers
    	
        MapEditor.person1 = person1;
        MapEditor.realPerson1 = realPerson1;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new MainPanel());
                frame.pack();
                frame.setSize(700, 700);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class CanvasPanel extends JPanel {

        private int columnCount;
        private int rowCount;
        private int cellSize;
        private List<Rectangle> cells;
        private Point selectedCell;
        private int map[][];
        private Vector<Point> person1Points;
        private Vector<Point> realPerson1Points;
        public CanvasPanel() {

            this.map = new int[][] {
            	//   0 1 2 3 4  5 6 7 8 9 1011121314 1516171819 2021222324 2526272829 3031323334
                    {1,1,1,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,1,1,1,0, 0,0,0,0,0, 0,0,0,1,1, 1,1,1,1,1},//0
                    {1,1,1,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,1,1,1,0, 0,0,0,0,0, 0,0,0,1,1, 1,1,1,1,1},//1
                    {1,1,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,1,1,1,0, 0,0,0,0,0, 1,1,1,0,0, 3,0,0,1,1},//2
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,1,1,1,0, 0,0,0,0,0, 1,1,1,0,0, 0,0,2,1,1},//3

                    {1,1,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,1,1,1,2, 0,0,0,0,0, 1,1,1,0,0, 0,0,3,1,1},//4
                    {1,1,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,1,1,2,0, 0,0,0,1,1},//5
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,1,1,0,0, 0,0,0,0,0},//6
                    {0,0,0,3,0, 0,0,0,2,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},//7


                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,2,1,1},//8
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,3,1,1},//9
                    {1,1,1,1,1, 1,1,1,1,1 ,1,1,1,1,1, 1,0,0,0,0, 0,0,0,0,0, 0,1,1,0,0, 0,0,0,1,1},//10
                    {0,0,1,1,0, 0,0,0,0,1 ,1,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},//11

                    {0,0,0,0,0, 3,3,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},//12
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 1,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},//13
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 1,1,0,0,0, 1,1,1,1,1, 1,0,0,0,0, 0,0,0,0,0},//14
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,0,0,0,0, 0,0,0,0,4},//15
                    
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,0,0,0,0, 0,0,0,0,6},//16
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 1,0,0,0,0, 0,0,0,0,0},//17
                    {0,0,0,0,0, 0,0,0,0,0 ,0,0,0,0,0, 1,0,6,0,0, 0,0,0,0,0, 1,1,1,0,0, 0,0,0,0,1},//18
                    {0,0,0,0,0, 0,3,0,0,0 ,0,0,0,0,0, 1,1,1,1,0, 0,0,0,3,0, 1,1,1,0,3, 0,0,0,3,0},//19
                    
                    {0,0,0,0,0, 1,1,0,0,0 ,0,0,0,0,0, 1,1,1,1,0, 0,0,0,0,0, 1,1,1,0,0, 0,0,0,0,0},//20


            };

            this.drawReaders();
            this.drawMS();
            this.drawPerson1();
            this.drawRealPerson1Points();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    CanvasPanel panel = (CanvasPanel) e.getComponent();
                   // panel.updateCell(e.getPoint());
                }
            });

        }

        public void drawReaders(){
            for(Vector<Integer> v : MapEditor.readers){
                  int row = v.get(0);
                  int col = v.get(1);
                  this.map[row][col] = READER;
            }
        }
        public void drawMS(){
            for(Vector<Integer> v : MapEditor.MS){
                  int row = v.get(0);
                  int col = v.get(1);
                  if(row != 15 && col != 34)
                  this.map[row][col] =ms;
            }
        }

        public void drawPerson1(){
            person1Points = new Vector<Point>();
            for(Vector<Integer> v : MapEditor.person1){
                  int row = v.get(1);
                  int col = v.get(2);
                  person1Points.add(new Point(col,row));
            }

        }

        public void drawRealPerson1Points(){
            realPerson1Points = new Vector<Point>();
            for(Vector<Integer> v : MapEditor.realPerson1){
                  int row = v.get(1);
                  int col = v.get(2);
                realPerson1Points.add(new Point(col,row));
            }

        }


        public void updateCell(Point point) {
            int cellIndexCol = (int) Math.floor(point.x / this.cellSize);
            int cellIndexRow = (int) Math.floor(point.y / this.cellSize);
            int val = SPACE;
            if (MapEditor.currentSelectedButton.equals("Block")) {
                val = BLOCK;
            } else if (MapEditor.currentSelectedButton.equals("Space")) {
                val = SPACE;
            } else if (MapEditor.currentSelectedButton.equals("FirstPath")) {
                val = FIRST_PATH;
            } else if (MapEditor.currentSelectedButton.equals("SecondPath")) {
                val = SECOND_PATH;
            }
            this.map[cellIndexRow][cellIndexCol] = val;
            this.repaint();
        }

        @Override
        public void invalidate() {
            super.invalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();


            int width = this.getWidth();
            int height = this.getHeight();
            int rowCount = map.length;
            int colCount = map[0].length;

            this.cellSize = width / (colCount);
            int offsetX = 0;
            int offsetY = 0;


            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Rectangle cell = new Rectangle();
                    cell.setLocation(offsetX, offsetY);
                    cell.setSize(cellSize, cellSize);

                    switch (map[row][col]) {
                        case SPACE:
                            g2d.setColor(SPACE_COLOR);
                            g2d.draw(cell);
                            break;
                        case BLOCK:
                            g2d.setColor(BLOCK_COLOR);
                            g2d.fill(cell);
                            break;
                        case FIRST_PATH:
                            g2d.setColor(FIRST_PATH_COLOR);
                            g2d.fill(cell);
                            break;
                        case SECOND_PATH:
                            g2d.setColor(SECOND_PATH_COLOR);
                            g2d.fill(cell);
                            break;
                        case READER:
                            g2d.setColor(READER_COLOR);
                            g2d.fill(cell);
                            break;
                        case ms:
                            g2d.setColor(MS_COLOR);
                            g2d.fill(cell);
                            break;
                        case COMMON_PATH:
                            g2d.setColor(Netural);
                            g2d.fill(cell);
                            break;
                    }
                    offsetX += cellSize;
                }
                offsetY += cellSize;
                offsetX = 0;
            }

            for(int i=0;i<person1Points.size();i++){
                if(i+1 >= person1Points.size())
                    break;
                this.drawPath(g2d,person1Points.get(i),person1Points.get(i+1),Color.red);
            }

            for(int i=0;i<realPerson1Points.size();i++){
                if(i+1 >= realPerson1Points.size())
                	break;
                
                this.drawPath(g2d,realPerson1Points.get(i),realPerson1Points.get(i+1),Color.BLUE);
                
            }

            g2d.dispose();
        }

        public void drawPath(Graphics2D g,Point point1,Point point2,Color color) {
            g.setColor(color);
            g.setStroke(new BasicStroke(2));
            g.drawLine(point1.x * cellSize + cellSize / 2, point1.y * cellSize + cellSize / 2, point2.x * cellSize + cellSize / 2, point2.y * cellSize + cellSize / 2);
        }
    }

    public class MainPanel extends JPanel implements ActionListener {

        protected String newline = "\n";


        public MainPanel() {
            super(new BorderLayout());

            //Create the toolbar.
            JToolBar toolBar = new JToolBar("Still draggable");
            addButtons(toolBar);

            //add(toolBar, BorderLayout.PAGE_START);
            add(new CanvasPanel(), BorderLayout.CENTER);
        }

        protected void addButtons(JToolBar toolBar) {

            JButton loadMapButton = new JButton("Run");
            loadMapButton.setActionCommand("LoadMap");
            loadMapButton.addActionListener(this);

            JButton saveMapButton = new JButton("Save Map");
            saveMapButton.setActionCommand("SaveMap");
            saveMapButton.addActionListener(this);


            JButton saveAsImageButton = new JButton("Same As Image");
            saveAsImageButton.setActionCommand("SaveAsImage");
            saveAsImageButton.addActionListener(this);

            JButton runButton = new JButton("Run");
            runButton.setActionCommand("Run");
            runButton.addActionListener(this);


            ButtonGroup group = new ButtonGroup();
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JToggleButton tb1 = new JToggleButton("Block");
            tb1.setActionCommand("Block");
            tb1.addActionListener(this);
            tb1.setSelected(true);
            MapEditor.currentSelectedButton = "Block";

            JToggleButton tb2 = new JToggleButton("Space");
            tb2.setActionCommand("Space");
            tb2.addActionListener(this);

            JToggleButton tb3 = new JToggleButton("First Path");
            tb3.setActionCommand("FirstPath");
            tb3.addActionListener(this);

            JToggleButton tb4 = new JToggleButton("Second Path");
            tb4.setActionCommand("SecondPath");
            tb4.addActionListener(this);


            group.add(tb1);
            panel.add(tb1);
            group.add(tb2);
            panel.add(tb2);
            group.add(tb3);
            panel.add(tb3);
            group.add(tb4);
            panel.add(tb4);

            toolBar.add(loadMapButton);
            toolBar.add(saveMapButton);
            toolBar.add(saveAsImageButton);
            toolBar.add(runButton);
            toolBar.add(panel);

        }


        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            String description = null;

            if ("Block".equals(cmd)) {
                MapEditor.currentSelectedButton = "Block";
            } else if ("Space".equals(cmd)) {
                MapEditor.currentSelectedButton = "Space";
            } else if ("FirstPath".equals(cmd)) {
                MapEditor.currentSelectedButton = "FirstPath";
            } else if ("SecondPath".equals(cmd)) {
                MapEditor.currentSelectedButton = "SecondPath";
            }

        }

        protected void displayResult(String actionDescription) {
            JOptionPane.showMessageDialog(this, actionDescription);

        }

    }

}
