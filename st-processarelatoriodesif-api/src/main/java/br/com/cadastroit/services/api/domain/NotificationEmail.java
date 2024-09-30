package br.com.cadastroit.services.api.domain;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmail {
    private String to;
    private String cc;
    private String subject;
    private String bodyMsg;
    private MultipartFile file;
    private boolean isHtmlMsg;
}