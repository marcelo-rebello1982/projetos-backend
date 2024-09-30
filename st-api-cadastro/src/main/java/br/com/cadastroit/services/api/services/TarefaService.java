package br.com.cadastroit.services.api.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.AtomicDouble;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.api.enums.TipoArquivo;
import br.com.cadastroit.services.exceptions.BusinessException;
import br.com.cadastroit.services.export.TarefasAgendadasExcelHelper;
import br.com.cadastroit.services.repositories.TarefaRepository;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import br.com.cadastroit.services.web.dto.TarefaDetalhesDTO;
import br.com.cadastroit.services.web.mapper.TarefaDetalhesMapper;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TarefaService {

	private static final String ORDER = "order";
	private static final String MODE = "Error on %s MODE to %s, [error] = %s";
	private static final String OBJECT = "TAREFA";
	
	@Autowired
	private TarefaRepository tarefaRepository;
	
	private TarefaDetalhesMapper tarefaDetalhesMapper = Mappers.getMapper(TarefaDetalhesMapper.class);
	
	public List<Pessoa> calculaMediaHoras(List<Pessoa> pessoas) {

		AtomicDouble media = new AtomicDouble(0.0);

		pessoas.stream().forEach(obj -> {
			obj.getTarefas().stream().forEach(tarefa -> {
				obj.setMediaDeHorasPorTarefa(media.addAndGet(formatDouble(tarefa.getMediaTarefa())));
			});
		});

		return pessoas;
	}
	
	public TarefaDetalhesDTO calculaTotalTarefas(List<Tarefa> tarefas) {
		
	    long totalTarefas = tarefas.stream()
	            .map(Tarefa::getId)
	            .distinct()
	            .count();

	    long totalPessoas = tarefas.stream()
	            .map(Tarefa::getPessoa)
	            .map(Pessoa::getId)
	            .distinct()
	            .count();

	    long totalDepartamentos = tarefas.stream()
	            .map(Tarefa::getDepartamento)
	            .distinct()
	            .map(Departamento::getId)
	            .count();

	    TarefaDetalhesDTO tarefasDetalhesDto = TarefaDetalhesDTO.builder()
	    		.totalPessoas(totalPessoas)
	    		.totalDepartamentos(totalDepartamentos)
	    		.totalTarefas(totalTarefas)
	    		.tarefas(tarefaDetalhesMapper.toDto(ordenarTarefas(tarefas)))
	    		.build();
	    
	    return tarefasDetalhesDto;
	}
	
	private List<Tarefa> ordenarTarefas(List<Tarefa> list) {

		return list.stream().sorted(Comparator.comparing(Tarefa::getDescr)).collect(Collectors.toList());
	}
	

	public byte[] exportar(List<Tarefa> tarefas, TipoArquivo tipoArquivo) throws Exception {

		List<TarefaDTO> tarefasDto = this.tarefaDetalhesMapper.toDto(tarefas);

		TarefasAgendadasExcelHelper tarefasExcel = TarefasAgendadasExcelHelper.builder().build();

		switch (tipoArquivo) {

			case PDF:
				return tarefasExcel.exportarPdf(tarefasDto, null, false);

			case XLSX:
				return tarefasExcel.exportarXls(tarefasDto, null, false);
				
			default:
				throw new BusinessException("Tipo inválido.");

		}

	}

	private double formatDouble(double value) {
	    return Math.round(value * 100.0) / 100.0;
	}
}
