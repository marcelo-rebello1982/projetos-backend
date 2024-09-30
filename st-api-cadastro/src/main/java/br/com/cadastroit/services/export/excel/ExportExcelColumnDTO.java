package br.com.cadastroit.services.export.excel;

import br.com.cadastroit.services.export.ExportColumnDTO;

class ExportExcelColumnDTO extends ExportColumnDTO {

	private boolean wrapText;

	protected ExportExcelColumnDTO(Object value, String dateFormat, int order, boolean wrapText) {

		super(value, dateFormat, order);
		this.wrapText = wrapText;
	}

	public boolean isWrapText() {

		return wrapText;
	}

	public void setWrapText(boolean wrapText) {

		this.wrapText = wrapText;
	}

}
