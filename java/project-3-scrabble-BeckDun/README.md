# Scrabble Board Game

A two-player implementation of the classic Scrabble word game built with JavaFX. This game allows players to compete against a computer opponent that uses an advanced algorithm to find the highest-scoring moves on the board.

The game will display the rules when launching the program but if needed see the general [Scrabble rules](https://www.hasbro.com/common/instruct/Scrabble_(2003).pdf)

**Author:** Beckett Dunlavy  
**Class:** UNM CS-351: Design of Large Programs  
**Spring 2025**

## Game Features
- Player always plays first
- Classic Scrabble gameplay with standard 15x15 board
- Computer opponent with highest scoring move-finding algorithm
- Support for blank tiles and all standard Scrabble rules
- Premium squares (double/triple letter/word scores)
- Score tracking
- Move validation against a dictionary (using [sowpods.txt](dictionaries_and_examples/sowpods.txt))
- Tile exchange and pass functionality
- Visual indication of tile values and board premiums
- Game rules popup for easy reference

## Known Bugs

- No known bugs (March 12, 2025)
## Future Features

- Play again feature
- multiple levels of computer players
- 
## Computer Player Algorithm

The computer player in this Scrabble implementation uses an advanced algorithm based on the approach described in ["Scrabble: An Efficient Approach"](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) from Carnegie Mellon University.

Key components of the algorithm include:
- A [trie data structure](https://en.wikipedia.org/wiki/Trie) for efficient dictionary lookups
- Cross-checks to determine valid letters for each square
- Anchor-based move generation that only examines promising squares
- Left and right word extensions to find all possible plays
- Special handling for blank tiles and premium squares
- Comprehensive scoring that accounts for all rules of Scrabble

This approach allows the computer player to find the highest-scoring moves
## Running the Game

### Scrabble Game (GUI)
To run the complete Scrabble game with the graphical interface:
```BASH
java -jar scrabble.jar
```

### Score Checker
To validate moves and calculate scores from board configurations:
```BASH
java -jar scorechecker.jar <dictionary_file>
```
Example:
```BASH
java -jar scorechecker.jar sowpods.txt < example_score_input.txt
```
The program reads pairs of board configurations from standard input, finds the played tiles, determines if the move is legal, and outputs the score.

### Word Solver
To find the highest-scoring move for a given board and tray:
```BASH
java -jar solver.jar <dictionary_file>
```
Example:
```BASH
java -jar solver.jar sowpods.txt < example_input.txt
```
The program reads a board configuration and a tray of letters from standard input, then outputs the highest-scoring move possible.

## Project Structure


### .gitignore

### Jar files
 - [scorechecker.jar](scorechecker.jar)

 - [solver.jar](solver.jar)
 - [scrabble.jar](scrabble.jar)


### src/ Scrabble
- Contains Scrabble package with the [source code](src/Scrabble) for this project

### docs
- [Object design diagram](docs/object-design-Scrabble.pdf)
- [Computer algorithm](docs/scrabble_computer_algo.pdf)

### [dictionaries_and_examples](dictionaries_and_examples)
This folder contains example files for testing the programs. See the readme in this folder for more details.

### [.github/ workflows](.gihub/workflows)
This folder contains tests for the project structure

