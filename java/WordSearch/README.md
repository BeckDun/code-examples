# Word Search (2023)

Java program to solve a Word Search puzzle.

## Description

Full implementation of a Java command-line program that solves a Word Search puzzle leveraging the following:

- File-based specification of puzzle and dictionary
- Trie data structure for efficient dictionary look-up
- Multiple direction patterns for searching (compass, diagonal, forward/backward)

## Dependencies

- `java.util.Iterator`, `java.nio.file.*`
 
## Runtime  

```bash
java WordSearch <dictionary file> <puzzle file> <valid directions: {ALL, HORIZVERT, FORWARD, DIAGONAL}>
```

## Demo

Command-line: 
```bash
$ java WordSearch textfiles/dics/animals.txt textfiles/puzzles/animalPuzz1.txt ALL > textfiles/results/animals-animalPuzz1-all-result.txt
```

Files:
```bash
$ head textfiles/dics/animals.txt
aardvark
alligator
alpaca
anteater
antelope
ape
armadillo
baboon
badger
bat
```

```bash
$ head textfiles/puzzles/animalPuzz1.txt
20 30
otacdliwrabbitqxqzqamarrxbison
wloxhqqalligatorminkwazesgswyx
rtarnenipucropmareaoeodvuierxe
xeefaoelidocorcxyyobxboarpaqpw
tqgefnetxwwxxxghedgehogelnlaoz
afedkuglaraccooncqqlionbazqckx
rlezaabuehogrwphaxxswxzcweeqnw
klqrlbrytmwzosumatopoppihlxyui
suzprweanaaqwcmwxqswerxkoalamg
```

```bash
$ head textfiles/results/animals-animalPuzz1-all-result.txt
aardvark 12 29 S
alligator 1 7 E
alpaca 6 5 SW
anteater 17 6 E
antelope 10 11 SW
ape 4 27 NE
armadillo 11 3 S
baboon 10 9 SW
badger 7 5 NW
bat 14 2 SW
```


## Author

[Beckett Dunlavy](https://github.com/BeckDun)

