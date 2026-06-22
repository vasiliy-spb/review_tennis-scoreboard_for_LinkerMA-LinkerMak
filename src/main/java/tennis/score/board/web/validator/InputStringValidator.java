package tennis.score.board.web.validator;

import tennis.score.board.exception.BadRequestException;

public final class InputStringValidator {

    private InputStringValidator() {}

    public static void validateName(String str) {
        if(str == null || str.isBlank()) {
            throw new BadRequestException("Поле name обязательно");
        }
    }
}
