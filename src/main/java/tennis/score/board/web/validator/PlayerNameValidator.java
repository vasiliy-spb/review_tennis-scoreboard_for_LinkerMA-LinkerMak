package tennis.score.board.web.validator;

import tennis.score.board.exception.BadRequestException;

import java.util.regex.Pattern;

public final class PlayerNameValidator {

    // Можно использовать @UtilityClass из Lombok.

    // Класс валидирует и нормализует значение — это нарушает Принцип единой ответственности (SRP).
        // Валидатор должен заниматься только валидацией.

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}]+(?:[ '-][\\p{L}]+)*$");

    private PlayerNameValidator() {
    }

    public static String normalizeAndValidate(String rawName) {
        if (rawName == null) {
            throw new BadRequestException("Имя игрока обязательно");
        }

        String normalized = normalize(rawName);

        if (normalized.isBlank()) {
            throw new BadRequestException("Имя игрока обязательно");
        }

        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new BadRequestException(
                    "Имя игрока может содержать только буквы, пробел, дефис и апостроф"
            );
        }

        return normalized;
    }

    public static void validateDifferentPlayers(String player1, String player2) {
        if (player1.equalsIgnoreCase(player2)) {
            throw new BadRequestException("Игроки должны быть разными");
        }
    }

    private static String normalize(String rawName) {
        return rawName.strip().replaceAll("\\s+", " ");
    }
}