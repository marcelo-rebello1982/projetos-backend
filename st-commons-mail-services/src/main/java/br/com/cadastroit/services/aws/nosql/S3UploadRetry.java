package br.com.cadastroit.services.aws.nosql;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "s3UploadRetry")
@CompoundIndexes(value= {
	@CompoundIndex(name = "uuid_idx", def = "{'uuid': 1}", unique = true),
	@CompoundIndex(name = "notafiscalid_idx", def = "{'notafiscalId': 1}")
})
@Getter
@Setter
@Builder
public class S3UploadRetry implements Serializable {

	private static final long serialVersionUID = 1L;
	private UUID uuid;
	private String fileName;
	private String dateUploadRetry;
	private Long notafiscalId;
	private Integer retry;

}
