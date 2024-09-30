package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TAREFA")
@SequenceGenerator(name = "TAREFA_SEQ", sequenceName = "TAREFA_SEQ", allocationSize = 1, initialValue = 1)
public class Tarefa implements Serializable {

	private static final long serialVersionUID = -4259460547095223762L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAREFA_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "TITULO", nullable = false)
	private String titulo;

	@Column(name = "DESCR", nullable = false)
	private String descr;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "PRAZO")
	private Date prazo;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INI")
	private Date dataInicio;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_FIN")
	private Date dataFinal;

	@Column(name = "HR_INI",nullable = true)
	private LocalDateTime horaInicio;

	@Column(name = "HR_FIN",nullable = true)
	private LocalDateTime horaFinal;

	@Column(name = "ENCERRADO")
	private Boolean encerrado;

	@ManyToOne
	@JoinColumn(name = "DEPARTAMENTO_ID")
	private Departamento departamento;

	@ManyToOne
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;

	@Builder.Default
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_UPDATE", nullable = true)
	private Date dtUpdate = new Date(System.currentTimeMillis());
	
	@Transient
	private Long totalTarefas;
	
	@Transient
	private Long totalDepartamentos;
	
	@Transient
	private Long totalPessoas;
	
	@Transient
	public String getTempoGasto() {

		if (horaInicio != null && horaFinal != null && Boolean.TRUE.equals(encerrado)) {
			Duration duration = Duration.between(horaInicio, horaFinal);
			return String.format("%dh %dm", duration.toHours(), duration.toMinutesPart());
		}
		return "0h 0m";
	}
	
	@Transient
	public String getTempoRestante() {

		if (horaInicio != null && horaFinal != null && Boolean.TRUE.equals(encerrado)) {
			Duration duration = Duration.between(horaInicio, horaFinal);
			return String.format("%dh %dm", duration.toHours(), duration.toMinutesPart());
		}
		return "0h 0m";
	}
	
	@Transient
	public Double getMediaTarefa() {

		if (horaInicio != null && horaFinal != null && Boolean.TRUE.equals(encerrado)) {
			Duration duration = Duration.between(horaInicio, horaFinal);
			return duration.toHours() + duration.toMinutesPart() / 60.0;
		}
		return 0.0;
	}

	@Transient
	public Long getMediaTarefa_() {

		if (horaInicio != null && horaFinal != null && Boolean.TRUE.equals(encerrado)) {
			Duration duration = Duration.between(horaInicio, horaFinal);
			return duration.toHours() + duration.toMinutesPart() / 60;
		}
		return 0L;
	}
}
