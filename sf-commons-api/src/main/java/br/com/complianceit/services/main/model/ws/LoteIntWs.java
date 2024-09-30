package br.com.complianceit.services.main.model.ws;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class LoteIntWs implements Serializable{
	
	private static final long serialVersionUID = 8095581166233145606L;
    public static final int RECEBIDO = 1;
    public static final int EM_PROCESSAMENTO = 2;
    public static final int PROCESSADO = 3;
    public static final int PROCESSADO_COM_ERRO = 4;
    public static final int REJEITADO = 5;

    private Long id;
    private Long multOrgId;
    private Long tipoObjIntegrId;
    private Date dataHoraRecebimento;
    private Date dataHoraProcessamento;
    private Integer dmStProc;
    private byte[] xmlRecebido;
    private String diretorioLote;
    private Integer dmProcXml;

}
