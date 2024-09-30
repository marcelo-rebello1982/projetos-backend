package br.com.cadastroit.services.api.services;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManagerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.commons.api.ViewCidadeEmpresa;
import br.com.cadastroit.services.commons.api.ViewTotalEstado;
import br.com.cadastroit.services.exceptions.NfServException;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.utils.UtilDate;
import br.com.cadastroit.services.web.dto.Filters;
import lombok.AllArgsConstructor;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@AllArgsConstructor
public class NfServService {

    private static final String MODE = "Error on %s mode to %s, [error] = %s";
    private static final String OBJECT = "";
    private final CommonsRepository commonsRepository;

    public List<Filters> vDashTotalizadorDmStProc(Filters filters, EntityManagerFactory entityManagerFactory) throws ClassNotFoundException, SQLException {

        final Connection connection = this.commonsRepository.getUtilities().getConnection();
        List<Filters> listDto = new ArrayList<>();
        String fDateBegin = UtilDate.toDateString(UtilDate.addMinHourToDate(UtilDate.toDateString(filters.getDtEmissIni())), "dd/MM/yyyy");
        String fDateEnd = UtilDate.toDateString(UtilDate.addMaxHourToDate(UtilDate.toDateString(filters.getDtEmissFim())), "dd/MM/yyyy");

        StringBuilder sBuilder = new StringBuilder("SELECT * FROM V_DASH_TOTALIZADOR V ");

        if (filters.getEmpresa() != null && filters.getEmpresa().getUf() != null) {
            //sBuilder
            //	.append("INNER JOIN EMPRESA E ON E.UF = '")
            //		.append(filters.getSiglaEstado()).append("' ");
        }

        sBuilder.append("WHERE V.MULTORG_CD = '")
                .append(filters.getMultOrgCd()).append("' ")
                .append("AND V.DT_EMISS >= TO_DATE ('")
                .append(fDateBegin).append("','DD/MM/YYYY') ")
                .append("AND V.DT_EMISS <= TO_DATE ('")
                .append(fDateEnd).append("','DD/MM/YYYY') ")
                .append("ORDER BY V.DT_EMISS DESC");

        try {
            try (Statement statement = connection.createStatement();
                 ResultSet rSet = statement.executeQuery(sBuilder.toString())) {
                while (rSet.next()) {
                    Filters filtersDto = Filters.builder()
                            .multOrgCd(rSet.getString("MULTORG_CD"))
                            .dtEmissao(rSet.getString("DT_EMISS"))
                            .qtdProcess(rSet.getLong("PROCESSAMENTO"))
                            .qtdPendenc(rSet.getLong("PENDENCIA"))
                            .qtdAutoriz(rSet.getLong("AUTORIZADA"))
                            .qtdCancel(rSet.getLong("CANCELADA"))
                            .build();

                    listDto.add(filtersDto);
                }
            }
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "PKB_V_DASH_TOTALIZADOR", OBJECT, ex.getMessage()));
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return listDto;
    }

    public List<ViewTotalEstado> vDashTotCidadeEstado(Filters filters,
                                                      Map<String, String> requestParams, EntityManagerFactory entityManagerFactory) throws ClassNotFoundException, SQLException {

        AtomicBoolean order = new AtomicBoolean(true);
        final Connection connection = this.commonsRepository.getUtilities().getConnection();
        List<ViewTotalEstado> listDto = new ArrayList<>();

        requestParams.entrySet().stream()
                .filter(entry -> entry.getKey()
                        .equalsIgnoreCase("ORDER"))
                .forEach(entry -> {
                    if (entry.getValue() == null || entry.getValue().equals("desc")) {
                        order.set(true);
                    } else {
                        order.set(false);
                    }
                });

        if (filters.getSiglaEstado() == null) {

        }
        
        StringBuilder toSelect = new StringBuilder();
        toSelect.append("V.MULTORG_CD, ");
        toSelect.append("V.DT_EMISS, ");
        toSelect.append("V.UF, ");
        toSelect.append("V.IBGE_CIDADE, ");
        toSelect.append("V.DESCR_CIDADE, ");
        toSelect.append("V.PROCESSAMENTO, ");
        toSelect.append("V.PENDENCIA, ");
        toSelect.append("V.AUTORIZADA, ");
        toSelect.append("V.CANCELADA, ");
        toSelect.append("V.DM_DB_DESTINO ");
        toSelect.append("FROM ");
        
        StringBuilder sqlQryString = this.commonsRepository.getUtilities().createQuery(false, toSelect.toString(), "V_DASH_TOT_CIDADE_ESTADO" ,
                        this.commonsRepository.createPredicates(filters != null ?
                                        true : false, // no predicates,apenas confere se o filtro é null.
                                filters))
                .append(" ORDER BY V.DT_EMISS")
                .append(order.get() == true
                        ? " DESC" : " ASC");

        try {
            try (Statement statement = connection.createStatement();
                 ResultSet rSet = statement.executeQuery(sqlQryString.toString())) {
                while (rSet.next()) {
                    ViewTotalEstado object = ViewTotalEstado.builder()
                            .multOrgCd(rSet.getString("MULTORG_CD"))
                            .dtEmissao(rSet.getString("DT_EMISS"))
                            .uf(rSet.getString("UF"))
                            .qtdTotalNotasProc(rSet.getLong("PROCESSAMENTO"))
                            .qtdTotalNotasPend(rSet.getLong("PENDENCIA"))
                            .qtdTotalNotasAutoriz(rSet.getLong("AUTORIZADA"))
                            .qtdTotalNotasCancel(rSet.getLong("CANCELADA"))
                            .viewCidadeEmpresa(ViewCidadeEmpresa.builder()
                                    .ibgeCidade(rSet.getString("IBGE_CIDADE"))
                                    .descrCidade(rSet.getString("DESCR_CIDADE"))
                                    .dmDbDestino(rSet.getInt("DM_DB_DESTINO"))
                                    .uf(rSet.getString("UF"))
                                    .build())
                            .build();

                    listDto.add(object);
                }
            }
        } catch (Exception ex) {
            throw new NfServException(String.format(MODE, "PKB_V_DASH_TOT_CIDADE_ESTADO", OBJECT, ex.getMessage()));
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return listDto;
    }
}