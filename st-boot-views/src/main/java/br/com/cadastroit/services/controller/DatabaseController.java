package br.com.cadastroit.services.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.config.JWTTokenUtil;
import br.com.cadastroit.services.controllers.model.QSInput;
import br.com.cadastroit.services.controllers.model.QSResult;
import br.com.cadastroit.services.controllers.model.UserDetailsJwt;
import br.com.cadastroit.services.oracle.OracleBootJdbc;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin(value = {"*"})
@RestController
@RequestMapping(path = "database")
public class DatabaseController {

    @Autowired
    private OracleBootJdbc oracleBootJdbc;
    
    @Autowired
	private JWTTokenUtil jwtTokenUtil;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @SuppressWarnings("unused")
    private String removeSpecialChars(String content){
        // Remove caracteres especiais
        if(content != null) {
            String result = Normalizer.normalize(content, Normalizer.Form.NFD);
            result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            result = result.replaceAll("[^a-zA-Z0-9\\.,-:; ]+", "");
            return result;
        }else {
            return null;
        }
    }

    private void getFields(String sql, QSResult qsresult) throws Exception {
    	String[] splitFROM = null;
    	
    	splitFROM = sql.contains("FROM") ? sql.split("FROM") 
    			  : sql.contains("from") ? sql.split("from")
    			  : sql.contains("From") ? sql.split("From") : null;
    	
    	if(splitFROM == null) throw new Exception("Palavra reservada \"FROM\" fora do formato. Sao permitidos 3 formatos validos, FROM, from ou From...");

    	String fields = null;
    	fields = splitFROM[0].contains("SELECT") ? splitFROM[0].replace("SELECT", "").trim()
    		   : splitFROM[0].contains("select") ? splitFROM[0].replace("select", "").trim()
    		   : splitFROM[0].contains("Select") ? splitFROM[0].replace("Select", "").trim() : "";
    		   
        if(fields == null) throw new Exception("Palavra reservada \"SELECT\" fora do formato. Sao permitidos 3 formatos validos, SELECT, select ou Select...");
        if(fields.equals("*")) {
        	throw new Exception("Informe as colunas, o simbolo (*) nao e permitido...");
        }
        
        String[] privateWords = new String[] {"ROWNUM","rownum", "Rownum", "OFFSET", "offset", "Offset", "ROWS", "rows", "Rows", "FETCH", "fetch", "Fetch", "NEXT", "next", "Next", "ONLY", "Only", "only"};
        for(String privateWord : privateWords) {
        	if(sql.contains(privateWord)) {
            	throw new Exception("Uso de palavra reservada ["+privateWord+"] no script de consulta. "
            					  + "As palavras [\"ROWNUM\",\"OFFSET\",\"ROWS\",\"FETCH\",\"NEXT\",\"ONLY\"] nao sao permitidas");
            }
        }
        String[] viewFields = fields.split(",");
        if(viewFields.length == 1) {
        	qsresult.getColumns().add(viewFields[0]);
        } else {
	        for(String viewField : viewFields) {
	        	qsresult.getColumns().add(viewField.trim());
	        }
        }
    }
    
    public String buildCountQueryString(String sql) throws Exception {
    	String[] splitFROM = sql.contains("FROM") ? sql.split("FROM") 
		    			   : sql.contains("from") ? sql.split("from")
		    			   : sql.contains("From") ? sql.split("From") : null;
		    			   
    	if(splitFROM == null) throw new Exception("Palavra reservada \"FROM\" fora do formato. Sao permitidos 3 formatos validos, FROM, from ou From...");
    	String[] whereClausure = sql.contains("WHERE") ? sql.split("WHERE")
    						   : sql.contains("where") ? sql.split("where")
    						   : sql.contains("Where") ? sql.split("Where") : null;
    						   
       if(whereClausure == null) {
    	   String view = splitFROM[1].trim();
    	   return "SELECT COUNT(*) ROWS_NUM FROM "+view.toUpperCase();
       } else {
    	   int limitFrom	= getLimitWordFromSQL(sql, "FROM");
    	   return "SELECT COUNT(*) ROWS_NUM FROM "+sql.substring(limitFrom, sql.length());
       }
    }
    
