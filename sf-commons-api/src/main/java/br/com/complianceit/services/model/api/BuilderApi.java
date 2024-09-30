package br.com.complianceit.services.model.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import br.com.complianceit.enterprise.common.util.UtilDate;
import br.com.complianceit.services.model.commons.annotations.OracleField;

public class BuilderApi<T,Y> {

	private Object dataApi;
	private List<T> collectionDataApi;
	private Class<Y> dbClass;
	
	@SuppressWarnings("unchecked")
	protected Class<Y> getDbClass() {
        if (this.dbClass == null) {
            ParameterizedType paramType = (ParameterizedType) this.getClass().getGenericSuperclass();
            this.dbClass = (Class<Y>) paramType.getActualTypeArguments()[1];
        }
        return this.dbClass;
    }
	
	public BuilderApi() {
		this.collectionDataApi = new ArrayList<>();
	}
	
	public List<T> getCollectionDataApi(){
		return this.collectionDataApi;
	}

	@SuppressWarnings("unchecked")
	public List<T> build(List<Y> ruleCollectionDb, Class<T> clazz) {
		ruleCollectionDb.forEach(ruleDb->{
			if(this.dbClass == null)this.dbClass = this.getDbClass();
			try {
				this.dataApi = clazz.getConstructor(this.dbClass).newInstance(ruleDb);
				this.getCollectionDataApi().add((T)this.dataApi);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				   | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		});
		return this.collectionDataApi;
	}
	
	@SuppressWarnings("unchecked")
	public T build(Y ruleDb, Class<T> clazz) {
		if(this.dbClass == null)this.dbClass = this.getDbClass();
		try {
			this.dataApi = clazz.getConstructor(this.dbClass).newInstance(ruleDb);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			   | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return (T)this.dataApi;
	}
	
	public Y buildRuleDb(T api) throws Exception {
		try {
			if(this.dbClass == null)this.dbClass = this.getDbClass();
			Field[] fields = api.getClass().getDeclaredFields();
			Y oracle 	   = this.dbClass.newInstance();
			Stream.of(fields).forEach(f->{
				try {
					f.setAccessible(true);
					Annotation a = f.getAnnotation(OracleField.class);
					if(a != null) {
						OracleField o = (OracleField) a;
						if(o.reference() != null && !o.reference().equals(Object.class)) {
							Class<?> reference = o.reference();
							Object data	= reference.newInstance();
							Field referenceField = data.getClass().getDeclaredField(o.mapField());
							referenceField.setAccessible(true);
							if(f.get(api) != null && new Long(f.get(api).toString()) > 0) { 
								referenceField.set(data, f.get(api));
							}else {
								referenceField.set(data, null);
								data = null;
							}
							Field oracleF = oracle.getClass().getDeclaredField(o.fReference());
							oracleF.setAccessible(true);
							oracleF.set(oracle, data);
							oracleF.setAccessible(false);
							referenceField.setAccessible(false);
						} else {
							Field oracleF = oracle.getClass().getDeclaredField(o.mapField());
							oracleF.setAccessible(true);
							Object data   = f.get(api);
							data 		  = buildValue(api, data, o, f);
							oracleF.set(oracle, data);
							oracleF.setAccessible(false);
						}
						f.setAccessible(false);
					}
				}catch (NoSuchFieldException | InstantiationException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			});
			return oracle;
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new Exception(ex);
		}
	}
	
	public Y buildRuleDb(T api, Y db) throws Exception {
		if(this.dbClass == null)this.dbClass = this.getDbClass();
		Field[] fields = api.getClass().getDeclaredFields();
		Stream.of(fields).forEach(f->{
			try {
				f.setAccessible(true);
				Annotation a = f.getAnnotation(OracleField.class);
				if(a != null) {
					OracleField o = (OracleField) a;
					if(o.reference() != null && !o.reference().equals(Object.class)) {
						Class<?> reference = o.reference();
						Object data	= reference.newInstance();
						Field referenceField = data.getClass().getDeclaredField(o.mapField());
						referenceField.setAccessible(true);
						if(f.get(api) != null && new Long(f.get(api).toString()) > 0) { 
							referenceField.set(data, f.get(api));
						}else {
							referenceField.set(data, null);
							data = null;
						}
						Field oracleF = db.getClass().getDeclaredField(o.fReference());
						oracleF.setAccessible(true);
						oracleF.set(db, data);
						oracleF.setAccessible(false);
						referenceField.setAccessible(false);
					} else {
						Field oracleF = db.getClass().getDeclaredField(o.mapField());
						oracleF.setAccessible(true);
						Object data   = f.get(api);
						data = buildValue(api, data, o, f);
						oracleF.set(db, data);
						oracleF.setAccessible(false);
					}
					f.setAccessible(false);
				}
			}catch (NoSuchFieldException | InstantiationException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		});
		return db;
	}
	
	private Object buildValue(T api, Object data, OracleField o, Field f) throws IllegalAccessException {
		if(data != null && !data.toString().equals("")) {
			if(o.convertTo() != null && !o.convertTo().equals(Object.class)) {
				if(o.convertTo().equals(BigDecimal.class)) {
					BigDecimal result	= null;
					String myValue 		= f.get(api).toString();
					if(myValue.contains("=")) {//Is a domain field
						result = new BigDecimal(myValue.split("=")[0]);
					} else {//Is a value
						if(myValue.contains(",")) {
							myValue = myValue.replace(".", "")
										     .replace(",", ".");
						}
						myValue = myValue.replace("-", "")	
										 .replace("R$", "");
						result  = new BigDecimal(myValue.trim());
					}
					data = result;
				}else if(o.convertTo().equals(BigInteger.class)) {
					BigInteger result	= null;
					String myValue 		= f.get(api).toString();
					if(myValue.contains("=")) {//Is a domain field
						result = new BigInteger(myValue.split("=")[0]);
					} else {//Is a value
						if(myValue.contains(",")) {
							myValue = myValue.replace(".", "")
										     .replace(",", ".");
						}
						myValue = myValue.replace("-", "")	
								 		 .replace("R$", "");
						result  = new BigInteger(myValue.trim());
					}
					data = result;
				}else if(o.convertTo().equals(Long.class)) {
					data = new Long(f.get(api).toString());
				}else if(o.convertTo().equals(Integer.class)) {
					data = new Integer(f.get(api).toString().split("=")[0]);
				}else if(o.convertTo().equals(Date.class)) {
					Date myDate = null;
					if(f.get(api) != null) myDate = UtilDate.toDate(f.get(api).toString(), "dd/MM/yyyy");
					data = myDate;
				}else if(o.convertTo().equals(Timestamp.class)) {
					Timestamp myTimestamp = new Timestamp(UtilDate.toDate(f.get(api).toString(),"dd/MM/yyyy").getTime());
					data = myTimestamp;
				}
			}
		}
		return data;
	}
}
