package br.com.cadastroit.services.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringJoiner;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.cadastroit.services.config.PostGreSqlJdbc;

@Component
public class Utilities extends PostGreSqlJdbc {

	@Autowired
	protected PostGreSqlJdbc oracleJdbc;

	protected Connection oracleConnection;

	public Connection getConnection(EntityManager entityManager) throws SQLException {

		return oracleJdbc.getConnection(entityManager);
	}

	public static String createQuery(StringBuilder sBuilder, int length) {

		String qry = sBuilder.toString();
		StringJoiner strJoiner = new StringJoiner(",", qry + "(", ")");
		for (int i = 0; i < length; i++) {
			strJoiner.add("?");
		}
		return strJoiner.toString();
	}
}
