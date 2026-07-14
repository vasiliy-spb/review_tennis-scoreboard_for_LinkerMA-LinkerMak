package tennis.score.board.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tennis.score.board.model.matchstate.MatchState;
import tennis.score.board.model.matchstate.ScoreboardSnapshot;
import tennis.score.board.web.dto.MatchStateDTO;

@Mapper(componentModel = "spring") // для "spring" в mapstruct есть специальная константа: MappingConstants.ComponentModel.SPRING
public interface MatchStateMapper {
    MatchStateDTO toMatchStateDTO(ScoreboardSnapshot snapshot);
}
