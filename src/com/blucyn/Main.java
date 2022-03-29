package com.blucyn;

import java.util.ArrayList;

public class Main {

    /**************
     *
     * WHAT DO WE ACTUALLY NEED TO GET THE JOB DONE?
     *
     * - A game-board of either ints or chars
     *      - Has numbers representing the amount of mines surrounding the tile.
     * - We need a method who figures out how many unfulfilled values a tile has.
     *      - An unfulfilled tile equals to the # of mines surrounding minus # of mines found.
     * - Need a method who figures out how many unknown spaces are surrounding a tile.
     *
     * - Need another reflection of the game-board who represents probabilities of a tile being a mine.
     *      - Tiles who have already been uncovered, or have been flagged will be marked as 0%.
     *
     * - Need a method who updates the probabilities of unknown tiles adjacent to a given known tile.
     *
     *
     */


    // -------------------------- //
    // Global variables.

    // The game-board.
    // Should it be made of ints or chars ???
    // If ints, what will be the number for an unknown square? -1?
    // If char, we will need to make another method to convert each place into an int.
    public static char[][] gameBoard = new char[][]{

            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  '3',  '2',  '3',  '2',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  '2',  'm',  '2',  'm',  'm',  'm',  'm',  '2',  ' ',  ' ',  ' ',},
            {' ',  '1',  '1',  '2',  '1',  '2',  '2',  '3',  '3',  '2',  '3',  ' ',  ' ',  ' ',},
            {' ',  '1',  '0',  '0',  '1',  '1',  '1',  '0',  '0',  '1',  '3',  ' ',  ' ',  ' ',},
            {' ',  '2',  '1',  '1',  '2',  'm',  '2',  '1',  '1',  '1',  'm',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  'm',  '2',  '2',  '2',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},
            {' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',  ' ',},



    };


    // -------------------------- //
    // Main method.

    public static void main(String[] args) {

        boolean keepGoing = true;
        int count = 0;
        while(count < 10) {
            count ++;
            clearBoard();
            double[][] probBoard = getGameBoardProbabilities();
            double highest = -1;
            int highestRow = -1;
            int highestCol = -1;
            for (int row = 0; row < gameBoard.length; row++) {
                for (int col = 0; col < gameBoard[0].length; col++) {
                    double prob = probBoard[row][col];
                    if (prob > highest) {
                        highest = prob;
                        highestRow = row;
                        highestCol = col;
                    }
                }
            }
            if (highest == -1) {
                keepGoing = false;
            } else {
                gameBoard[highestRow][highestCol] = 'm';
            }
        }
        for(int row = 0; row < gameBoard.length; row++)
        {
            System.out.print("{");
            for (int col = 0; col < gameBoard[0].length; col++)
            {
                String asString = "'" + gameBoard[row][col] + "'";
                if(asString.equals("'g'")) {
                    System.out.print("\u001B[32m" + asString + ",\u001B[0m");

                }else if(asString.equals("'m'")){
                    System.out.print("\u001B[31m" + asString + ",\u001B[0m");

                }else{
                    System.out.print(asString + ",");
                }
                System.out.print("  ");


            }
            System.out.println("},");
        }

    }


    // -------------------------- //
    // Higher-level methods.

    // Returns the amount of mines who are not found surrounding a tile.
    public static int getUnfulfilled(int row, int col){

       // An unfulfilled tile equals to the # of mines surrounding minus # of mines found.
       int tileNumber  = getTileNumber(row, col);
       int minesAround = getSurrounding(
               row, col,
               true
       );
       return tileNumber - minesAround;

   }

    // Returns the probability value of all empty tiles surrounding a tile to be a mine.
    public static double getTileProbability(int row, int col) {

        if(gameBoard[row][col] == ' ' || gameBoard[row][col] == 'm')
        {
            return 0;
        }
        int unfulfilled    = getUnfulfilled(row, col);
        double emptySpaces = getSurrounding(row, col, false);
        if(emptySpaces == 0)
        {
            return 0;
        }
        return unfulfilled / emptySpaces;
    }

    // We must create a double game-board for probabilities, and update every single value.
    // We must loop through every single value, find its probability, then add that probability to every surrounding empty tile.
    public static double[][] getGameBoardProbabilities(){

        double[][] doubleBoard = new double[gameBoard.length][gameBoard[0].length];

        // Loop through all values.
        for(int row = 0; row < gameBoard.length; row++)
        {
            for(int col = 0; col < gameBoard[0].length; col++)
            {
                double prob = getTileProbability(row, col);

                // Add prob to all surrounding empty tiles.
                boolean upAllowed    = row != 0;
                boolean downAllowed  = row != gameBoard.length - 1;
                boolean rightAllowed = col != gameBoard[0].length - 1;
                boolean leftAllowed  = col != 0;

                if(upAllowed)
                {
                    if(gameBoard[row - 1][col] == ' ')
                    {
                        doubleBoard[row - 1][col] = addProbability(doubleBoard[row - 1][col], prob);
                    }
                }
                if(downAllowed)
                {
                    if(gameBoard[row + 1][col] == ' ')
                    {
                        doubleBoard[row + 1][col] = addProbability(doubleBoard[row + 1][col], prob);
                    }
                }
                if(leftAllowed)
                {
                    if(gameBoard[row][col - 1] == ' ')
                    {
                        doubleBoard[row][col - 1] = addProbability(doubleBoard[row][col - 1], prob);
                    }
                }
                if(rightAllowed)
                {
                    if(gameBoard[row][col + 1] == ' ')
                    {
                        doubleBoard[row][col + 1] = addProbability(doubleBoard[row][col + 1], prob);
                    }
                }

                // Check diagonals.
                // Upper left.
                if(upAllowed && leftAllowed)
                {
                    if(gameBoard[row - 1][col - 1] == ' ')
                    {
                        doubleBoard[row - 1][col - 1] = addProbability(doubleBoard[row - 1][col - 1], prob);
                    }
                }
                // Upper right.
                if(upAllowed && rightAllowed)
                {
                    if(gameBoard[row - 1][col + 1] == ' ')
                    {
                        doubleBoard[row - 1][col + 1] = addProbability(doubleBoard[row - 1][col + 1], prob);
                    }
                }
                // Lower left.
                if(downAllowed && leftAllowed)
                {
                    if(gameBoard[row + 1][col - 1] == ' ')
                    {
                        doubleBoard[row + 1][col - 1] = addProbability(doubleBoard[row + 1][col - 1], prob);
                    }
                }
                // Lower right.
                if(downAllowed && rightAllowed)
                {
                    if(gameBoard[row + 1][col + 1] == ' ')
                    {
                        doubleBoard[row + 1][col + 1] = addProbability(doubleBoard[row + 1][col + 1], prob);
                    }
                }


            }
        }

        return doubleBoard;

    }

    // If a tile is fulfilled, then give surrounding tiles a guess modifier.
    // A guess modifier means that there is no mine there.
    public static void clearBoard(){

        for(int row = 0; row < gameBoard.length; row++)
        {
            for (int col = 0; col < gameBoard[0].length; col++)
            {
                // If a tile is a number, and is all fulfilled, make all empty tiles surrounding it as a g.
                if(getTileNumber(row, col) > 0 && getUnfulfilled(row, col) == 0)
                {
                    // Add prob to all surrounding empty tiles.
                    boolean upAllowed    = row != 0;
                    boolean downAllowed  = row != gameBoard.length - 1;
                    boolean rightAllowed = col != gameBoard[0].length - 1;
                    boolean leftAllowed  = col != 0;

                    if(upAllowed)
                    {
                        if(gameBoard[row - 1][col] == ' ')
                        {
                            gameBoard[row - 1][col] = 'g';
                        }
                    }
                    if(downAllowed)
                    {
                        if(gameBoard[row + 1][col] == ' ')
                        {
                            gameBoard[row + 1][col] = 'g';

                        }
                    }
                    if(leftAllowed)
                    {
                        if(gameBoard[row][col - 1] == ' ')
                        {
                            gameBoard[row][col - 1] = 'g';

                        }
                    }
                    if(rightAllowed)
                    {
                        if(gameBoard[row][col + 1] == ' ')
                        {
                            gameBoard[row][col + 1] = 'g';
                        }
                    }

                    // Check diagonals.
                    // Upper left.
                    if(upAllowed && leftAllowed)
                    {
                        if(gameBoard[row - 1][col - 1] == ' ')
                        {
                            gameBoard[row - 1][col - 1] = 'g';
                        }
                    }
                    // Upper right.
                    if(upAllowed && rightAllowed)
                    {
                        if(gameBoard[row - 1][col + 1] == ' ')
                        {
                            gameBoard[row - 1][col + 1] = 'g';
                        }
                    }
                    // Lower left.
                    if(downAllowed && leftAllowed)
                    {
                        if(gameBoard[row + 1][col - 1] == ' ')
                        {
                            gameBoard[row + 1][col - 1] = 'g';

                        }
                    }
                    // Lower right.
                    if(downAllowed && rightAllowed)
                    {
                        if(gameBoard[row + 1][col + 1] == ' ')
                        {
                            gameBoard[row + 1][col + 1] = 'g';
                        }
                    }
                }
            }
        }


    }


    // -------------------------- //
    // Helper methods.

    // Methods to add probabilities together.
    // One will accept two double inputs.
    // The other will accept an n sized array of doubles.
    // Both will return the overall chance of success.
    public static double addProbability(double a, double b){

        return (a + b) - (a * b);

    }
    public static double addProbability(double[] doubles){

        double total = 0;
        for(double d : doubles)
        {
            total = addProbability(total, d);
        }
        return total;

    }
    public static double addProbability(ArrayList<Double> doubles){

        double total = 0;
        for(double d : doubles)
        {
            total = addProbability(total, d);
        }
        return total;

    }

    // Converts a char on the game-board to an int which represents how many mines are surrounding the tile.
    // Returns the numbers from 0 to 9, and otherwise returns -1 when a tile is unknown, and -2 is known to be a mine.
    public static int charToInt(char c){

        return (c == 109 || c == 77)? -2 : (c >= 48 && c <= 57)? c - 48 : -1;

    }

    // Returns the tile number of a spot on the board.
    public static int getTileNumber(int row, int col){

        return charToInt(gameBoard[row][col]);

    }

    // Returns the number of known mines surrounding a spot on the board.
    public static int getSurrounding(int row, int col, boolean searchForMines){

        char searchFor = searchForMines? 'm' : ' ';

        boolean upAllowed    = row != 0;
        boolean downAllowed  = row != gameBoard.length - 1;
        boolean rightAllowed = col != gameBoard[0].length - 1;
        boolean leftAllowed  = col != 0;

        // Check all available surrounding sections to see if their mines.
        int foundMines = 0;
        if(upAllowed)
        {
            if(gameBoard[row - 1][col] == searchFor)
            {
                foundMines++;
            }
        }
        if(downAllowed)
        {
            if(gameBoard[row + 1][col] == searchFor)
            {
                foundMines++;
            }
        }
        if(leftAllowed)
        {
            if(gameBoard[row][col - 1] == searchFor)
            {
                foundMines++;
            }
        }
        if(rightAllowed)
        {
            if(gameBoard[row][col + 1] == searchFor)
            {
                foundMines++;
            }
        }

        // Check diagonals.
        // Upper left.
        if(upAllowed && leftAllowed)
        {
            if(gameBoard[row - 1][col - 1] == searchFor)
            {
                foundMines++;
            }
        }
        // Upper right.
        if(upAllowed && rightAllowed)
        {
            if(gameBoard[row - 1][col + 1] == searchFor)
            {
                foundMines++;
            }
        }
        // Lower left.
        if(downAllowed && leftAllowed)
        {
            if(gameBoard[row + 1][col - 1] == searchFor)
            {
                foundMines++;
            }
        }
        // Lower right.
        if(downAllowed && rightAllowed)
        {
            if(gameBoard[row + 1][col + 1] == searchFor)
            {
                foundMines++;
            }
        }

        return foundMines;

    }

    // Rounds a number to a given amount of digits.
    public static double round(double d, int digits){

        double multiplier = Math.pow(10, digits);
        return Math.round(d * multiplier) / multiplier;

    }

    // Pads a string to a set length.
    public static String padString(String str, int padding){

        StringBuilder strBuilder = new StringBuilder(str);
        while(strBuilder.length() < padding)
        {
            strBuilder.append(" ");
        }
        str = strBuilder.toString();
        return str;

    }


}
