package br.com.cadastroit.services.export.excel;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import br.com.cadastroit.services.exceptions.ImportacaoTxtException;
import br.com.cadastroit.services.utils.DateUtils;
import br.com.cadastroit.services.utils.NumberUtils;
import br.com.cadastroit.services.utils.StringUtils;
import br.com.cadastroit.services.web.dto.ImportacaoArquivoAbstractDTO;

public abstract class ImportacaoArquivoAbstract<T extends ImportacaoArquivoAbstractDTO> {

	protected Class<T> typeDTO;
	protected List<Field> declaredFields = new ArrayList<>();
	protected List<Method> declaredMethods = new ArrayList<>();
	protected String filename = Long.toString(Calendar.getInstance().getTimeInMillis());
	protected List<String> errors = new ArrayList<>();

	protected ImportacaoArquivoAbstract(Class<T> clazz) {

		this.typeDTO = clazz;

		List<AccessibleObject> accessible = this.getFieldsMethodsRecursive(this.typeDTO).collect(Collectors.toList());
		this.declaredFields = accessible.stream().filter(a -> a instanceof Field).map(Field.class::cast).collect(Collectors.toList());
		this.declaredMethods = accessible.stream().filter(a -> a instanceof Method).map(Method.class::cast).collect(Collectors.toList());
	}

	protected String getFilename() {

		return filename;
	}

	protected boolean stopReadOnError() {

		return true;
	}

	protected List<String> getErrors() {

		return this.errors;
	}

	protected boolean hasErrors() {

		return CollectionUtils.isNotEmpty(this.getErrors());
	}

	protected String getErrorsAsString() {

		return this.getErrorsAsString(null);
	}

	protected String getErrorsAsString(CharSequence delimiter) {

		if (delimiter == null)
			delimiter = StringUtils.SPACE;

		return this.getErrors().stream().collect(Collectors.joining(delimiter));
	}

	protected <R> R stringToReturnType(Class<R> returnType, String value) {

		if (returnType == int.class || returnType == Integer.class) {

			Integer intVal = StringUtils.isNotBlank(value) ? Integer.valueOf(NumberUtils.digitsOnly(value)) : null;
			return returnType.cast(intVal);
		}

		if (returnType == long.class || returnType == Long.class) {

			Long longVal = StringUtils.isNotBlank(value) ? Long.valueOf(NumberUtils.digitsOnly(value)) : null;
			return returnType.cast(longVal);
		}

		if (returnType == float.class || returnType == Float.class) {

			Float floatVal = StringUtils.isNotBlank(value) ? Float.valueOf(NumberUtils.digitsOnly(value)) : null;
			return returnType.cast(floatVal);
		}

		if (returnType == double.class || returnType == Double.class) {

			Double doubleVal = StringUtils.isNotBlank(value) ? Double.valueOf(NumberUtils.digitsOnly(value)) : null;
			return returnType.cast(doubleVal);
		}

		if (returnType == BigDecimal.class) {

			BigDecimal bigVal = StringUtils.isNotBlank(value) ? new BigDecimal(NumberUtils.digitsOnly(value)) : null;
			return returnType.cast(bigVal);
		}

		if (returnType == Date.class) {

			Date date = DateUtils.stringToDate(value);
			return returnType.cast(date);
		}

		if (returnType == Calendar.class) {

			Calendar calendar = DateUtils.stringToCalendar(value);
			return returnType.cast(calendar);
		}

		if (returnType == String.class)
			return returnType.cast(value);

		throw new ImportacaoTxtException(String.format("O tipo \"%s\" não é suportado.", returnType.toString()));
	}

	protected Class<?> getFieldReturnType(String fieldName) throws NoSuchMethodException {

		String methodGetName = "get" + fieldName;
		String methodIsName = "is" + fieldName;

		for (Method method : this.declaredMethods) {

			String methodName = method.getName();

			if (methodName.equalsIgnoreCase(methodGetName) || methodName.equalsIgnoreCase(methodIsName))
				return method.getReturnType();
		}

		throw new NoSuchMethodException(String.format("%s, %s", methodGetName, methodIsName));
	}

	protected Method getSetterMethod(String fieldName) throws NoSuchMethodException {

		String methodSetName = "set" + StringUtils.capitalize(fieldName);
		Class<?> returnType = this.getFieldReturnType(fieldName);

		return this.typeDTO.getMethod(methodSetName, returnType);
	}

	protected void setLinha(T dto, int linha) {

		if (dto == null || !(dto instanceof ImportacaoArquivoAbstractDTO))
			return;

		((ImportacaoArquivoAbstractDTO) dto).setLinha(linha);
	}

	private Stream<AccessibleObject> getFieldsMethodsRecursive(Class<?> type) {

		if (type == null)
			return Stream.empty();

		Stream<AccessibleObject> stream = Stream.concat(Stream.of(type.getDeclaredFields()), Stream.of(type.getDeclaredMethods()));

		Class<?> superclass = type.getSuperclass();
		if (superclass != ImportacaoArquivoAbstractDTO.class) {

			Stream<AccessibleObject> sSuper = getFieldsMethodsRecursive(superclass);
			stream = Stream.concat(stream, sSuper);
		}

		return stream;
	}

}