# Роадмап рефакторинга по файлам

Это упорядоченный список файлов, которые следует исправлять в соответствии с замечаниями в комментариях. Рекомендую двигаться последовательно.

Файлы, не указанные в списке, можно исправлять в любом порядке.

### Шаг 1: Entity и слой доступа к данным

- `/model/entity/Match.java`
- `/repository/PlayerRepository.java`
- `/repository/MatchRepository.java`

### Шаг 2: Доменные модели

- `/model/matchstate/game/regulargame/Points.java`
- `/model/matchstate/game/regulargame/RegularGameScore.java`
- `/model/matchstate/game/deuce/DeucePoints.java`
- `/model/matchstate/game/deuce/DeuceGameScore.java`
- `/model/matchstate/game/GameResult.java`
- `/model/matchstate/game/tiebreak/TieBreakGameScore.java`
- `/model/matchstate/set/SetScore.java`
- `/model/matchstate/match/MatchScore.java`
- `/model/matchstate/game/GameScore.java`
- `/model/matchstate/MatchState.java`
- `/model/matchstate/WinnerSide.java`

### Шаг 3: Сервисный слой

- `/service/PlayerService.java`
- `/service/OngoingMatchService.java`
- `/service/MatchService.java`

### Шаг 4: DTO (Data Transfer Object)

- `/web/dto/MatchStateDTO.java`

### Шаг 5: Контроллеры

- `/web/controller/HomeController.java`
- `/web/controller/CreateMatchController.java`
- `/web/controller/MatchScoreController.java`
- `/web/controller/MatchesController.java`

### Шаг 6: Конфигурация, мапперы, валидаторы, обработка исключений

- `/web/exception/ExceptionController.java`
- `/config/PersistenceConfiguration.java`
- `/config/WebMvcConfiguration.java`
- `/config/WebMvcDispatcherServletInitializer.java`
- `/web/mapper/MatchMapper.java`
- `/web/mapper/MatchStateMapper.java`
- `/web/validator/PlayerNameValidator.java`

### Шаг 7: Тесты и HTML

- `src/test/java/tennis/score/board/model/matchstate/set/SetScoreTest.java`
- `src/test/java/tennis/score/board/model/matchstate/MatchStateTest.java`
- `src/main/webapp/WEB-INF/views/matches.html`
