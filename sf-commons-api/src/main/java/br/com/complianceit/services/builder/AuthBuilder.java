package br.com.complianceit.services.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.complianceit.services.model.auth.Auth;
import lombok.Getter;

@Getter
public class AuthBuilder {

	protected String json;
	protected Auth auth;
	
	public static class JSON {
		
		private Gson builderGson() {
			GsonBuilder builder = new GsonBuilder()
					 .setLenient()
					 .disableHtmlEscaping()
					 .setPrettyPrinting();
			Gson gson = builder.create();
			return gson;
		}
		
		public AuthBuilder toJson(Auth auth) {
			Gson gson = this.builderGson();
			AuthBuilder authBuilder = new AuthBuilder();
			authBuilder.json = gson.toJson(auth);
			
			return authBuilder;
		}
		
		public AuthBuilder toAuth(String json) {
			Gson gson = this.builderGson();
			AuthBuilder authBuilder = new AuthBuilder();
			authBuilder.auth = gson.fromJson(json, Auth.class);
			return authBuilder;
		}
		
	}
}
