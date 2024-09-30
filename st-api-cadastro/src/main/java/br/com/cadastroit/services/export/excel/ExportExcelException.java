package br.com.cadastroit.services.export.excel;

public class ExportExcelException extends RuntimeException {

	private static final long serialVersionUID = 832082356655568540L;

	public ExportExcelException(String mensagem) {

		super(mensagem);
	}

	public ExportExcelException(String mensagem, Throwable e) {

		super(mensagem, e);

	}

}
