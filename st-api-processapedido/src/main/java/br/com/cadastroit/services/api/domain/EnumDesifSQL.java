package br.com.cadastroit.services.api.domain;

public enum EnumDesifSQL {

    R_LOTEINTWS_TBL("R_LOTEINTWS_DESIF_%s"),
    R_LOTEINTWS_SEQ("RLOTEINTWSDESIF%s_SEQ"),
    R_LOTEINTWS_INSERT_SQL("INSERT INTO %s VALUES (%s,%s,%s)"),
    LOTEINTWS_SELECT_SQL("SELECT L.ID, " +
            "       L.TIPOOBJINTEGR_ID, " +
            "       L.DT_HR_RECEB, " +
            "       L.DM_ST_PROC, " +
            "       L.DT_HR_PROC, " +
            "       L.DM_PROCESSA_XML, " +
            "       TP.CD, " +
            "       TP.DESCR, " +
            "       O.ID, " +
            "       O.CD, " +
            "       O.DESCR " +
            "FROM LOTE_INT_WS L " +
            "INNER JOIN TIPO_OBJ_INTEGR TP ON L.TIPOOBJINTEGR_ID = TP.ID " +
            "INNER JOIN OBJ_INTEGR O ON O.ID = TP.OBJINTEGR_ID " +
            "WHERE L.ID = %s"),
    R_LOTEINTWS_SELECT_SQL("SELECT ID, LOTEINTWS_ID, %s FROM %s WHERE LOTEINTWS_ID = %s");
    private String value;
    EnumDesifSQL(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }

}
