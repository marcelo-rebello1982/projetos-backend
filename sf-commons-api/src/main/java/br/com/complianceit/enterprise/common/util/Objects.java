package br.com.complianceit.enterprise.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;

public class Objects {

	private Boolean newInstance;
	
	public static boolean isNotNull(Object value) {
		return value != null;
	}
	
	public static boolean isNotNullAndNotEmpty(String value){
		return value != null && !value.isEmpty();
	}
	
	public static boolean isNotNullAndNotEmpty(Collection<?> value){
		return value != null && !value.isEmpty();
	}

	public static BigDecimal ifNotNullOrEmptyConvertBigDecimalReplacedBy(String value, String oldChar, String newChar) {
		return value != null && !value.trim().isEmpty() ? new BigDecimal(value.trim().replace(oldChar, newChar)) : null;
	}
	
	public static BigDecimal ifNotNullOrEmptyConvertBigDecimal(String value) {
		return value != null && !value.isEmpty() ? new BigDecimal(value) : null;
	}
	
	public static Integer ifNotNullOrEmptyConvertInteger(String value) {
		return value != null && !value.trim().isEmpty() ? Integer.parseInt(value.trim()) : null;
	}
	
	public Boolean getNewInstance() {				
		if(this.newInstance == null){
			this.newInstance = Boolean.TRUE;
		}
		return newInstance;
	}

	public void setNewInstance(Boolean newInstance) {
		this.newInstance = newInstance;
	}

	public boolean validateNullableFieldObjects(Object o){
		boolean allNullable = true;
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field f : fields){
			f.setAccessible(true);
			Object value;
			try {
				value = f.get(o);
				if(!f.getName().equals("serialVersionUID") && value != null){
					f.setAccessible(false);
					return false;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return allNullable;
	}
	
	public Object validatePopValuesListIdEntity(Object value, Object[] list) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{		
		try {
			this.setNewInstance(Boolean.TRUE);
			Method mIdObjectValue= value.getClass().getDeclaredMethod("getId");
			Long idObjectValue	 = (Long) mIdObjectValue.invoke(value);
			
			Object valueList     = null;
			boolean found = false;
			for(Object o : list){
				Method mIdObjectList = o.getClass().getDeclaredMethod("getId");				
				Long idObjectList = (Long) mIdObjectList.invoke(o);
				if(idObjectValue.equals(idObjectList)){
					valueList = o;
					this.setNewInstance(Boolean.FALSE);
					found 	  = true;
				}
				if(found){
					break;
				}
			}	
			System.out.println("--RESULT VALIDATE--");
			System.out.println("REFERENCE RETURNED ==> "+(found ? "LIST REFERENCE ==> "+valueList.toString() : "NEW REFERENCE ==> "+value.toString()));
			return found ? valueList : value;
		} catch (SecurityException e) {
			throw new SecurityException("SECURITY ERROR, PRIVATE METHOD...");			
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodException("METHOD getId DOES NOT EXISTS...");
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("METHOD getId DOES NOT HAVE ARGS...");
		} catch (IllegalAccessException e) {
			throw new IllegalAccessException("IT'S NOT POSSIBLE ACESS METHOD getId...");
		} catch (InvocationTargetException e) {
			throw new InvocationTargetException(e);			
		}		
	}
	
	public Object validatePopValuesListMemoryAddress(Object value, Object[] list) throws Exception{		
		try {
			String valueMemoryAddress = value.toString();			
			Object valueList     = null;
			boolean found = false;
			for(Object o : list){
				if(valueMemoryAddress.equals(o.toString())){
					valueList = o;
					found 	  = true;
				}
				if(found){
					break;
				}
			}	
			System.out.println("--RESULT VALIDATE--");
			System.out.println("REFERENCE RETURNED ==> "+(found ? "LIST REFERENCE ==> "+valueList.toString() : "NEW REFERENCE ==> "+value.toString()));
			return found ? valueList : value;
		}catch(Exception e){
			throw new Exception("ERROR ON READING THE OBJECT ADDRESS MEMORY...");			
		}		
	}
}
