package ru.yandex.practicum.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.ActionType;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.ConditionOperation;
import ru.yandex.practicum.analyzer.model.ConditionType;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис обработки событий хабов (устройства и сценарии).
 */
@Slf4j
@Service
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    public HubEventService(SensorRepository sensorRepository,
                          ScenarioRepository scenarioRepository,
                          ConditionRepository conditionRepository,
                          ActionRepository actionRepository,
                          ScenarioConditionRepository scenarioConditionRepository,
                          ScenarioActionRepository scenarioActionRepository) {
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
        this.scenarioConditionRepository = scenarioConditionRepository;
        this.scenarioActionRepository = scenarioActionRepository;
    }

    /**
     * Обрабатывает сообщение {@link HubEventAvro} и сохраняет изменения в БД.
     */
    @Transactional
    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();
        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(event.getHubId(), deviceAdded);
            return;
        }

        if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(event.getHubId(), deviceRemoved);
            return;
        }

        if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(event.getHubId(), scenarioAdded);
            return;
        }

        if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(event.getHubId(), scenarioRemoved);
            return;
        }

        log.warn("Неизвестный тип payload в HubEventAvro: {}", payload == null ? null : payload.getClass());
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro payload) {
        String sensorId = payload.getId();

        Optional<Sensor> existingOpt = sensorRepository.findByIdAndHubId(sensorId, hubId);
        if (existingOpt.isPresent()) {
            log.info("DeviceAdded: sensor уже существует, игнорируем: hubId={}, sensorId={}", hubId, sensorId);
            return;
        }

        Sensor sensor = new Sensor(sensorId, hubId);
        sensorRepository.save(sensor);
        log.info("DeviceAdded: sensor сохранён: hubId={}, sensorId={}", hubId, sensorId);
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro payload) {
        String sensorId = payload.getId();

        Optional<Sensor> existingOpt = sensorRepository.findByIdAndHubId(sensorId, hubId);
        if (existingOpt.isEmpty()) {
            log.info("DeviceRemoved: sensor не найден, игнорируем: hubId={}, sensorId={}", hubId, sensorId);
            return;
        }

        sensorRepository.delete(existingOpt.get());
        log.info("DeviceRemoved: sensor удалён: hubId={}, sensorId={}", hubId, sensorId);
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro payload) {
        String scenarioName = payload.getName();
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(hubId, scenarioName);

        if (scenarioOpt.isEmpty()) {
            log.info("ScenarioRemoved: сценарий не найден, игнорируем: hubId={}, name={}", hubId, scenarioName);
            return;
        }

        Scenario scenario = scenarioOpt.get();
        Long scenarioId = scenario.getId();

        scenarioConditionRepository.deleteByScenario_Id(scenarioId);
        scenarioActionRepository.deleteByScenario_Id(scenarioId);
        scenarioRepository.delete(scenario);

        log.info("ScenarioRemoved: сценарий удалён: hubId={}, name={}", hubId, scenarioName);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro payload) {
        String scenarioName = payload.getName();

        if (log.isDebugEnabled()) {
            List<ScenarioConditionAvro> conditions = payload.getConditions();
            List<DeviceActionAvro> actions = payload.getActions();

            String conditionsSummary = conditions == null ? "[]" : conditions.stream()
                    .map(c -> "{" + c.getSensorId() + " " + c.getType() + " " + c.getOperation() + " " + c.getValue() + "}")
                    .toList()
                    .toString();

            String actionsSummary = actions == null ? "[]" : actions.stream()
                    .map(a -> "{" + a.getSensorId() + " " + a.getType() + " " + a.getValue() + "}")
                    .toList()
                    .toString();

            log.debug("ScenarioAdded payload: hubId={}, name={}, conditionsCount={}, actionsCount={}, conditions={}, actions={}",
                    hubId,
                    scenarioName,
                    conditions == null ? 0 : conditions.size(),
                    actions == null ? 0 : actions.size(),
                    conditionsSummary,
                    actionsSummary);
        }

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseGet(() -> new Scenario(hubId, scenarioName));

        scenario.setHubId(hubId);
        scenario.setName(scenarioName);
        scenario = scenarioRepository.save(scenario);

        Long scenarioId = scenario.getId();
        scenarioConditionRepository.deleteByScenario_Id(scenarioId);
        scenarioActionRepository.deleteByScenario_Id(scenarioId);

        List<ScenarioCondition> scenarioConditions = mapConditions(hubId, scenario, payload.getConditions());
        List<ScenarioAction> scenarioActions = mapActions(hubId, scenario, payload.getActions());

        if (!scenarioConditions.isEmpty()) {
            scenarioConditionRepository.saveAll(scenarioConditions);
        }
        if (!scenarioActions.isEmpty()) {
            scenarioActionRepository.saveAll(scenarioActions);
        }

        log.info("ScenarioAdded: сценарий сохранён/обновлён: hubId={}, name={}, conditions={}, actions={}",
                hubId, scenarioName, scenarioConditions.size(), scenarioActions.size());
    }

    private List<ScenarioCondition> mapConditions(String hubId, Scenario scenario, List<ScenarioConditionAvro> conditionsAvro) {
        if (conditionsAvro == null || conditionsAvro.isEmpty()) {
            return List.of();
        }

        List<ScenarioCondition> result = new ArrayList<>(conditionsAvro.size());

        for (ScenarioConditionAvro conditionAvro : conditionsAvro) {
            String sensorId = conditionAvro.getSensorId();
            Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                    .orElseGet(() -> sensorRepository.save(new Sensor(sensorId, hubId)));

            Integer value = mapConditionValueToInteger(conditionAvro.getValue());

            Condition condition = new Condition(
                    mapConditionType(conditionAvro.getType()),
                    mapConditionOperation(conditionAvro.getOperation()),
                    value
            );
            condition = conditionRepository.save(condition);

            result.add(new ScenarioCondition(scenario, sensor, condition));
        }

        return result;
    }

    private List<ScenarioAction> mapActions(String hubId, Scenario scenario, List<DeviceActionAvro> actionsAvro) {
        if (actionsAvro == null || actionsAvro.isEmpty()) {
            return List.of();
        }

        List<ScenarioAction> result = new ArrayList<>(actionsAvro.size());

        for (DeviceActionAvro actionAvro : actionsAvro) {
            String sensorId = actionAvro.getSensorId();
            Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                    .orElseGet(() -> sensorRepository.save(new Sensor(sensorId, hubId)));

            Integer value = actionAvro.getValue() == null ? null : (Integer) actionAvro.getValue();

            Action action = new Action(mapActionType(actionAvro.getType()), value);
            action = actionRepository.save(action);

            result.add(new ScenarioAction(scenario, sensor, action));
        }

        return result;
    }

    private ConditionType mapConditionType(ConditionTypeAvro type) {
        return ConditionType.valueOf(type.name());
    }

    private ConditionOperation mapConditionOperation(ConditionOperationAvro operation) {
        return ConditionOperation.valueOf(operation.name());
    }

    private ActionType mapActionType(ActionTypeAvro type) {
        return ActionType.valueOf(type.name());
    }

    private Integer mapConditionValueToInteger(Object avroUnionValue) {
        if (avroUnionValue == null) {
            return null;
        }

        if (avroUnionValue instanceof Integer i) {
            return i;
        }

        if (avroUnionValue instanceof Boolean b) {
            return b ? 1 : 0;
        }

        throw new IllegalArgumentException("Unsupported ScenarioConditionAvro.value type: " + avroUnionValue.getClass());
    }
}
