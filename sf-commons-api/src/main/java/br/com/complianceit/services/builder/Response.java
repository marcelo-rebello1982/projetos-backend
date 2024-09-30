package br.com.complianceit.services.builder;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2196146022846577422L;
	protected ResponseBuilder responseBuilder;
	
	public static class Builder {
		
		public Response createResponse(ResponseBuilder responseBuilder) {
			Response response 		 = new Response();
			response.responseBuilder = responseBuilder;
			return response;
		}
		
	}
	
}
