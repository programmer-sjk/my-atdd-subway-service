package study.unit;

import com.google.common.collect.Lists;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.ExtraFare;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("단위 테스트 - mockito를 활용한 가짜 협력 객체 사용")
public class MockitoTest {
    @Test
    void findAllLines() {
        // given
        LineRepository lineRepository = mock(LineRepository.class);
        StationRepository stationRepository = mock(StationRepository.class);

        when(lineRepository.findAll()).thenReturn(
                Lists.newArrayList(new Line("신분당선", "bg-red-600", new ExtraFare(500)))
        );
        LineService lineService = new LineService(lineRepository, stationRepository);

        // when
        List<LineResponse> responses = lineService.findLines();

        // then
        assertThat(responses).hasSize(1);
    }
}
