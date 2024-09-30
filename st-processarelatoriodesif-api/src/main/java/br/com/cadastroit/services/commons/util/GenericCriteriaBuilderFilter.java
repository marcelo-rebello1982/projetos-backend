package br.com.cadastroit.services.commons.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import br.com.cadastroit.services.api.domain.FilterCondition;

public class GenericCriteriaBuilderFilter {

    private final List<FilterCondition> filterAndConditions;
    private final List<FilterCondition> filterOrConditions;

    private static final Map<String, Function<FilterCondition, Criteria>>
            FILTER_CRITERIA = new HashMap<>();

    static {
        FILTER_CRITERIA.put("EQUAL", condition -> Criteria.where(condition.getField()).is(condition.getValue()));
        FILTER_CRITERIA.put("NOT_EQUAL", condition -> Criteria.where(condition.getField()).ne(condition.getValue()));
        FILTER_CRITERIA.put("GREATER_THAN", condition -> Criteria.where(condition.getField()).gt(condition.getValue()));
        FILTER_CRITERIA.put("GREATER_THAN_OR_EQUAL_TO", condition -> Criteria.where(condition.getField()).gte(condition.getValue()));
        FILTER_CRITERIA.put("LESS_THAN", condition -> Criteria.where(condition.getField()).lt(condition.getValue()));
        FILTER_CRITERIA.put("LESSTHAN_OR_EQUAL_TO", condition -> Criteria.where(condition.getField()).lte(condition.getValue()));
        FILTER_CRITERIA.put("CONTAINS", condition -> Criteria.where(condition.getField()).regex((String) condition.getValue()));
        FILTER_CRITERIA.put("JOIN", condition ->  Criteria.where(condition.getField()).is(condition.getValue()));
    	}

    private Criteria buildCriteria(FilterCondition condition) {
        Function<FilterCondition, Criteria>
                function = FILTER_CRITERIA.get(condition.getOperator().name());

        if (function == null) {
            throw new IllegalArgumentException("Invalid function param type: ");
        }
        return function.apply(condition);
    }

    public GenericCriteriaBuilderFilter() {
        filterOrConditions = new ArrayList<>();
        filterAndConditions = new ArrayList<>();
    }

    public Query addCondition(List<FilterCondition> andConditions, List<FilterCondition> orConditions) {

        if (andConditions != null && !andConditions.isEmpty()) {
            filterAndConditions.addAll(andConditions);
        }
        if (orConditions != null && !orConditions.isEmpty()) {
            filterOrConditions.addAll(orConditions);
        }

        List<Criteria> criteriaAndClause = new ArrayList<>();
        List<Criteria> criteriaOrClause = new ArrayList<>();
        Criteria criteria = new Criteria();

        filterAndConditions.stream().map(condition -> criteriaAndClause.add(buildCriteria(condition))).collect(Collectors.toList());
        filterOrConditions.stream().map(condition -> criteriaOrClause.add(buildCriteria(condition))).collect(Collectors.toList());


        if (!criteriaAndClause.isEmpty() && !criteriaOrClause.isEmpty()) {
            return new Query(criteria.andOperator(criteriaAndClause.toArray(new Criteria[0])).orOperator(criteriaOrClause.toArray(new Criteria[0])));
        } else if (!criteriaAndClause.isEmpty()) {
            return new Query(criteria.andOperator(criteriaAndClause.toArray(new Criteria[0])));
        } else if (!criteriaOrClause.isEmpty()) {
            return new Query(criteria.orOperator(criteriaOrClause.toArray(new Criteria[0])));
        } else {
            return new Query();
        }
    }
}