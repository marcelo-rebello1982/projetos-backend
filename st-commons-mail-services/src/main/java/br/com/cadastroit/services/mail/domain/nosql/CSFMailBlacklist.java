package br.com.cadastroit.services.mail.domain.nosql;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "csfmailblacklist")
public class CSFMailBlacklist {

	@Indexed(name = "idx_email", direction = IndexDirection.ASCENDING)
	private String email;
	private String typeOf;

}
