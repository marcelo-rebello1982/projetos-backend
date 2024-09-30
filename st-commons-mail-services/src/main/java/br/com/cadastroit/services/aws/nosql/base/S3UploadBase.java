package br.com.cadastroit.services.aws.nosql.base;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3UploadBase implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID uuid;
    private String fileName;
    private String tipoArquivo;
    private String codStatus;
    private String msgStatus;
    private String bucketName;
    private String bucketLocation;
    private String dateUpload;
    private String eTag;
    private Long notafiscalId;
    private String params;
    private String paramsHash;

}
