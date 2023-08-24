package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.CurrentPieceListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;
    /**
     * the game piece the player can play
     */
    public GamePiece currentPiece;
    /**
     * variable for storing next gamePiece
     */
    public GamePiece nextGamePiece;
    /**
     * the bindable int for score
     */
    public SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * bindable int for lives
     */
    public SimpleIntegerProperty lives = new SimpleIntegerProperty(3);
    /**
     * bindable int for multiplier
     */
    public SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);
    /**
     * bindable int for level
     */
    public SimpleIntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * The current piece the player will place
     */
    public NextPieceListener nextPieceListener;
    public CurrentPieceListener currentPieceListener;
    private GameBlockCoordinate gameBlockCoordinate;
    public LineClearedListener lineClearedListener;
    public GameLoopListener gameLoopListener;
    public Timer gameTimer;
    public GameOverListener gameOverListener;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.currentPiece = spawnPiece();
        this.nextGamePiece = nextPiece();
        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        this.nextPieceListener = null;
        this.lineClearedListener = null;
        this.gameLoopListener = null;
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        startGameLoop();
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x, y);
        int newValue = previousValue + 1;
        if (newValue > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        if(grid.canPlayPiece(currentPiece,x,y)) {
            grid.playPiece(currentPiece,x,y,currentPiece.getValue());
            //grid.set(x,y,newValue);
            this.currentPiece = nextGamePiece;
            this.nextGamePiece = nextPiece();
            if (this.nextPieceListener != null) {
                this.nextPieceListener.nextPiece(this.nextGamePiece);
            }
            if (this.currentPieceListener != null) {
                this.currentPieceListener.currentPiece(this.currentPiece);
            }

            afterPiece();
            Multimedia.playSound("place.wav");
        } else { Multimedia.playSound("fail.wav");}
    }


    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Makes the initial piece in the game
     * @return first game piece
     */
    public GamePiece spawnPiece() {
        logger.info("generating new piece");
        return GamePiece.createPiece((int)(Math.random() * (GamePiece.PIECES) ));
        //return GamePiece.createPiece(5);
    }

    /**
     * Generates a piece after one is placed
     * @return next piece to be played
     */
    public GamePiece nextPiece() {
        logger.info("new piece");
        return GamePiece.createPiece((int)(Math.random() * (GamePiece.PIECES) ));
        //return GamePiece.createPiece(5);

    }

    /**
     * Checks to see if any rows or columns need to be cleared
     */
    public void afterPiece() {
        //resetting the gameloop
        gameLoopReset();

        //check rows and columns, remove if necessary. Squares should not be counted twice
        int[] clearCol = new int[grid.getRows()];
        int[] clearRow = new int[grid.getCols()];
        int rows = 0;
        for(int i=0;i<grid.getRows();i++) {
            int count = 0;
            int count2 = 0;
            for(int j=0;j<grid.getCols();j++) {
                //System.out.println(i+"," +j);
                //System.out.println(grid.get(i,j));
                //System.out.println(grid.get(j,i));
                if(grid.get(i,j)>0) {
                    count+=1;
                }
                if(grid.get(j,i)>0) {
                    count2+=1;
                }
            }
            if(count == 5) {
                //System.out.println("Clear Vertical line");
                clearCol[i] = 1;
            } else {
                clearCol[i] = 0;
            }
            if(count2 == 5) {
                //System.out.println("Clear Horizontal Line");
                clearRow[i] = 1;
            } else {
                clearRow[i] = 0;
            }
        }
        int blocksRemoved = 0;
        int overlapping = 0;
        Set<GameBlockCoordinate> blocksFade = new HashSet<GameBlockCoordinate>();
        for (int i= 0; i < clearCol.length;i++) {
            if(clearCol[i]==1) {
                for(int j=0;j<clearRow.length;j++) {
                    //System.out.println(i+","+j);
                    GameBlockCoordinate gbc= new GameBlockCoordinate(i,j);
                    blocksFade.add(gbc);
                    grid.set(i,j, 0);
                }
                blocksRemoved+=5;
                overlapping+=1;
                rows+=1;
            }
        }
        for (int j= 0; j < clearRow.length;j++) {
            if(clearRow[j] == 1) {
                for(int i=0;i<clearCol.length;i++) {
                    //System.out.println(i+","+j);
                    GameBlockCoordinate gbc= new GameBlockCoordinate(i,j);
                    blocksFade.add(gbc);
                    grid.set(i,j,0);
                }
                blocksRemoved+=5-overlapping;
                rows+=1;
            }
        }
        //System.out.println("Blocks Removed = "+blocksRemoved);

        if(blocksRemoved>0) {
            if(this.lineClearedListener!=null) {
                logger.info("lineCleared!");
                this.lineClearedListener.lineCleared(blocksFade);
            }
        }
        score(blocksRemoved,rows);
    }



    public void score(int blocksRemoved,int rows) {
        if (this.gameLoopListener != null) {
            this.gameLoopListener.gameLoop(this.getTimeDelay());
        }
        score.set(score.get()+ blocksRemoved*10*multiplier.get()*rows);
        if(rows == 0) {
            multiplier.set(1);
        } else {
            Multimedia.playSound("clear.wav");
            multiplier.set(multiplier.get() + 1);
        }
        level.set((int) score.get()/1000);
    }

    /**
     * rotates current gamepiece
     */
    public void rotateCurrentPiece() {
        this.currentPiece.rotate();
        Multimedia.playSound("rotate.wav");
    }

    /**
     * method for switching each piece around, then calls listeners to change displays
     */
    public void swapCurrentPiece() {
        Multimedia.playSound("rotate.wav");
        logger.info("swapCurrentPiece called");
        GamePiece temp = this.currentPiece;
        this.currentPiece = this.nextGamePiece;
        this.nextGamePiece = temp;
    }
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }
    public void setCurrentPieceListener(CurrentPieceListener currentPieceListener) {
        this.currentPieceListener = currentPieceListener;
    }
    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }
    public void setOnGameLoop(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }
    public void setOnGameOver(GameOverListener gameOverListener) {
        this.gameOverListener = gameOverListener;
    }

    public void rotateCurrentPiece(int rot) {
        this.currentPiece.rotate(rot);
        Multimedia.playSound("rotate.wav");
    }
    public int getTimeDelay() {
        if (2500 > 12000 -(500*this.level.get())) {
            return 2500;
        } return 12000-(500*this.level.get());
    }
    public void startGameLoop() {
        System.out.println("starting loop");

        TimerTask task = new TimerTask() {
            public void run() {
                gameLoop();
            }
        };
        gameTimer = new Timer("Timer");
        gameTimer.schedule(task,this.getTimeDelay());
        if(this.gameLoopListener!=null) {
            this.gameLoopListener.gameLoop(this.getTimeDelay());
        }


    }
    public void gameLoop() {
        boolean gameEnded = false;
        logger.info("GameLoop ran");
        this.multiplier.set(1);
        this.lives.set(this.lives.get()-1);
        if(this.lives.get()<0) {
            this.gameTimer.cancel();
            if(this.gameOverListener!=null) {
                gameEnded = true;
                this.gameOverListener.GameOver();
            }
        } else {
            if (this.gameLoopListener != null) {
                this.gameLoopListener.gameLoop(this.getTimeDelay());
            }
            this.currentPiece = nextGamePiece;
            this.nextGamePiece = nextPiece();
            if (this.nextPieceListener != null) {
                this.nextPieceListener.nextPiece(this.nextGamePiece);
            }
            if (this.currentPieceListener != null) {
                this.currentPieceListener.currentPiece(this.currentPiece);
            }
            Multimedia.playSound("lifelose.wav");
            gameLoopReset();
        }
    }
    public void gameLoopReset(){
        logger.info("Resetting gameloop");
        this.gameTimer.cancel();
        if (this.gameLoopListener != null) {
            this.gameLoopListener.gameLoop(this.getTimeDelay());
        }
        startGameLoop();
    }
    public int getScore(){return this.score.get();}
}
