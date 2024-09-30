package br.com.cadastroit.services.common.aws;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class AwsS3RabbitMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID uuid;
    private String prefix;
    private byte[] file;
    private String fileName;
    private Long id;
    private Map<String,String> reference;
    private String params;
    private Long multorgId;

    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Map<String, String> getReference() {
        return reference;
    }
    public void setReference(Map<String, String> reference) {
        this.reference = reference;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getParams() {
		return params;
	}
    public void setParams(String params) {
		this.params = params;
	}
    public Long getMultorgId(){ return this.multorgId;}
    public void setMultOrgId(Long multorgId){this.multorgId = multorgId;}

    @Override
    public String toString() {
        return "AwsS3RabbitMessage{" +
                "uuid=" + uuid +
                ", prefix='" + prefix + '\'' +
                ", fileName='" + fileName + '\'' +
                ", id=" + id +
                ", reference=" + reference +
                ", multorgId=" + multorgId +
                '}';
    }
}
