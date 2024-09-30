package br.com.cadastroit.services.api.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.web.mapper.PedidoMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class PedidoService {

	@Autowired
	private PedidoMapper pedidoMapper;
	
	private final ObjectMapper mapperJson = new ObjectMapper();
	
	
//	
//	
//	public Pedido handleAprovedPayment(Long pedidoId, Pessoa pessoa, EntityManagerFactory entityManagerFactory) {
//
//		Optional<Pedido> pedido = Optional.of(pedidoRepository.findById(pedidoId, entityManagerFactory));
//
//		if (!pedido.isPresent()) {
//			throw new PedidoException("PEDIDO NÃO ENCONTRADO");
//		}
//
//		pedido.get().setStatus(PedidoStatus.APROVADO);
//
//		pedidoRepository.save(pedido.get());
//		pedidoClient.handleAprovedPayment(pedido.get().getId(), pedido.get().getStatus());
//		return pedidoClient.handleAprovedPayment(pedido.get().getId(), pedido.get().getStatus());
//
//	}
//	
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