    private int getLimitWordFromSQL(String sql, String term) {
    	char[] qsChar 	= sql.toCharArray();
 	   	StringBuilder sb = new StringBuilder();
 	   	int indexWord	= 0;
 	   	for(int i = 0; i < qsChar.length; i++) {
			char qsPart = qsChar[i];
			if (sb.toString().equals("")) {
				sb.append(qsPart);
			} else {
				if (qsPart == ' ') {
					if (sb.toString().trim().equalsIgnoreCase(term)) {
						indexWord = i;
					} else {
						sb.delete(0, sb.length());
					}
				} else {
					sb.append(qsPart);
				}
			}
			if(indexWord != 0)break;
 	   	}
 	   	return indexWord;
    }

    @PostMapping("/views/values")
    @ApiOperation(value="Extract values from view using crypt-sql and provide a json result", 
    			  produces = "application/json")
    public ResponseEntity<Object> viewValues(@RequestHeader("Authorization") String token,
    										 @ApiParam(allowEmptyValue = false, value = "A QSInput Json Object to get Values from views")
    										 @RequestBody(required = true)QSInput qsInput){
    	boolean valid 	= token.contains("Bearer");
		if(!valid) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is wrong format, please check it...");
		
    	String jwtToken = token.replace("Bearer", "").trim();
    	String username = this.jwtTokenUtil.getUsernameFromToken(jwtToken);
    	UserDetailsJwt userDetailsJwt = this.mongoTemplate.findOne(new Query(Criteria.where("jwttoken").is(jwtToken)), UserDetailsJwt.class);
    	if(userDetailsJwt == null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido. Nao existe usuario associado a este token...");
		if(!username.equals(userDetailsJwt.getUsername())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido ou vencido...");
		
        Connection connection   = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet		= null;
        QSResult qsResult 		= new QSResult();
        
        try{
            connection = this.oracleBootJdbc.connection();
            int times  = 0;
            String sql = "";
            while (times < 3){
                byte[] qsBytes = Base64.getDecoder().decode(times == 0 ? qsInput.getQs() : sql);
                sql     = new String(qsBytes);
                times++;
            }
            
            String sqlCount = "";
            try {
            	this.getFields(sql, qsResult);
            	sqlCount = this.buildCountQueryString(sql);
            }catch(Exception ex) {
            	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na execucao da [qs] = "+(qsInput.getQs())+", [ERRO] = "+ex.getMessage());
            }
            
            sql				 += " OFFSET "+(qsInput.getStart())+" ROWS FETCH NEXT "+(qsInput.getLength())+" ROWS ONLY";
            pstmt     		  = connection.prepareStatement(sql);
            resultSet 		  = pstmt.executeQuery();
            int totalRecords  = 0;
            try {
	            while (resultSet.next()){
	            	int index	= 1;
	            	int columns = qsResult.getColumns().size();
	            	Object[] values = new Object[columns];
	            	
	            	for(index = 1; index <= columns; index++) {
	            		Object o = resultSet.getObject(index);
	            		values[index-1] = o;
	            	}
	            	qsResult.getData().add(values);
	            }
	            try {
	            	Connection connCount = this.oracleBootJdbc.connection();
	            	Statement stmtCount  = this.oracleBootJdbc.statement(connCount);
	            	ResultSet rsCount	 = stmtCount.executeQuery(sqlCount);
	            	while(rsCount.next()) {
	            		totalRecords = rsCount.getInt(1);
	            	}
	            	
	            	this.oracleBootJdbc.close(connCount, stmtCount, null, rsCount);
	            }catch(Exception ex) {
	            	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na execucao da contagem do resultado final..., [ERRO] = "+ex.getMessage());
	            }
	            qsResult.setDraw(qsInput.getDraw());
	            qsResult.setRecordsTotal(totalRecords);
	            qsResult.setRecordsFiltered(totalRecords);
            }catch(Exception ex) {
            	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na execucao da [qs] = "+(qsInput.getQs())+", [ERRO] = "+ex.getMessage());
            }
        }catch (ClassNotFoundException | SQLException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na execucao da [qs] = "+(qsInput.getQs())+", [ERRO] = "+ex.getMessage());
        }finally {
            try{
                this.oracleBootJdbc.close(connection,null,pstmt,resultSet);
            }catch (SQLException ex){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no encerramento dos objetos de acesso a banco durante a execucao da [qs] = "+(qsInput.getQs())+", [ERRO] = "+ex.getMessage());
            }
        }
        return ResponseEntity.ok(qsResult);
    }

}
