package br.com.cadastroit.services.aws.nosql;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.cadastroit.services.aws.nosql.base.S3UploadBase;

@Document(collection = "s3UploadLeg")
@CompoundIndexes(value= {
	@CompoundIndex(name = "uuid_idx", def = "{'uuid': 1}", unique = true),
	@CompoundIndex(name = "uuid_tipo_arquivo_idx", def = "{'uuid': 1, 'tipoArquivo': 1}"),
	@CompoundIndex(name = "file_name_idx", def = "{'fileName': 1}")
})
public class S3UploadLeg extends S3UploadBase {
}
