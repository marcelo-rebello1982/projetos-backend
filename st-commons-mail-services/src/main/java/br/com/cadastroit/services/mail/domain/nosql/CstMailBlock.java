package br.com.cadastroit.services.mail.domain.nosql;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cst_mail_block")
public class CstMailBlock {

    private UUID uuid;
    private Long empresaId;
    private String email;
    private Long times;
    private Long date;

}
