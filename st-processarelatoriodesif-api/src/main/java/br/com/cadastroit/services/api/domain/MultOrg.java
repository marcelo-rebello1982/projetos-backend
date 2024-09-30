package br.com.cadastroit.services.api.domain;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "MULT_ORG")
@SequenceGenerator(name = "MULTORG_SEQ", sequenceName = "MULTORG_SEQ", allocationSize = 1, initialValue = 1)
public class MultOrg implements Serializable{

    private static final long serialVersionUID = -7680302850471548894L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "MULTORG_SEQ")
    @Column(name = "ID")
    private Long id;

    @Column(name = "CD")
    private String cd;

    @Column(name = "HASH")
    private String hash;

}
