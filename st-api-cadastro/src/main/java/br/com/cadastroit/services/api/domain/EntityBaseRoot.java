package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EntityBaseRoot implements Serializable {

	private static final long serialVersionUID = -7696450501073056934L;

	@Id
	public abstract Long getId();

	public abstract void setId(Long id);

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (!this.getClass().isAssignableFrom(obj.getClass()))
			return false;

		EntityBaseRoot other = (EntityBaseRoot) obj;

		if (this.getId() == null)
			return false;

		if (!this.getId().equals(other.getId()))
			return false;

		return true;
	}
}
