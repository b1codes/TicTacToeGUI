package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel {

    public ArrayList<GridSpace> grid;

    /*
    0 1 2
    3 4 5
    6 7 8
     */
    public Grid() {
        super();
        grid = new ArrayList<GridSpace>();
        for (int i = 0; i < 9; i++) {
            grid.add(new GridSpace(" ", i));
        }
        fillGrid();
    }

    public void updateGrid(int i, GridSpace newGridSpace) {
        grid.set(i, newGridSpace);
    }

    public void resetGrid() {
        this.setVisible(false);
        this.removeAll();
        grid = new ArrayList<GridSpace>();
        for (int i = 0; i < 9; i++) {
            grid.add(new GridSpace(" ", i));
        }
        fillGrid();
    }

    private void fillGrid() {
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new GridLayout(3, 3));
        GridSpace topLeft = grid.get(0);
        topLeft.setPreferredSize(new Dimension(100,100));
        this.add(topLeft);
        GridSpace topCenter = grid.get(1);
        topCenter.setPreferredSize(new Dimension(100,100));
        this.add(topCenter);
        GridSpace topRight = grid.get(2);
        topRight.setPreferredSize(new Dimension(100,100));
        this.add(topRight);

        GridSpace middleLeft = grid.get(3);
        middleLeft.setPreferredSize(new Dimension(100,100));
        this.add(middleLeft);
        GridSpace middleCenter = grid.get(4);
        middleCenter.setPreferredSize(new Dimension(100,100));
        this.add(middleCenter);
        GridSpace middleRight = grid.get(5);
        middleRight.setPreferredSize(new Dimension(100,100));
        this.add(middleRight);

        GridSpace bottomLeft = grid.get(6);
        bottomLeft.setPreferredSize(new Dimension(100,100));
        this.add(bottomLeft);
        GridSpace bottomCenter = grid.get(7);
        bottomCenter.setPreferredSize(new Dimension(100,100));
        this.add(bottomCenter);
        GridSpace bottomRight = grid.get(8);
        bottomRight.setPreferredSize(new Dimension(100,100));
        this.add(bottomRight);
        this.setPreferredSize(new Dimension(300,300));
        this.setMaximumSize(new Dimension(300,300));
        this.setVisible(true);
    }
}
