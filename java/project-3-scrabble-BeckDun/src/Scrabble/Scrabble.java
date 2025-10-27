package Scrabble;
/**
 * Classic scrabble game using javaFX
 * How to play instructions pop up before the user starts playing the game
 * @author BeckDun
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class Scrabble extends Application {
    private BorderPane root;
    private ScrabbleGame game;
    private GridPane boardGrid;
    private HBox playerTray;
    private ArrayList<Button> playerTrayButtons;

    private ArrayList<Letter> playerHand;
    private ArrayList<Letter> computerHand;

    private Label gameStatusLabel;
    private Label playerScoreLabel;
    private Label computerScoreLabel;
    private int playerScore = 0;
    private int computerScore = 0;

    private Button[][] tileButtons;
    private Button selectedTrayButton;
    private HashMap<Position, Letter> currentMove;

    private Button submitMoveButton;
    private Button exchangeAllButton;
    private Button passButton;

    private Button exchangeButton;
    private GridPane exchangeArea;
    private Button[][] exchangeTileButtons;
    private HashMap<Position, Letter> exchangeTiles = new HashMap<>();
    private Button rulesButton;

    private boolean isFirstMove = true;

    /** Pixel size for each tile */
    private static final int TILE_SIZE = 40;
    /** Size of the scrabble board */
    private final int boardSize = 15;


    public static void main(String[] args) {launch(args);}

    /**
     * Initializes and starts the application.
     * Sets up the game board, player tray, controls, and initial game state.
     * @param primaryStage The primary stage for this application
     * @throws Exception If an error occurs during initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        computerHand = new ArrayList<>();

        primaryStage.setTitle("Scabble");
        setupGame(primaryStage);

        createGameBoard();
        createPlayerTray();
        createControls();
        createStatus();
        createExchangeArea();
        root = new BorderPane();

        HBox bottomPanel = new HBox(10);
        bottomPanel.getChildren().addAll(submitMoveButton, exchangeAllButton,passButton);
        bottomPanel.setAlignment(Pos.CENTER);
        VBox bottom = new VBox();
        bottom.getChildren().addAll(playerTray, bottomPanel);
        bottom.setBackground(new Background(new BackgroundFill(Color.TAN,
                CornerRadii.EMPTY,Insets.EMPTY)));

        HBox topPanel = new HBox(15);
        topPanel.getChildren().addAll(playerScoreLabel,computerScoreLabel);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));
        topPanel.setBackground(new Background(new BackgroundFill(Color.TAN,
                CornerRadii.EMPTY,Insets.EMPTY)));

        VBox left = new VBox(35);
        left.getChildren().add(rulesButton);
        left.getChildren().add(exchangeButton);
        left.getChildren().add(exchangeArea);
        left.setBackground(new Background(new BackgroundFill(Color.TAN,
                CornerRadii.EMPTY,Insets.EMPTY)));

        VBox right = new VBox();
        right.getChildren().add(new Rectangle(100,25, Color.TAN));
        right.setBackground(new Background(new BackgroundFill(Color.TAN,
                CornerRadii.EMPTY,Insets.EMPTY)));

        root.setLeft(left);
        root.setRight(right);
        root.setTop(topPanel);
        root.setBottom(bottom);
        root.setCenter(boardGrid);
        root.setBackground(new Background(new BackgroundFill(Color.FORESTGREEN,
                CornerRadii.EMPTY,Insets.EMPTY)));

        Scene scene = new Scene(root,900,800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


        dealPlayerTiles();
        updateGUI();

        handleRules();
    }

    /**
     * Updates the graphical user interface to reflect the current game state.
     * Updates the board display and player's tray.
     */
    private void updateGUI() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Tile tile = game.getBoard()[row][col];
                Button button = tileButtons[row][col];

                if (tile.isEmpty()) {
                    button.setText("");
                } else {
                    Character c = tile.getLetter().getChar();
                    button.setText(String.valueOf(c).toUpperCase());
                    button.setStyle("-fx-background-color: " +
                            "#F5DEB3; -fx-font-weight: bold;");
                }
            }
        }
        playerTray.getChildren().clear();
        playerTrayButtons.clear();

        for (Letter letter : playerHand) {
            String displayText;
            if(letter.isBlank()) {
                displayText = "";
            } else {
                Character c = letter.getChar();
                displayText = String.valueOf(c);
            }
            Button tileButton = new Button(displayText);

            // For blank tiles, show a special visual
            if (letter.getChar() == '*') {
                tileButton.setText("_");
                tileButton.setStyle("-fx-background-color: " +
                        "#F5DEB3; -fx-font-weight: bold; -fx-text-fill: #888888;");
            } else {
                tileButton.setText(String.valueOf(Character.toUpperCase(letter.getChar())));
                tileButton.setStyle("-fx-background-color: #F5DEB3; -fx-font-weight: bold;");
            }

            tileButton.setPrefSize(TILE_SIZE, TILE_SIZE);
            tileButton.setOnAction(e -> handleTrayTileClick(tileButton));

            // Add the letter's point value as a small subscript
            VBox letterBox = new VBox(0);
            letterBox.setAlignment(Pos.CENTER);
            letterBox.getChildren().add(tileButton);

            // Add a small label with the point value
            if (letter.getChar() != '*') {
                Label pointsLabel = new Label(String.valueOf(letter.getPoints()));
                pointsLabel.setFont(Font.font("Arial", 8));
                pointsLabel.setAlignment(Pos.BOTTOM_RIGHT);
                letterBox.getChildren().add(pointsLabel);
            }

            playerTrayButtons.add(tileButton);
            playerTray.getChildren().add(tileButton);
        }
    }


    /**
     * Deals tiles to the player from the bag.
     * Fills the player's hand to 7 tiles if possible.
     */
    private void dealPlayerTiles() {
        playerHand.clear();
        ArrayList<Letter> bag = game.getBag();

        Random random = new Random();
        for (int i = 0; i < 7 && !bag.isEmpty(); i++) {
            int index = random.nextInt(bag.size());
            playerHand.add(bag.remove(index));
        }
    }


    /**
     * Creates the exchange area for players to exchange less than 7 tiles
     */
    private void createExchangeArea() {
        exchangeArea = new GridPane();
        exchangeArea.setAlignment(Pos.CENTER);
        exchangeArea.setHgap(3);
        exchangeArea.setVgap(3);
        exchangeArea.setPadding(new Insets(10));
        exchangeArea.setBorder(new Border(new BorderStroke(Color.DARKGRAY,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        exchangeTileButtons = new Button[7][1];

        for (int row = 0; row < 7; row++) {
            Button exchangeSlot = new Button();
            exchangeSlot.setPrefSize(TILE_SIZE, TILE_SIZE);
            exchangeSlot.setStyle("-fx-background-color: " +
                    "#E0E0E0; -fx-border-color: #A0A0A0;");

            final int finalRow = row;
            exchangeSlot.setOnAction(e -> handleExchangeSlotClick(finalRow, 0));

            exchangeTileButtons[row][0] = exchangeSlot;
            exchangeArea.add(exchangeSlot, 0, row);
        }

        exchangeButton = new Button("Exchange Tiles");
        exchangeButton.setOnAction(e -> handleExchangeSelectedTiles());
        exchangeButton.setMaxWidth(Double.MAX_VALUE);


    }



    /**
     * Returns a tile from the exchange area back to the player's hand.
     * @param row The row in the exchange grid
     * @param col The column in the exchange grid
     */
    private void returnExchangeTileToHand(int row, int col) {
        Position pos = new Position(row, col);

        if (exchangeTiles.containsKey(pos)) {
            Letter letter = exchangeTiles.remove(pos);
            playerHand.add(letter);

            // Remove the tile from the exchange grid
            exchangeArea.getChildren().removeIf(node ->
                    GridPane.getColumnIndex(node) == col &&
                            GridPane.getRowIndex(node) == row);

            // Restore the empty exchange slot
            Button emptySlot = new Button();
            emptySlot.setPrefSize(TILE_SIZE, TILE_SIZE);
            emptySlot.setStyle("-fx-background-color: " +
                    "#E0E0E0; -fx-border-color: #A0A0A0;");

            final int finalCol = col;
            emptySlot.setOnAction(e -> handleExchangeSlotClick(0, finalCol));

            exchangeArea.add(emptySlot, col, row);
            exchangeTileButtons[0][col] = emptySlot;

            updateGUI();
        }
    }



    /**
     * Returns all tiles from the exchange area back to the player's hand.
     */
    private void returnAllExchangeTilesToHand() {
        for (Letter letter : exchangeTiles.values()) {
            playerHand.add(letter);
        }

        clearExchangeArea();
        updateGUI();
    }

    /**
     * Clears the exchange area, resetting all slots to empty.
     */
    private void clearExchangeArea() {
        exchangeTiles.clear();

        // Remove all children from the exchange grid
        exchangeArea.getChildren().clear();


        // Recreate empty slots
        for (int row = 0; row < 7; row++) {
            Button emptySlot = new Button();
            emptySlot.setPrefSize(TILE_SIZE, TILE_SIZE);
            emptySlot.setStyle("-fx-background-color: " +
                    "#E0E0E0; -fx-border-color: #A0A0A0;");

            final int finalRow = row;
            emptySlot.setOnAction(e -> handleExchangeSlotClick(finalRow, 0));

            exchangeTileButtons[row][0] = emptySlot;
            exchangeArea.add(emptySlot, 0, row);
        }
    }


    /**
     * Creates the status display components including player and
     * computer score labels.
     */
    private void createStatus() {
        playerScoreLabel = new Label("Player Score: 0");
        playerScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        computerScoreLabel = new Label("Computer Score: 0");
        computerScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        gameStatusLabel = new Label("Your turn");
        gameStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
    }

    /**
     * Creates the game control buttons (submit, exchange, pass, rules)
     * and sets up their event handlers.
     */
    private void createControls() {
        submitMoveButton = new Button("Submit Move");
        submitMoveButton.setMaxWidth(Double.MAX_VALUE);
        submitMoveButton.setOnAction(e -> handleSubmitMove());

        exchangeAllButton = new Button("Exchange Tiles");
        exchangeAllButton.setMaxWidth(Double.MAX_VALUE);
        exchangeAllButton.setOnAction(e -> handleExchangeTiles());

        passButton = new Button("Pass Turn");
        passButton.setMaxWidth(Double.MAX_VALUE);
        passButton.setOnAction(e -> handlePassTurn());

        rulesButton = new Button("Rules/\nHow to play");
        rulesButton.setAlignment(Pos.CENTER);
        rulesButton.setOnAction(e -> handleRules());
    }

    /**
     * Creates the player's tray for holding letter tiles.
     */
    private void createPlayerTray() {
        playerTray = new HBox(3);
        playerTray.setAlignment(Pos.CENTER);
        playerTray.setPadding(new Insets(10));

        playerTrayButtons = new ArrayList<>();
    }

    /**
     * Creates the game board grid with buttons for each tile position.
     * Sets up tile styles based on board multipliers.
     */
    private void createGameBoard() {
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        tileButtons = new Button[boardSize][boardSize];

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Button tileButton = new Button();
                tileButton.setPrefSize(TILE_SIZE, TILE_SIZE);

                Tile tile = game.getBoard()[row][col];
                String buttonStyle = getTileStyle(tile);
                tileButton.setStyle(buttonStyle);
                final int finalRow = row;
                final int finalCol = col;

                tileButton.setOnAction(e -> handleBoardTileClick(finalRow, finalCol));

                tileButtons[row][col] = tileButton;
                boardGrid.add(tileButton, col, row);
            }
        }

    }

    /**
     * Determines the appropriate style for a tile based on its multipliers.
     * @param tile The tile to style
     * @return A CSS style string for the tile
     */
    private String getTileStyle(Tile tile) {
        String baseStyle = "-fx-border-color: #FFD700; -fx-border-width: 2px;";
        if(tile.getLetterMult() > 1 || tile.getWordMult() > 1) {
            if(tile.getLetterMult() == 2) {
                return baseStyle + "-fx-background-color: #ADD8E6;";
            } else if (tile.getLetterMult() == 3) {
                return baseStyle + "-fx-background-color: #0000FF;";
            } else if (tile.getWordMult() ==2) {
                return baseStyle + "-fx-background-color: #FFC0CB;";
            } else if (tile.getWordMult() ==3) {
                return baseStyle + "-fx-background-color: #FF0000;";
            }
        }

        return baseStyle + "-fx-background-color: #E8E8E8;";
    }



    /**
     * Sets up the initial game state.
     * Loads the board layout and dictionary.
     *
     * @param primaryStage The primary stage for the application
     */
    private void setupGame(Stage primaryStage) {
        InputStream board = getClass().getResourceAsStream("scrabble_board.txt");
        InputStream dictionary = getClass().getResourceAsStream("sowpods.txt");
        Scanner dicIn = new Scanner(dictionary);
        game = new ScrabbleGame(true);
        game.loadDictionary(dicIn);
        game.generateBag();
        Scanner boardin = new Scanner(board);
        game.loadBoard(boardin);
        currentMove = new HashMap<>();
        playerHand = new ArrayList<>();
    }

    /**
     * Handles the computer's turn.
     * The computer player analyzes the board and makes the best move.
     */
    private void computerTurn() {
        gameStatusLabel.setText("Computer is thinking...");
        if (computerHand != null) {
            ArrayList<Letter> bag = game.getBag();
            if (!computerHand.isEmpty()) {
                bag.addAll(computerHand);
            }
        }

        computerHand = new ArrayList<>();
        ArrayList<Letter> bag = game.getBag();
        Random random = new Random();

        while (computerHand.size() < 7 && !bag.isEmpty()) {
            int index = random.nextInt(bag.size());
            computerHand.add(bag.remove(index));
        }

        Computer computerPlayer = new Computer(game, computerHand);
        computerPlayer.findAllWords();

        HashMap<Position, Letter> bestMove = computerPlayer.getBestMove();

        if (!bestMove.isEmpty()) {
            int score = game.makeMove(bestMove);
            for (Letter letter : bestMove.values()) {
                computerHand.remove(letter);
            }
            computerScore += score;
            computerScoreLabel.setText("Computer Score: " + computerScore);

            gameStatusLabel.setText("Computer played " + computerPlayer.getBestWord());
        } else {
            gameStatusLabel.setText("Computer passed. Your turn.");
        }
        updateGUI();
        checkGameOver();
    }

    /**
     * Checks if the game is over (when the bag is empty).
     * Calculates final scores and displays the winner.
     */
    private void checkGameOver() {
        ArrayList<Letter> bag = game.getBag();
        int computerLeftOver = 0;
        int playerLeftOver = 0;
        if (bag.isEmpty()) {

            if(!computerHand.isEmpty() && !playerHand.isEmpty()) {
                for(Letter l: computerHand) {
                    computerLeftOver += l.getPoints();
                }
                for(Letter l: playerHand) {
                    playerLeftOver += l.getPoints();
                }

                computerScore -= computerLeftOver;
                playerScore -= playerLeftOver;
            } else if (computerHand.isEmpty() && !playerHand.isEmpty()) {
                for(Letter l: playerHand) {
                    playerLeftOver += l.getPoints();
                }
                computerScore += playerLeftOver;
                playerScore -= playerLeftOver;
            } else {
                for(Letter l: computerHand) {
                    computerLeftOver += l.getPoints();
                }
                playerScore += computerLeftOver;
                computerScore -= computerLeftOver;
            }

            playerScoreLabel.setText("Player Score: " + playerScore);
            computerScoreLabel.setText("Computer Score: " + computerScore);



            String winner;
            if (playerScore > computerScore) {
                winner = "You win!";
            } else if (computerScore > playerScore) {
                winner = "Computer wins!";
            } else {
                winner = "It's a tie!";
            }

            showAlert("Game Over", "Game over! " + winner +
                    "\nYour score: " + playerScore +
                    "\nComputer score: " + computerScore);

            submitMoveButton.setDisable(true);
            exchangeAllButton.setDisable(true);
            passButton.setDisable(true);
        }
    }

    /**
     * Returns all tiles from the current move back to the player's hand.
     * Used when canceling a move or when an invalid move is attempted.
     */
    private void returnTilesToHand() {
        for (Map.Entry<Position, Letter> entry : currentMove.entrySet()) {
            Position pos = entry.getKey();
            Letter letter = entry.getValue();
            Tile tile = game.getBoard()[pos.row][pos.col];
            if (!tile.isEmpty() && tile.getLetter().equals(letter)) {
                tile.removeLetter();

            }

            if(letter.isBlank()) {
                letter.setBlank('*');

            }

            tileButtons[pos.row][pos.col].setStyle(getTileStyle(tile));
            playerHand.add(letter);
        }
        currentMove.clear();
        updateGUI();
    }



    /**
     * Refills the player's hand from the bag after a move.
     */
    private void refillPlayerHand() {
        ArrayList<Letter> bag = game.getBag();
        while(playerHand.size() < 7 && !bag.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(bag.size());
            playerHand.add(bag.remove(index));
        }
        updateGUI();
    }


    /**
     * Displays a dialog to prompt the player to choose a letter for a blank tile.
     *
     * @param tileButton The button representing the blank tile
     */
    private void promptForBlankTile(Button tileButton) {
        GridPane letterGrid = new GridPane();
        letterGrid.setHgap(5);
        letterGrid.setVgap(5);
        letterGrid.setPadding(new Insets(10));

        Stage blankDialog = new Stage();
        blankDialog.setTitle("Select Letter for Blank Tile");
        int row = 0;
        int col = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            final char letter = c;
            Button letterButton = new Button(String.valueOf(letter));
            letterButton.setPrefSize(40, 40);
            letterButton.setOnAction(e -> {
                tileButton.setText(String.valueOf(letter));

                int index = playerTrayButtons.indexOf(tileButton);
                if (index >= 0 && index < playerHand.size()) {
                    Letter blankTile = playerHand.get(index);
                    blankTile.setBlank(Character.toLowerCase(letter));
                }

                blankDialog.close();
            });

            letterGrid.add(letterButton, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        Scene scene = new Scene(letterGrid);
        blankDialog.setScene(scene);
        blankDialog.showAndWait();
    }

    /**
     * Handles the exchange tiles button click.
     * Exchanges the tiles in the exchange area with new ones from the bag.
     */
    private void handleExchangeSelectedTiles() {
        if(!isFirstMove){
            if (exchangeTiles.isEmpty()) {
                showAlert("Exchange Error",
                        "Please select at least one tile to exchange.");
                return;
            }

            ArrayList<Letter> tilesToExchange = new ArrayList<>(exchangeTiles.values());
            ArrayList<Letter> bag = game.getBag();

            if (bag.size() < tilesToExchange.size()) {
                showAlert("Exchange Error",
                        "Not enough tiles in the bag to exchange. " +
                                "Needed: " + tilesToExchange.size() +
                                ", Available: " + bag.size());
                returnAllExchangeTilesToHand();
                return;
            }

            for (int i = 0; i < tilesToExchange.size(); i++) {
                if (!bag.isEmpty()) {
                    int index = new Random().nextInt(bag.size());
                    playerHand.add(bag.remove(index));
                }
            }
            bag.addAll(tilesToExchange);
            clearExchangeArea();

            // Computer's turn
            computerTurn();
            updateGUI();
        }
    }

    /**
     * Handles clicks on the exchange area slots.
     * If a tray tile is selected and the exchange slot is empty,
     * places the tile there.
     * @param row The row in the exchange grid
     * @param col The column in the exchange grid
     */
    private void handleExchangeSlotClick(int row, int col) {
        if (!isFirstMove){
            if (exchangeArea.getChildren().stream()
                    .anyMatch(node -> GridPane.getColumnIndex(node) == col &&
                            GridPane.getRowIndex(node) == row &&
                            node instanceof Button &&
                            !((Button) node).getText().isEmpty())) {

                returnExchangeTileToHand(row, col);
                return;
            }

            // If a tray tile is selected, place it in the exchange slot
            if (selectedTrayButton != null) {
                int index = playerTrayButtons.indexOf(selectedTrayButton);

                if (index >= 0 && index < playerHand.size()) {
                    Letter letter = playerHand.get(index);

                    Button tileInExchange = new Button(selectedTrayButton.getText());
                    tileInExchange.setPrefSize(TILE_SIZE, TILE_SIZE);
                    tileInExchange.setStyle(selectedTrayButton.getStyle());

                    exchangeArea.getChildren().removeIf(node ->
                            GridPane.getColumnIndex(node) == col &&
                                    GridPane.getRowIndex(node) == row);
                    exchangeArea.add(tileInExchange, col, row);
                    exchangeTiles.put(new Position(row, col), letter);

                    playerHand.remove(index);
                    playerTrayButtons.remove(selectedTrayButton);
                    playerTray.getChildren().remove(selectedTrayButton);
                    selectedTrayButton = null;
                    updateGUI();
                }
            }
        }
    }

    /**
     * Handles the player's pass turn action.
     * Returns any tiles from the current move back to the player's hand
     * and gives the turn to the computer.
     */
    private void handlePassTurn() {
        if(!isFirstMove){
            returnTilesToHand();
            computerTurn();
            updateGUI();
        }
    }

    /**
     * Handles the player's exchange tiles action.
     * Returns the current tiles to the bag and draws new ones.
     */
    private void handleExchangeTiles() {
        if(!isFirstMove  && exchangeTiles.isEmpty()){
            if (game.getBag().size() < 7) {
                showAlert("Cannot Exchange",
                        "Not enough tiles in the bag to exchange.");
                return;
            }
            returnTilesToHand();
            ArrayList<Letter> bag = game.getBag();
            ArrayList<Letter> tempHand = new ArrayList<>(playerHand);
            playerHand.clear();
            bag.addAll(tempHand);

            for (int i = 0; i < 7 && !bag.isEmpty(); i++) {
                int index = new Random().nextInt(bag.size());
                playerHand.add(bag.remove(index));
            }
            computerTurn();
            updateGUI();
        }
    }

    /**
     * Handles the player's submit move action.
     * Validates the move, scores it, and gives the turn to the computer if valid.
     */
    private void handleSubmitMove() {
        if (currentMove.isEmpty()) {
            showAlert("Invalid Move",
                    "You need to place at least one tile on the board.");
            return;
        }

        boolean isValid = game.validMove(currentMove);
        if (isValid) {
            LinkedList<Position> positions = new LinkedList<>(currentMove.keySet());
            int moveScore = game.makeMove(currentMove);

            playerScore += moveScore;
            playerScoreLabel.setText("Your Score: " + playerScore);

            currentMove.clear();
            refillPlayerHand();
            isFirstMove = false;
            computerTurn();
        } else {
            showAlert("Invalid Move",
                    "The move you made is not valid. Please try again.");
            returnTilesToHand();
        }

        updateGUI();
    }

    /**
     * Handles a click on a tile in the player's tray.
     * Selects or deselects the tile.
     * For blank tiles, prompts the player to choose a letter.
     * @param tileButton The button that was clicked
     */
    private void handleTrayTileClick(Button tileButton) {
        if (selectedTrayButton == tileButton) {
            selectedTrayButton.setStyle("-fx-background-color: " +
                    "#F5DEB3; -fx-font-weight: bold;");
            selectedTrayButton = null;
        } else {
            if (selectedTrayButton != null) {
                selectedTrayButton.setStyle("-fx-background-color: " +
                        "#F5DEB3; -fx-font-weight: bold;");
            }

            // Select new tile
            selectedTrayButton = tileButton;
            selectedTrayButton.setStyle("-fx-background-color: " +
                    "#FFD700; -fx-font-weight: bold;");

            // Check if this is a blank tile
            int index = playerTrayButtons.indexOf(tileButton);
            if (index >= 0 && index < playerHand.size() &&
                    playerHand.get(index).getChar() == '*') {
                promptForBlankTile(tileButton);
            }
        }
    }

    /**
     * Handles a click on a board tile.
     * If a tray tile is selected and the board tile is empty, places the letter on the board.
     *
     * @param row The row of the clicked board tile
     * @param col The column of the clicked board tile
     */
    private void handleBoardTileClick(int row, int col) {
        Position p =  new Position(row,col);
        for(Position movePos: currentMove.keySet()) {
            if(movePos.equals(p)) {
                return;
            }
        }

        if(exchangeTiles.isEmpty()){
            if (selectedTrayButton != null && game.getBoard()[row][col].isEmpty()) {
                int index = playerTrayButtons.indexOf(selectedTrayButton);

                if (index >= 0 && index < playerHand.size()) {
                    Letter letter = playerHand.get(index);
                    String displayText;

                    if (letter.getChar() == '*') {
                        Character blankChar = letter.getChar();
                        displayText = String.valueOf(Character.toUpperCase(blankChar));
                        tileButtons[row][col].setStyle("-fx-background-color: " +
                                "#F5DEB3; -fx-font-weight: bold;");

                    } else {
                        displayText = String.valueOf(Character.toUpperCase(letter.getChar()));
                        tileButtons[row][col].setStyle("-fx-background-color: " +
                                "#F5DEB3; -fx-font-weight: bold;");
                    }

                    tileButtons[row][col].setText(displayText.toUpperCase());

                    Position position = new Position(row, col);
                    currentMove.put(position, letter);

                    playerTrayButtons.remove(selectedTrayButton);
                    playerTray.getChildren().remove(selectedTrayButton);

                    selectedTrayButton = null;

                    playerHand.remove(index);
                }
            }
        }
    }

    /**
     * Displays the game rules in a popup window.
     */
    private void handleRules() {
        // Create a stage for the rules popup
        Stage rulesStage = new Stage();
        rulesStage.setTitle("Scrabble Rules");

        // Create a scrollable text area for the rules
        TextArea rulesText = new TextArea();
        rulesText.setEditable(false);
        rulesText.setWrapText(true);
        rulesText.setPrefWidth(600);
        rulesText.setPrefHeight(500);

        // Set the rules text
        rulesText.setText(
                "SCRABBLE RULES\n\n" +

                        "OBJECTIVE:\n" +
                        "The goal is to score more points than the computer by " +
                        "creating words on the game board using letter tiles, " +
                        "each with a different point value. Words are created by " +
                        "placing tiles horizontally or vertically on the board.\n\n" +

                        "GAME SETUP:\n" +
                        "1. Each player get 7 letter tiles from the bag.\n\n" +
                        "PLAYER MUST MOVE FIRST\n\n" +

                        "GAMEPLAY:\n" +
                        "1. On your turn, you can:\n" +
                        "   - Form a new word on the board\n" +
                        "   - Add to an existing word\n" +
                        "   - Exchange some or all of your tiles\n" +
                        "   - Pass your turn\n\n" +

                        "2. The first word must cover the center square.\n\n" +

                        "3. All subsequent words must:\n" +
                        "   - Connect to existing words on the board\n" +
                        "   - Form valid words in all directions where tiles are connected\n\n" +

                        "4. After placing tiles, your score for the turn is calculated:\n" +
                        "   - Add the values of all tiles placed\n" +
                        "   - Apply any premium square bonuses\n" +
                        "   - If all 7 tiles are played in one turn, add 50 bonus points (a \"Bingo\")\n\n" +

                        "5. Tiles are drawn from the bag and placed into your hand.\n\n" +

                        "6. Computer plays a word and draws from the bag\n\n" +

                        "BOARD PREMIUM SQUARES:\n" +
                        "- Double Letter Score (Light blue): Doubles the score " +
                        "of the letter placed on it\n" +
                        "- Triple Letter Score (Dark blue): Triples the score of " +
                        "the letter placed on it\n" +
                        "- Double Word Score (Light red): Doubles the score of " +
                        "the entire word\n" +
                        "- Triple Word Score (Dark red): Triples the score of " +
                        "the entire word\n\n" +

                        "BLANK TILES:\n" +
                        "Blank tiles have no point value but can represent any letter. " +
                        "Once a blank is played, it remains the chosen letter for the rest of the game.\n\n" +


                        "ENDING THE GAME:\n" +
                        "The game ends when:\n" +
                        "1. All tiles have been drawn and one player uses their last tile, OR\n" +
                        "2. All possible plays have been exhausted (e.g., both players pass)\n\n" +

                        "FINAL SCORING:\n" +
                        "- When the game ends, each player deducts the sum of " +
                        "their remaining tiles from their score\n" +
                        "- The player who used all their tiles adds the sum of " +
                        "all opponents' remaining tiles to their score\n\n" +

                        "LETTER DISTRIBUTION AND VALUES:\n" +
                        "A (1 point): 9 tiles    N (1 point): 6 tiles\n" +
                        "B (3 points): 2 tiles   O (1 point): 8 tiles\n" +
                        "C (3 points): 2 tiles   P (3 points): 2 tiles\n" +
                        "D (2 points): 4 tiles   Q (10 points): 1 tile\n" +
                        "E (1 point): 12 tiles   R (1 point): 6 tiles\n" +
                        "F (4 points): 2 tiles   S (1 point): 4 tiles\n" +
                        "G (2 points): 3 tiles   T (1 point): 6 tiles\n" +
                        "H (4 points): 2 tiles   U (1 point): 4 tiles\n" +
                        "I (1 point): 9 tiles    V (4 points): 2 tiles\n" +
                        "J (8 points): 1 tile    W (4 points): 2 tiles\n" +
                        "K (5 points): 1 tile    X (8 points): 1 tile\n" +
                        "L (1 point): 4 tiles    Y (4 points): 2 tiles\n" +
                        "M (3 points): 2 tiles   Z (10 points): 1 tile\n" +
                        "Blank (0 points): 2 tiles\n\n" +

                        "GAME CONTROLS:\n" +
                        "- Click on a letter in your tray, then click on the board to place it\n" +
                        "- To exchange tiles, select them from your tray and click on the exchange area, " +
                        "or use the 'Exchange all' button\n" +
                        "- Use 'Submit Move' to confirm your word placement\n" +
                        "- Use 'Exchange Selected Tiles' to swap the tiles in the exchange area\n" +
                        "- Use 'Exchange All Tiles' to swap your entire hand\n" +
                        "- Use 'Pass Turn' to skip your turn\n"
        );

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> rulesStage.close());
        closeButton.setPrefWidth(100);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(rulesText, closeButton);
        layout.setAlignment(Pos.CENTER);

        // make the new window
        Scene scene = new Scene(layout);
        rulesStage.setScene(scene);
        rulesStage.showAndWait();
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
