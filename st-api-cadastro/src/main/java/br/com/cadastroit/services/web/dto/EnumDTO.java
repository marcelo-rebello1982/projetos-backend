package br.com.cadastroit.services.web.dto;
public class EnumDTO {

	private String name;
	private String descricao;
	private String codigo;

	public EnumDTO() {

	}

	public EnumDTO(String name) {

		this.name = name;
	}

	public EnumDTO(String name, String descricao) {

		this(name);
		this.descricao = descricao;
	}

	public EnumDTO(String name, String descricao, String codigo) {

		this(name, descricao);
		this.setCodigo(codigo);
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescricao() {

		return descricao;
	}

	public void setDescricao(String descricao) {

		this.descricao = descricao;
	}

	public String getCodigo() {

		return codigo;
	}

	public void setCodigo(String codigo) {

		this.codigo = codigo;
	}

}