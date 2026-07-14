package tennis.score.board.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tennis.score.board.model.entity.Match;
import tennis.score.board.web.dto.MatchDTO;

@Mapper(componentModel = "spring") // для "spring" в mapstruct есть специальная константа: MappingConstants.ComponentModel.SPRING
public interface MatchMapper {

    @Mapping(source = "player1.name", target = "player1Name")
    @Mapping(source = "player2.name", target = "player2Name")
    @Mapping(source = "winner.name", target = "winnerName")
    MatchDTO toMatchDTO(Match match);
}
