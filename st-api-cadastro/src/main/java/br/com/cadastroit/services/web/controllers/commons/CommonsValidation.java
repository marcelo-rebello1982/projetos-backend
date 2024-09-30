package br.com.cadastroit.services.web.controllers.commons;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.api.services.EmpresaService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.repositories.DepartamentoRepository;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.EnderecoRepository;
import br.com.cadastroit.services.repositories.PessoaEmpresaRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.repositories.TarefaRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
public class CommonsValidation {

	private final ObjectMapper mapperJson = new ObjectMapper();
	
	private EmpresaService empresaService;
	
	private DepartamentoRepository departamentoRepository;
	
	private EmpresaRepository empresaRepository;
	
	private PessoaRepository pessoaRepository;
	
	private TarefaRepository tarefaRepository;
	
	private EnderecoRepository enderecoRepository;
	
	private PessoaEmpresaRepository pessoaEmpresaRepository;
	
	private PessoaService pessoaService;
	
	private TarefaService tarefaService;
	
	public Departamento recuperarDepartamento(Long id, EntityManagerFactory entityManager) {
		try {
			return departamentoRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		}
	}
	
	public Empresa recuperarEmpresa(Long id, EntityManagerFactory entityManager) {
		try {
			return empresaRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		}
	}
	
	public Pessoa recuperarPessoa(Long id, EntityManagerFactory entityManager) {
		try {
			return pessoaRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		}
	}
	
	public Tarefa recuperarTarefa(Long id, EntityManagerFactory entityManager) {
		try {
			return tarefaRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		}
	}
	
	public String createResponseTemplate(String message) {
	    try {
	        ResponseTemplate responseTemplate = ResponseTemplate.builder().message(message).build();
	        return convertToJson(responseTemplate, responseTemplate.getClass());
	    } catch (JsonProcessingException ex) {
	        log.error(ex.getMessage());
	        return null;
	    }
	}

	public String convertToJson(Object data, Class<? extends ResponseTemplate> clazz) throws JsonProcessingException {
		mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(data);
	}
}
