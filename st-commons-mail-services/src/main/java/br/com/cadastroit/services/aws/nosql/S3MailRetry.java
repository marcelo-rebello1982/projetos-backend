package br.com.cadastroit.services.aws.nosql;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@CompoundIndexes(
        value = {
                @CompoundIndex(name = "uuid_idx", def = "{'uuid':1}"),
                @CompoundIndex(name = "notafiscal_idx", def = "{'notafiscalId':1}")
        }
)
@Document(collection = "s3MailRetry")
public class S3MailRetry implements Serializable {

	private static final long serialVersionUID = 2L;
	
	private UUID uuid;
    private String email;
    private Long notafiscalId;
    private Long idOracle;
    private String tbOracle;

}
