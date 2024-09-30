package br.com.cadastroit.services.api.services;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.repositories.PessoaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PessoaService {

	private static final String ORDER = "order";
	private static final String MODE = "Error on %s MODE to %s, [error] = %s";
	private static final String OBJECT = "PESSOA";
	
	private PessoaRepository repository;
	
	public Long maxId(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		Query query = em.createNativeQuery("select nvl(max(id),1) from PESSOA");
		@SuppressWarnings("unchecked")
		List<BigDecimal> object = query.getResultList();
		return object.get(0).longValue();
	}
	
//	@Override
//	public byte[] obterArquivosAnexados(Long id) {
//
//		SolicitacaoPagamento solicitacaoPagamento = this.solicitacaoPagamentoService.findByProvisao(id);
//
//		if (solicitacaoPagamento != null)
//			return this.solicitacaoPagamentoService.obterArquivosAnexados(solicitacaoPagamento.getId());
//		else {
//			Provisao provisao = this.getRepository().findById(id).get();
//
//			List<ProvisaoArquivoAnexado> arquivosAnexados = provisao.getArquivosAnexados();
//			if (CollectionUtils.isEmpty(arquivosAnexados))
//				throw new BusinessException("Provisão não possui arquivos anexados.");
//
//			List<File> files = arquivosAnexados.stream().map(arquivoAnexado -> {
//
//				String fileUrl = this.storage.download(arquivoAnexado.getNomeSalvo());
//
//				byte[] fileAsBytes;
//
//				try {
//					fileAsBytes = FileUtils.getBytesFromUrl(fileUrl);
//				} catch (IOException e) {
//					throw new BusinessException("Ocorreu um erro ao obter os dados do arquivo anexado.");
//				}
//
//				return FileUtils.bytesToFile(fileAsBytes, arquivoAnexado.getNomeOriginal());
//
//			}).collect(Collectors.toList());
//
//			try {
//				return FileUtils.gerarZip(files);
//			} catch (IOException e) {
//				throw new BusinessException("Ocorreu um erro ao obter os arquivos anexados.");
//			}
//		}
//
//	}
//
//	@Override
//	public void removerArquivosAnexados(PessoaDTO dto, Set<Long> fileToRemove) throws NotFoundException {
//
//		Provisao provisao = super.findById(dto.getId());
//
//		List<ProvisaoArquivoAnexado> arquivosAnexados = provisao.getArquivosAnexados();
//		if (CollectionUtils.isEmpty(arquivosAnexados))
//			return;
//
//		List<ProvisaoArquivoAnexado> arquivosAnexadosToRemove = arquivosAnexados.stream()
//				.filter(arquivo -> fileToRemove.contains(arquivo.getId()))
//				.collect(Collectors.toList());
//
//		if (CollectionUtils.isEmpty(arquivosAnexadosToRemove))
//			return;
//
//		this.excluirArquivosStorage(arquivosAnexadosToRemove);
//
//	}
//
//	private void excluirArquivosStorage(List<ProvisaoArquivoAnexado> arquivosAnexados) {
//
//		if (CollectionUtils.isEmpty(arquivosAnexados))
//			return;
//
//		for (ProvisaoArquivoAnexado arquivoAnexado : arquivosAnexados) {
//			this.storage.delete(arquivoAnexado.getNomeSalvo());
//		}
//	}

}
