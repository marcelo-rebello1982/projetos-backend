package br.com.cadastroit.services.api.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sexo {

	MASCULINO("Masculino"),
	FEMININO("Feminino");

	String description;

	Sexo(String description) {

		this.description = description;
	}

	@Override
	public String toString() {

		return this.description;
	}

	@JsonCreator
	public static Sexo findValue(@JsonProperty("description") String descricao) {

		return Arrays.stream(Sexo.values()).filter(pt -> pt.description.equals(descricao)).findFirst().get();
	}
}