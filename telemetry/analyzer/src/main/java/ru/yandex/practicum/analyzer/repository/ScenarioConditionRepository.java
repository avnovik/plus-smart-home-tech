package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;

import java.util.Collection;
import java.util.List;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioCondition.ScenarioConditionId> {

    void deleteByScenario_Id(Long scenarioId);

    List<ScenarioCondition> findByScenario_IdIn(Collection<Long> scenarioIds);
}
