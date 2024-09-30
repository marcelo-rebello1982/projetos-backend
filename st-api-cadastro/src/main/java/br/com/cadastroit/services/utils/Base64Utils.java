package br.com.cadastroit.services.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import br.com.cadastroit.services.exceptions.BusinessException;


public class Base64Utils {

	public static String encriptar(String senha) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(senha.getBytes());
			Base64 encoder = new Base64();
			return encoder.encodeToString(digest.digest()).trim();
		} catch (NoSuchAlgorithmException ns) {
			throw new BusinessException("Erro ao criptografar senha: " + ns.getMessage());
		}
	}
}
