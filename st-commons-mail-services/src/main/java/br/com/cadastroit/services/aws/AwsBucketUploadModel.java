package br.com.cadastroit.services.aws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsBucketUploadModel {
	
	private String fileName;
	private Integer code;
	private String textCode;
	private String eTag;
	
}
