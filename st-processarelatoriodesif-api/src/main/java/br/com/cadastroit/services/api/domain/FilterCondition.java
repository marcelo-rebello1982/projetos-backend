package br.com.cadastroit.services.api.domain;

import br.com.cadastroit.services.web.enums.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class FilterCondition {

    private String field;
    private Object value;
    private FilterOperation operator;


}