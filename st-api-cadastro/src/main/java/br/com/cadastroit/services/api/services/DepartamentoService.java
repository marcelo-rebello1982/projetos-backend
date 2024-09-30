package br.com.cadastroit.services.api.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.AtomicDouble;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.web.dto.DepartamentoDTO;
import br.com.cadastroit.services.web.dto.TarefaDetalhesDTO;
import br.com.cadastroit.services.web.mapper.TarefaDetalhesMapper;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class DepartamentoService {

	private static final String ORDER = "order";
	private static final String MODE = "Error on %s MODE to %s, [error] = %s";
	private static final String OBJECT = "DEPARTAMENTO";

	private TarefaDetalhesMapper tarefaDetalhesMapper = Mappers.getMapper(TarefaDetalhesMapper.class);

	public byte[] exportarProdutos(List<DepartamentoDTO> produtosInvalidos, List<DepartamentoDTO> produtosNaoEncontrados) {

		return null;

	}

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

		long totalTarefas = tarefas.stream().map(Tarefa::getId).distinct().count();

		long totalPessoas = tarefas.stream().map(Tarefa::getPessoa).map(Pessoa::getId).distinct().count();

		long totalDepartamentos = tarefas.stream().map(Tarefa::getDepartamento).distinct().map(Departamento::getId).count();

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

	private double formatDouble(double value) {

		return Math.round(value * 100.0) / 100.0;
	}
}
