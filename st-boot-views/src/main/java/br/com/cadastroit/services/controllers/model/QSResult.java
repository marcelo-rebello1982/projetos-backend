package br.com.cadastroit.services.controllers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QSResult implements Serializable {

    private static final long serialVersionUID = -2499338477626915487L;
    private List<String> columns;
	private List<Object> data;
	private int draw;
	private long recordsFiltered;
    private int recordsTotal;
    
    public List<String> getColumns() {
    	if(columns == null) {
    		this.columns = new ArrayList<>();
    	}
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<Object> getData() {
    	if(data == null) {
    		this.data = new ArrayList<>();
    	}
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
}
