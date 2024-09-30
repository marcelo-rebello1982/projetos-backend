package br.com.cadastroit.services.commons.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import br.com.cadastroit.services.api.enums.StatusProcessamento;
import br.com.cadastroit.services.repositories.CommonsRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
public class NfServCommons {

    private CommonsRepository commonsRepository;

    // aplica o (mapper) a entidade fornecida.
    protected <T, D> D mountDto(Function<T, D> mapper, T entity) {
        return mapper.apply(entity);
    }

    protected <T, D> List<D> mountListDto(Function<T, D> mapper, List<T> list) {
        return list
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public String getKeyName(BigDecimal key) {
        return Arrays
                .stream(StatusProcessamento
                        .values()).filter(s -> s.getValues()
                        .equals(getKeyValue(key)))
                .findFirst()
                .get()
                .name();
    }

    public String getKeyValue(BigDecimal key) {
        return Arrays
                .stream(StatusProcessamento
                        .values())
                .filter(s -> s.getKeys()
                        .equals(key.intValue()))
                .findFirst()
                .get()
                .getValues();
    }

    public static boolean countIsNotEmpty(Long[] values) {
        return Arrays
                .stream(values)
                .allMatch(Objects::nonNull);
    }

    public boolean containsNull(Long[] values) {
        return Arrays
                .stream(values)
                .anyMatch(Objects::isNull);
    }

    // Map<String, Long> sumQtdProcess_ =
    // listDto.stream().collect(Collectors.groupingBy(
    // FiltersDto::getMultOrgCd,Collectors
    // .summingLong(FiltersDto::getQtdProcess)));
    // Long sumQtdCancelada_ = sumValuesToLong_(listDto, FiltersDto::getQtdCancel);
    public <T> Long sumCurrentValuesInList(List<T> list, Function<T, Number> groupBy) {
        if (groupBy.apply(list.get(0)) instanceof Long)
            return list
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToLong(value -> ((Number) groupBy
                            .apply(value))
                            .longValue())
                    .sum();
        return (long) list
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(value -> ((Number) groupBy
                        .apply(value))
                        .intValue())
                .sum();
    }
    
    public <T> Long sumCurrentValuesInList_(List<ViewTotalEstado> qtd,
                                            ToLongFunction<ViewTotalEstado> groupBy) {
        return (Long) qtd.stream()
                .filter(Objects::nonNull)
                .collect(Collectors
                        .summingLong(groupBy));
    }

    public <T> Long sumValuesToLong(List<T> list, ToLongFunction<T> groupBy) {
        return list
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(groupBy)
                .sum();
    }

    public <T> T sumTotalValues(T... params) {
        return Arrays
                .stream(params)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public Long sumTotalValues(Long... params) {
        return Arrays
                .stream(params)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0L);
    }

    public String msg(String mensagem, Exception ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("msg1").append("msg2");
        return builder.toString();
    }

    private MediaType contentType(HttpStatus status) {
        MediaType type = null;
        if (status == HttpStatus.NOT_FOUND || status == HttpStatus.FORBIDDEN
                || status == HttpStatus.INTERNAL_SERVER_ERROR) {
            type = MediaType.TEXT_HTML;
        } else {
            type = MediaType.APPLICATION_PDF;
        }
        return type;
    }

    public ResponseEntity<byte[]> trataResponse(byte[] contents, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(this.contentType(status));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, status);
    }

    public HashMap<String, String> mountHash(Long empresaId) {
        HashMap<String, String> mapData = new HashMap<>();
        mapData.put("id", empresaId.toString());
        return mapData;
    }
}
