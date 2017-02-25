#WordMaster

##About
This game is the computer version of popular Russian pen-and-sheet game "Balda". 

##Rules
Two players have a grid field with one long word at the center. Players moves one by one putting one letter at any free grid cell. The only rule is that after every move there can be created a *new* word. Words are created by the following algorithm: 
1. select one letter
2. append one letter if it is not a world yet. You can append only letters, that stands at the top-right-bottom-left position
3. repeat step 2 if you don't get a new world
The game ends if there is no legal move. Player score is the amout of letters in his worlds.

##Compiling
You need [Maven](https://maven.apache.org/ "Maven page") to compile this game. The executable jar will be at the ./taget/wordmaster-[version].jar

##Contribution
Feel free to discover this code, send pull request or make any comments.
