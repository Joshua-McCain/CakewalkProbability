import java.util.Random;

public class Main {

    //**Keep track of player wins and time variables**
    private static final int playerCount = 4;
    private static float[] playerPositionWins = new float[playerCount];
    //In seconds
    private static final float minTimePerJump = 1f;
    private static final float maxTimePerJump = 3f;
    private static final float knownJumpTime = 0.5f;
    //index 0 for a normal unknown jump, index 1 for known jumps
    private static float[] gameplayJumps = new float[2];

    //**Modifiable variables to get different outcomes**
    private static final int totalGames = 100000;
    //Number of times a player has to jump before winning
    private static final int numberOfRows = 8;
    //Chance that the player will forget what platform to jump to if there are still 2 options available
    //Value must be from [0, 1)
    private static final float forgetChance = 0.1f;

    public static void main(String[] args){
        Random rand = new Random();

        for(int i = 0; i < totalGames; i++){
            //Game state vars
            boolean gameWon = false;
            int currentPlayer = 0;

            //Keeps track of the platforms that have already been guessed correct but
            //still have a chance to be forgotten because there are still 2 plats present
            int truePlatKnownUntil = -1;

            //A player matching this array results in a win for them
            int[] truePlatformSequence = new int[numberOfRows];
            //A value of true means a player cannot mess up when choosing this value. It pulls from the truePlat list
            boolean[] knownPlatformValues = new boolean[numberOfRows];

            //Get the sequence of correct platforms and set known plats all to false.
            for(int j = 0; j < numberOfRows; j++){
                truePlatformSequence[j] = rand.nextInt(2);
                knownPlatformValues[j] = false;
            }

            while(!gameWon){
                //Each player proceeds to make guesses until someone gets all the right guesses
                for (int j = 0; j < numberOfRows; j++){
                    if(!knownPlatformValues[j]){
                        int playerGuess;

                        //Has someone guessed this platform correctly before but left behind 2 options?
                        //If so, modify chances from 50/50 to more favorable to choose correct
                        if(j <= truePlatKnownUntil){
                            //2 is not a possible value, so it fails the player
                            playerGuess = rand.nextFloat() >= forgetChance ? truePlatformSequence[j] : 2;
                        }
                        else{ //New territory, 50/50 chance to guess correct
                            playerGuess = rand.nextInt(2);
                        }

                        gameplayJumps[0]++;

                        //If the player guess did not succeed
                        if(truePlatformSequence[j] != playerGuess){
                            //Only 1 option to choose from in the future
                            knownPlatformValues[j] = true;
                            //We know the platforms before this platform with relative certainty
                            truePlatKnownUntil = j;
                            //Rotate to next player in queue
                            currentPlayer += 1;
                            if(currentPlayer >= playerCount){
                                currentPlayer = 0;
                            }
                            //Start the next player's turn
                            break;
                        }
                    }
                    else{ //If plat is already known, aka other plat has fallen
                        gameplayJumps[1]++;
                    }
                    //Check to see if the player has won
                    if(j == numberOfRows - 1){
                        gameWon = true;
                    }
                }
            }
            //System.out.println("Game " + (i + 1) + " Complete");
            //The last to submit a sequence was the winner, so add to their wins
            playerPositionWins[currentPlayer] += 1f;
        }

        //All the games have been played, get the average win rate for each player
        System.out.printf("------------------------\nRows: %d\t Forget Percentage: %f%%\n------------------------\n", numberOfRows, forgetChance);
        for(int i = 0; i < playerCount; i++){
            System.out.printf("Player %d Win Percentage: %f%%\n", i + 1, playerPositionWins[i]/(float)totalGames);
        }
        float knownJumpsInSec = gameplayJumps[1] * knownJumpTime;
        //Get average times in minutes
        float minAverageTime = ((gameplayJumps[0] * minTimePerJump + knownJumpsInSec) / (float)totalGames) / 60f;
        float maxAverageTime = ((gameplayJumps[0] * maxTimePerJump + knownJumpsInSec) / (float)totalGames) / 60f;
        System.out.printf("Min Average Time (in minutes): %.2f\nMax Average Time (in minutes): %.2f", minAverageTime, maxAverageTime);
    }
}
