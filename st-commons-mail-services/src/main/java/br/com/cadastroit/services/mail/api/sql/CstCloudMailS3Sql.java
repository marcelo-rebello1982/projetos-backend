package br.com.cadastroit.services.mail.api.sql;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CstCloudMailS3Sql {

    public String sqlRetryNotaFiscal() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NF.ID, NF.DM_IND_EMIT, NF.DM_LEGADO, DEST.ID, DEST.EMAIL, NF.NFE_PROC_XML XML, PDF.CONTEUDO PDF, NF.EMPRESA_ID FROM NOTA_FISCAL NF ")
                .append(" INNER JOIN NOTA_FISCAL_DEST DEST ON DEST.NOTAFISCAL_ID = NF.ID ")
                .append(" INNER JOIN NOTA_FISCAL_PDF PDF ON PDF.NOTAFISCAL_ID = NF.ID ")
                .append(" WHERE NF.DM_ST_PROC = 4 ").append(" AND NF.DM_ST_EMAIL = 5 ")
                .append(" AND DEST.EMAIL IS NOT NULL ");
        return sql.toString();
    }

    public String sqlRetryNotaFiscal(Long idNotaFiscalDest) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NF.ID, NF.DM_IND_EMIT, NF.DM_LEGADO, DEST.ID, DEST.EMAIL, NF.NFE_PROC_XML XML, PDF.CONTEUDO PDF, NF.EMPRESA_ID FROM NOTA_FISCAL NF ")
                .append(" INNER JOIN NOTA_FISCAL_DEST DEST ON DEST.NOTAFISCAL_ID = NF.ID ")
                .append(" INNER JOIN NOTA_FISCAL_PDF PDF ON PDF.NOTAFISCAL_ID = NF.ID ")
                .append(" WHERE DEST.ID = ").append(idNotaFiscalDest);
        return sql.toString();
    }

    public String sqlRetryNfDestEmail() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NF.ID, NF.DM_IND_EMIT, NF.DM_LEGADO, NFDEST.ID, NFDEST.EMAIL, NF.NFE_PROC_XML XML, PDF.CONTEUDO PDF, NF.EMPRESA_ID FROM NOTA_FISCAL NF ")
                .append(" INNER JOIN NOTA_FISCAL_DEST DEST ON DEST.NOTAFISCAL_ID = NF.ID ")
                .append(" INNER JOIN NFDEST_EMAIL NFDEST ON NFDEST.NOTAFISCALDEST_ID = DEST.ID ")
                .append(" INNER JOIN NOTA_FISCAL_PDF PDF ON PDF.NOTAFISCAL_ID = NF.ID ")
                .append(" WHERE NF.DM_ST_PROC = 4 ").append(" AND NFDEST.DM_ST_EMAIL = 4 ")
                .append(" AND NFDEST.EMAIL IS NOT NULL ");
        return sql.toString();
    }

    public String sqlRetryNfDestEmail(Long idNfDestEmail) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT NF.ID, NF.DM_IND_EMIT, NF.DM_LEGADO, NFDEST.ID, NFDEST.EMAIL, NF.NFE_PROC_XML XML, PDF.CONTEUDO PDF, NF.EMPRESA_ID FROM NOTA_FISCAL NF ")
                .append(" INNER JOIN NOTA_FISCAL_DEST DEST ON DEST.NOTAFISCAL_ID = NF.ID ")
                .append(" INNER JOIN NFDEST_EMAIL NFDEST ON NFDEST.NOTAFISCALDEST_ID = DEST.ID ")
                .append(" INNER JOIN NOTA_FISCAL_PDF PDF ON PDF.NOTAFISCAL_ID = NF.ID ")
                .append(" WHERE NFDEST.ID = ").append(idNfDestEmail);
        return sql.toString();
    }
}
