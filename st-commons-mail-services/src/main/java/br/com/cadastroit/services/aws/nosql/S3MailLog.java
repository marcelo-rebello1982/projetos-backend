package br.com.cadastroit.services.aws.nosql;

import java.io.Serializable;
import java.util.Date;
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

@Document(collection = "s3_mail_log")
@CompoundIndexes(value = {
        @CompoundIndex(name = "uuid_idx", def = "{'uuid':1}"),
        @CompoundIndex(name = "notafiscal_idx", def = "{'notafiscalId':1}"),
        @CompoundIndex(name = "idOrigem_idx", def = "{'idOrigem':1}")
})
public class S3MailLog implements Serializable {

    private final static long serialVersionUID = 1L;
    private UUID uuid;
    private Long notafiscalId;
    private Long idOrigem;
    private String table;
    private Long status;
    private Date date;

    @Builder.Default
    private Long retry = 0l;
}
