package br.com.complianceit.services.configuration.beans;

import lombok.Builder;
import lombok.Data;

import java.sql.*;

@Builder
@Data
public class DbConnectionBean {

    private final String url = System.getenv("SPRING_DATASOURCE_URL");
    private final String username = System.getenv("SPRING_DATASOURCE_USERNAME");
    private final String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
    private final String driver = System.getenv("SPRING_DATASOURCE_DRIVER_CLASS");

    private final String msgError = "Problemas com o(a) %s, verifique a variavel de ambiente e seu conteudo!!!";

    private void validarParametrosConexao() throws ClassNotFoundException, SQLException{
        if(driver == null){
            throw new ClassNotFoundException(String.format(msgError, "driver"));
        }
        if(url == null){
            throw new SQLException(String.format(msgError, "URL"));
        }
        if(username == null){
            throw new SQLException(String.format(msgError, "credencial de acesso (Usuario ou senha)"));
        }
        if(password == null){
            throw new SQLException(String.format(msgError, "credencial de acesso (Usuario ou senha)"));
        }
    }

    public Connection connection() throws ClassNotFoundException, SQLException {
        try {
            this.validarParametrosConexao();
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex) {
            throw new SQLException(String.format("Erro ao estabelecer conexao com banco de dados, [Erro] = %s", ex.getMessage()));
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException(String.format("Problemas na leitura do driver de conexao. [Erro] = %s", ex.getMessage()));
        }
    }

    public Statement statement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public void closeOperations(Connection connection,
                                Statement statement,
                                PreparedStatement preparedStatement,
                                ResultSet resultSet) throws SQLException{
        try {
            if (statement != null) statement.close();
            if (preparedStatement != null) preparedStatement.close();
            if (resultSet != null) resultSet.close();
            if (connection != null) connection.close();
        } catch (SQLException ex) {
            throw new SQLException(String.format("Erro ao finalizar operacoes com banco de dados. [Erro] = %s", ex.getMessage()));
        }
    }
}
