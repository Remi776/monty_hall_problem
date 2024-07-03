package ru.gb.jdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class App {
    private static final int TOTAL_REPEATS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Map<Integer, Boolean> results = new HashMap<>();
        Map<Integer, List<Integer>> moves = new HashMap<>();

        for (int i = 1; i <= TOTAL_REPEATS; i++) {
            boolean win = playMontyHallGame(moves, i);
            results.put(i, win);
        }

        displayGameResults(results, moves);
        displayStatistics(results);
    }

    private static void displayGameResults(Map<Integer, Boolean> results, Map<Integer, List<Integer>> moves) {
        for (int i = 1; i <= TOTAL_REPEATS; i++) {
            List<Integer> gameMoves = moves.get(i);
            if (gameMoves != null && gameMoves.size() >= 3) {

                int playerChoice = gameMoves.get(0);
                int goatDoor = gameMoves.get(1);
                int switchChoice = gameMoves.get(2);
                boolean win = results.get(i);

                String explanation = String.format("Game %d: Player's door %d, Lead's door %d%s",
                        i, playerChoice, goatDoor,
                        switchChoice > 0 ? ", Player's new door " + switchChoice : ", Player leaves his choice");

                logger.info(explanation + (win ? " - Player won!" : " - Player lost!"));
            } else {
                logger.error("Error processing the game progress " + i);
            }
        }
    }

    private static void displayStatistics(Map<Integer, Boolean> results) {
        long positiveResults = results.values().stream().filter(Boolean::booleanValue).count();

        int negativeResults = TOTAL_REPEATS - (int) positiveResults;
        double positivePercentage = (double) positiveResults / TOTAL_REPEATS * 100;

        logger.info("\nStatistics:");
        logger.info("Positive games: {}", positiveResults);
        logger.info("Negative games: {}", negativeResults);
        logger.info("Percentage of positive games: {}%", positivePercentage);
    }

    private static boolean playMontyHallGame(Map<Integer, List<Integer>> moves, int gameNumber) {
        // генерация случайных дверей для Player
        int carDoor = ThreadLocalRandom.current().nextInt(3) + 1;
        int playerChoice = ThreadLocalRandom.current().nextInt(3) + 1;
        int goatDoor = selectGoatDoor(carDoor, playerChoice);

        boolean switchChoice = ThreadLocalRandom.current().nextBoolean();
        int switchPlayerChoice = 0;
        if (switchChoice) {
            switchPlayerChoice = switchPlayerChoice(playerChoice, goatDoor);
        }

        // Сохранение ходов игры.
        saveGameMove(moves, gameNumber, playerChoice, goatDoor, switchChoice ? switchPlayerChoice : 0);

        if (switchPlayerChoice == carDoor) return true;
        else return playerChoice == carDoor;
    }

    private static int selectGoatDoor(int carDoor, int playerChoice) {
        List<Integer> doors = new ArrayList<>(Arrays.asList(1, 2, 3));
        doors.removeIf(door -> door == carDoor || door == playerChoice);
        return doors.get(ThreadLocalRandom.current().nextInt(doors.size()));
    }

    private static int switchPlayerChoice(int playerChoice, int goatDoor) {
        // исхожу из того, что сумма № дверей 1 + 2 + 3 = 6;
        return 6 - playerChoice - goatDoor;
    }

    private static void saveGameMove(Map<Integer, List<Integer>> moves, int gameNumber, int playerChoice, int goatDoor, int switchChoice) {
        moves.put(gameNumber, Arrays.asList(playerChoice, goatDoor, switchChoice));
    }
}
