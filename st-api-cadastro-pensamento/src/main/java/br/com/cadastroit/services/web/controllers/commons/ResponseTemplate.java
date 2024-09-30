package br.com.cadastroit.services.web.controllers.commons;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplate implements Serializable {

	private static final long serialVersionUID = -3993351330846600406L;
	private String message;

}
