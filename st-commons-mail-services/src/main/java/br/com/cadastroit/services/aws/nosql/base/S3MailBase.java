package br.com.cadastroit.services.aws.nosql.base;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3MailBase implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID uuid;
    private String baseFileName;
    private byte[] baosXML;
    private byte[] baosPDF;
    private String mailSchedule;
    private Integer status;
    private Long notafiscalId;
}
