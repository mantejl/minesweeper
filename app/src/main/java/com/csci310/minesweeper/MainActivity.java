package com.csci310.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public int clockTimer = 0;
    public boolean gameRunning = false;
    public boolean gameOver = false;
    public boolean start = false;
    public int flagCounter = 4;
    public boolean placingFlags = true;
    public int correctSquares = 0;
    public boolean userWon = false;

    // storing the cell that is clicked for later on
    private ArrayList<TextView> cell_tvs = new ArrayList<TextView>();
    private ArrayList<Integer> placedBombs = new ArrayList<Integer>();

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add 120 dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i <= 11; i++) {
            for (int j = 0; j <= 9; j++) {
                TextView tv = new TextView(this);
                tv.setHeight(dpToPixel(30));
                tv.setWidth(dpToPixel(30));
                tv.setTextSize(20);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::clickButton);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);
                cell_tvs.add(tv);
            }
        }
        if (savedInstanceState != null) {
            clockTimer = savedInstanceState.getInt("clockTimer");
            gameRunning = savedInstanceState.getBoolean("gameRunning");
        }
        makeGrid();
        gameRunning = true;
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clockTimer", clockTimer);
        savedInstanceState.putBoolean("running", gameRunning);
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n < cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void showAdjacentCells(int index) {
        ArrayList<Integer> adjacentCells = new ArrayList<Integer>();
        adjacentCells.add(index);
        addAdjacentMines(index, adjacentCells);
        for (int i = 0; i < adjacentCells.size(); i++) {
            if (adjacentCells.get(i) >= 0 && adjacentCells.get(i) < 120) {
                ColorDrawable cell = (ColorDrawable) cell_tvs.get(adjacentCells.get(i)).getBackground();
                if (!cellHasMine(adjacentCells.get(i)) && cell.getColor() != Color.LTGRAY) {
                    correctSquares++;
                    cell_tvs.get(adjacentCells.get(i)).setBackgroundColor(Color.LTGRAY);
                    if (cell_tvs.get(adjacentCells.get(i)).getText().toString().equals("")) {
                        showAdjacentCells(adjacentCells.get(i));
                    }
                }
            }
        }
    }


    public void makeGrid() {
        placeMines();
        for (int i = 0; i <=119; i++) {
            if (!cellHasMine(i)) {
                int bombCounter = 0;
                ArrayList<Integer> cells = new ArrayList<Integer>();
                cells.add(i);
                addAdjacentMines(i, cells);
                for (int j: cells) {
                    if (j >= 0 && j <= 119) {
                        if (cellHasMine(j)) {
                            bombCounter++;
                        }
                    }
                }
                if (bombCounter > 0 ) {
                    cell_tvs.get(i).setText(String.valueOf(bombCounter));
                } else {
                    cell_tvs.get(i).setText("");
                }
             }
        }

    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < 4) {
            int randomIndex = random.nextInt(119);
            if (!cellHasMine(randomIndex)) {
                minesPlaced++;
                placedBombs.add(randomIndex);
            }
        }
    }


    public void addAdjacentMines(int index, ArrayList<Integer> mines) {
        int row = index / 10;
        int col = index % 10;

        // Check and add adjacent cells within the grid boundaries
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < 12 && j >= 0 && j < 10) {
                    int adjacentIndex = i * 10 + j;
                    mines.add(adjacentIndex);
                }
            }
        }
    }

    public boolean cellHasMine(int index) {
        return placedBombs.contains(index);
    }


    public void clickButton(View view){
        start = true;
        TextView tv = (TextView) view;
        if (!placingFlags) {
            int n = findIndexOfCellTextView(tv);
            if (cellHasMine(n)) {
                tv.setText("\uD83D\uDCA3");
                for (int i = 0; i < placedBombs.size(); i++) {
                    cell_tvs.get(i).setBackgroundColor(Color.LTGRAY);
                }
                gameOver = true;
                userWon = false;
                result();
            }
            tv.setBackgroundColor(Color.LTGRAY);
            correctSquares++;
            if (tv.getText().toString().equals("")) {
                showAdjacentCells(n);
            }
            if (correctSquares >= 116) {
                for (Integer i: placedBombs) {
                    cell_tvs.get(i).setBackgroundColor(Color.LTGRAY);
                    cell_tvs.get(i).setText("\uD83D\uDCA3");
                }
                userWon = true;
                gameOver = true;
                result();
            }
        } else {
            if (tv.getText().toString().equals("\uD83D\uDEA9")) {
                tv.setText("");
                flagCounter++;
                final TextView timeView = (TextView) findViewById(R.id.flagCount);
                timeView.setText(String.valueOf(flagCounter));
            } else {
                tv.setText("\uD83D\uDEA9");
                flagCounter--;
                TextView timeView = (TextView) findViewById(R.id.flagCount);
                timeView.setText(String.valueOf(flagCounter));
            }
        }
    }

    public void modeSwitch(View view) {
        Button button = findViewById(R.id.flagMode);
        if(placingFlags)
        {
            button.setText(R.string.tool);
            int counter = showNumber(5);
            placingFlags = false;
        }
        else {
            button.setText(R.string.flagIcon);
            int counter = showNumber(5);
            placingFlags = true;
        }
    }

    public int showNumber(int cells) {
        return cells;
    }

    public void result() {
        Intent ending = new Intent(this, ShowEnding.class);
        boolean win = checkWinCondition();
        ending.putExtra("timeTaken", String.valueOf(clockTimer));
        if (userWon == true) {
            ending.putExtra("winOrLose", "You won. \n Good job!");
        } else {
            ending.putExtra("winOrLose", "You lost. \n Better luck next time!");
        }
        startActivity(ending);
    }

    public boolean checkWinCondition() {
        int totalCells = cell_tvs.size();
        int totalRevealed = 0;
        int totalBombs = placedBombs.size();

        for (int i = 0; i < totalCells; i++) {
            TextView cell = cell_tvs.get(i);

            // Check if the cell is revealed and doesn't contain a bomb
            if (cell.getBackground() instanceof ColorDrawable) {
                ColorDrawable background = (ColorDrawable) cell.getBackground();
                int backgroundColor = background.getColor();

                if (backgroundColor == Color.LTGRAY && !cell.getText().toString().equals("\uD83D\uDCA3")) {
                    totalRevealed++;
                }
            }
        }

        // Check if all non-mine cells are revealed
        return totalRevealed == (totalCells - totalBombs);
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.timer);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int seconds = clockTimer % 60;
                String time = String.format("%02d", seconds);
                timeView.setText(time);

                if (gameRunning && gameOver == false && start == true) {
                    clockTimer++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}