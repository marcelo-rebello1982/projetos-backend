package br.com.cadastroit.services.web.dto;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Created by Deni Setiawan on 08/12/2022.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    Long id;
    String username;
    String password;
    String roles;
    String permissions;
    Integer blocked;
    Integer active;


    String createdBy;
    Date createdDate;
    String updatedBy;
    Date updatedDate;

}
