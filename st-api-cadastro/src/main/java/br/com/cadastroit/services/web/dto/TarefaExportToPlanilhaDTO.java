package br.com.cadastroit.services.web.dto;

import java.time.LocalDateTime;
import java.util.Date;



public class TarefaExportToPlanilhaDTO  {

	private Long id;
	
	private String titulo;

	private String descr;
	
	private String tempoGasto;
	
	private Date prazo;
	
	private Date dataInicio;

	private Date dataFinal;
	
	private LocalDateTime horaInicio;

	private LocalDateTime horaFinal;

	private Boolean encerrado;
	
	private Long departamentoId;
	
	private Date dtUpdate;

	private Long pessoaId;

	
	public Long getId() {
	
		return id;
	}

	
	public void setId(Long id) {
	
		this.id = id;
	}

	
	public String getTitulo() {
	
		return titulo;
	}

	
	public void setTitulo(String titulo) {
	
		this.titulo = titulo;
	}

	
	public String getDescr() {
	
		return descr;
	}

	
	public void setDescr(String descr) {
	
		this.descr = descr;
	}

	
	public String getTempoGasto() {
	
		return tempoGasto;
	}

	
	public void setTempoGasto(String tempoGasto) {
	
		this.tempoGasto = tempoGasto;
	}

	
	public Date getPrazo() {
	
		return prazo;
	}

	
	public void setPrazo(Date prazo) {
	
		this.prazo = prazo;
	}

	
	public Date getDataInicio() {
	
		return dataInicio;
	}

	
	public void setDataInicio(Date dataInicio) {
	
		this.dataInicio = dataInicio;
	}

	
	public Date getDataFinal() {
	
		return dataFinal;
	}

	
	public void setDataFinal(Date dataFinal) {
	
		this.dataFinal = dataFinal;
	}

	
	public LocalDateTime getHoraInicio() {
	
		return horaInicio;
	}

	
	public void setHoraInicio(LocalDateTime horaInicio) {
	
		this.horaInicio = horaInicio;
	}

	
	public LocalDateTime getHoraFinal() {
	
		return horaFinal;
	}

	
	public void setHoraFinal(LocalDateTime horaFinal) {
	
		this.horaFinal = horaFinal;
	}

	
	public Boolean getEncerrado() {
	
		return encerrado;
	}

	
	public void setEncerrado(Boolean encerrado) {
	
		this.encerrado = encerrado;
	}

	
	public Long getDepartamentoId() {
	
		return departamentoId;
	}

	
	public void setDepartamentoId(Long departamentoId) {
	
		this.departamentoId = departamentoId;
	}

	
	public Date getDtUpdate() {
	
		return dtUpdate;
	}

	
	public void setDtUpdate(Date dtUpdate) {
	
		this.dtUpdate = dtUpdate;
	}

	
	public Long getPessoaId() {
	
		return pessoaId;
	}

	
	public void setPessoaId(Long pessoaId) {
	
		this.pessoaId = pessoaId;
	}
	
	

}
