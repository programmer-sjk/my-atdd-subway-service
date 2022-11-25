package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final Lines lines;

    public PathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.lines = new Lines(lines);

        initializeVertax();
        initializeEdgeWeight();
    }

    private void initializeVertax() {
        lines.getStations()
                .forEach(graph::addVertex);
    }

    private void initializeEdgeWeight() {
        lines.getSections()
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()),
                        section.getDistance().get())
                );
    }

    public PathResponse shortestPath(Station sourceStation, Station targetStation) {
        validateBefore(sourceStation, targetStation);

        GraphPath<Station, DefaultWeightedEdge> shortestPath =
                new DijkstraShortestPath<>(graph).getPath(sourceStation, targetStation);

        validateAfter(shortestPath);

        List<StationResponse> responses = shortestPath.getVertexList()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new PathResponse(responses, (int) shortestPath.getWeight());
    }

    private void validateBefore(Station sourceStation, Station targetStation) {
        validateSameStation(sourceStation, targetStation);
        validateNotExistLines(sourceStation, targetStation);
    }

    private void validateSameStation(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
    }

    private void validateNotExistLines(Station sourceStation, Station targetStation) {
        if (lines.hasNotStation(sourceStation) || lines.hasNotStation(targetStation)) {
            throw new IllegalArgumentException("최단 경로를 조회하려는 역이 존재하지 않습니다.");
        }
    }

    private void validateAfter(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
        if (shortestPath == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결이 되어 있지 않습니다.");
        }
    }
}