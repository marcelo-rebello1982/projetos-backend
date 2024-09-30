package br.com.cadastroit.services.web.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterOperation {

    EQUAL("eq"),
    NOT_EQUAL("neq"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL_TO("gte"),
    LESS_THAN("lt"),
    LESSTHAN_OR_EQUAL_TO("lte"),
    IN("in"),
    NOT_IN("nin"),
    BETWEEN("btn"),
    CONTAINS("like"),
    NOT_CONTAINS("notLike"),
    IS_NULL("isnull"),
    IS_NOT_NULL("isnotnull"),
    START_WITH("startwith"),
    END_WITH("endwith"),
    IS_EMPTY("isempty"),
    IS_NOT_EMPTY("isnotempty"),
    JOIN("jn"),
    IS("is");

    private final String value;

    FilterOperation(String value) {
        this.value = value;
    }

    public static FilterOperation fromValue(String value) {
        for (FilterOperation op : FilterOperation.values()) {

            if (String.valueOf(op.value).equalsIgnoreCase(value)) {
                return op;
            }
        }
        return null;
    }
    
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}