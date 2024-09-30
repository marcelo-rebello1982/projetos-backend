package br.com.cadastroit.storage.services.aws;

import org.springframework.stereotype.Component;

@Component
public class SolicitacaoDocumentoStorage extends AmazonStorage {

	protected String getBucketName() {

		return "STORAGE_BUCKET_NAME";
	}

//	public void save(File file, String fileName) {
//
//		try {
//
//			super.sendFile(file, fileName);
//
//		} catch (StorageException e) {
//
//			throw new BusinessException(
//					String.format("Não será possível salvar o arquivo \"%s\" pois já existe um arquivo com esse nome.", fileName));
//		} catch (Exception e) {
//			throw new BusinessException("Ocorreu um erro ao salvar o arquivo.");
//		}
//
//	}
//
//	public String download(String fileName) {
//
//		try {
//
//			return super.getFile(fileName);
//
//		} catch (StorageException e) {
//			throw new BusinessException(String.format("Não foi possível encontrar o arquivo \"%s\"", fileName));
//		} catch (Exception e) {
//			throw new BusinessException(String.format("Não foi possível gerar o link de acesso para o arquivo \"%s\"", fileName), e);
//		}
//
//	}
//
//	public void delete(String fileName) {
//
//		try {
//
//			super.deleteFile(fileName);
//
//		} catch (StorageFileNotFoundException exceptionToIgnore) {
//
//		} catch (Exception e) {
//			throw new BusinessException(String.format("Não foi possível deletar o arquivo \"%s\"", fileName), e);
//		}
//	}
}