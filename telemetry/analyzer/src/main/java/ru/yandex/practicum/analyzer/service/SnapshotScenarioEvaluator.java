package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис проверки сценариев по снапшоту состояния датчиков.
 */
@Slf4j
@Service
public class SnapshotScenarioEvaluator {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    public SnapshotScenarioEvaluator(ScenarioRepository scenarioRepository,
                                     ScenarioConditionRepository scenarioConditionRepository,
                                     ScenarioActionRepository scenarioActionRepository) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioConditionRepository = scenarioConditionRepository;
        this.scenarioActionRepository = scenarioActionRepository;
    }

    /**
     * Проверяет сценарии, связанные с хабом снапшота, и возвращает список действий для выполнения.
     */
    @Transactional(readOnly = true)
    public List<ScenarioAction> evaluate(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Для hubId={} сценарии не найдены", hubId);
            return List.of();
        }

        Map<Long, Scenario> scenariosById = scenarios.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(Scenario::getId, s -> s));

        Collection<Long> scenarioIds = scenariosById.keySet();
        if (scenarioIds.isEmpty()) {
            return List.of();
        }

        List<ScenarioCondition> allConditions = scenarioConditionRepository.findByScenario_IdIn(scenarioIds);
        List<ScenarioAction> allActions = scenarioActionRepository.findByScenario_IdIn(scenarioIds);

        Map<Long, List<ScenarioCondition>> conditionsByScenarioId = groupConditions(allConditions);
        Map<Long, List<ScenarioAction>> actionsByScenarioId = groupActions(allActions);

        List<ScenarioAction> result = new ArrayList<>();

        for (Long scenarioId : scenarioIds) {
            Scenario scenario = scenariosById.get(scenarioId);
            String scenarioName = scenario.getName();
            List<ScenarioCondition> conditions = conditionsByScenarioId.getOrDefault(scenarioId, List.of());
            List<ScenarioAction> actions = actionsByScenarioId.getOrDefault(scenarioId, List.of());

            boolean matches = matchesScenario(snapshot, scenario, conditions);
            if (matches) {
                log.info("Сценарий подошёл: hubId={}, scenarioName={}, actionsCount={}", hubId, scenarioName, actions.size());
                result.addAll(actions);
            } else {
                log.debug("Сценарий не подошёл: hubId={}, scenarioName={}", hubId, scenarioName);
            }
        }

        for (ScenarioAction scenarioAction : result) {
            if (scenarioAction.getScenario() != null) {
                scenarioAction.getScenario().getName();
            }
            if (scenarioAction.getSensor() != null) {
                scenarioAction.getSensor().getId();
            }
            Action action = scenarioAction.getAction();
            if (action != null) {
                action.getType();
                action.getValue();
            }
        }

        return result;
    }

    private Map<Long, List<ScenarioCondition>> groupConditions(List<ScenarioCondition> conditions) {
        Map<Long, List<ScenarioCondition>> map = new HashMap<>();
        for (ScenarioCondition c : conditions) {
            Long scenarioId = c.getScenario().getId();
            map.computeIfAbsent(scenarioId, k -> new ArrayList<>()).add(c);
        }
        return map;
    }

    private Map<Long, List<ScenarioAction>> groupActions(List<ScenarioAction> actions) {
        Map<Long, List<ScenarioAction>> map = new HashMap<>();
        for (ScenarioAction a : actions) {
            Long scenarioId = a.getScenario().getId();
            map.computeIfAbsent(scenarioId, k -> new ArrayList<>()).add(a);
        }
        return map;
    }

    private boolean matchesScenario(SensorsSnapshotAvro snapshot, Scenario scenario, List<ScenarioCondition> conditions) {
        String hubId = snapshot.getHubId();
        String scenarioName = scenario.getName();

        if (conditions.isEmpty()) {
            log.debug("Сценарий без условий, считаем НЕ подходящим: hubId={}, scenarioName={}", hubId, scenarioName);
            return false;
        }

        for (ScenarioCondition sc : conditions) {
            boolean matches = matchesCondition(snapshot, sc);
            Condition condition = sc.getCondition();

            log.info("Проверка условия: hubId={}, scenarioName={}, sensorId={}, type={}, op={}, expected={}, result={}",
                    hubId,
                    scenarioName,
                    sc.getSensor().getId(),
                    condition.getType(),
                    condition.getOperation(),
                    condition.getValue(),
                    matches
            );

            if (!matches) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesCondition(SensorsSnapshotAvro snapshot, ScenarioCondition scenarioCondition) {
        String sensorId = scenarioCondition.getSensor().getId();
        SensorStateAvro state = snapshot.getSensorsState() == null ? null : snapshot.getSensorsState().get(sensorId);
        if (state == null) {
            log.debug("Состояние датчика не найдено в снапшоте: hubId={}, sensorId={}", snapshot.getHubId(), sensorId);
            return false;
        }

        Condition condition = scenarioCondition.getCondition();
        Integer expected = condition.getValue();

        Integer actual = extractActualValue(state, condition.getType());
        if (actual == null || expected == null) {
            return false;
        }

        return compare(actual, expected, condition.getOperation());
    }

    private Integer extractActualValue(SensorStateAvro state, ConditionType type) {
        Object data = state.getData();
        if (data == null) {
            return null;
        }

        return switch (type) {
            case MOTION -> {
                if (data instanceof MotionSensorAvro motion) {
                    yield motion.getMotion() ? 1 : 0;
                }
                yield null;
            }
            case SWITCH -> {
                if (data instanceof SwitchSensorAvro sw) {
                    yield sw.getState() ? 1 : 0;
                }
                yield null;
            }
            case LUMINOSITY -> {
                if (data instanceof LightSensorAvro light) {
                    yield light.getLuminosity();
                }
                yield null;
            }
            case TEMPERATURE -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getTemperatureC();
                }
                if (data instanceof TemperatureSensorAvro temp) {
                    yield temp.getTemperatureC();
                }
                yield null;
            }
            case CO2LEVEL -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getCo2Level();
                }
                yield null;
            }
            case HUMIDITY -> {
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getHumidity();
                }
                yield null;
            }
        };
    }

    private boolean compare(int actual, int expected, ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> actual == expected;
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
        };
    }
}
