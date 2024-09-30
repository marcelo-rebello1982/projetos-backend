package br.com.cadastroit.services.aws.nosql;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.cadastroit.services.aws.nosql.base.S3MailBase;

@Document(collection = "s3_mail_nfs")
@CompoundIndexes(value = {
	@CompoundIndex(name = "uuid_idx", def = "{'uuid':1}"),
	@CompoundIndex(name = "notafiscal_idx", def = "{'notafiscalId':1}")
})
public class S3MailNfs extends S3MailBase {
}
