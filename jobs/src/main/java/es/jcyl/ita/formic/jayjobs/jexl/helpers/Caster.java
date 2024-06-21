package es.jcyl.ita.formic.jayjobs.jexl.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.beanutils.ConvertUtilsBean;

public class Caster {
	private ConvertUtilsBean typeConverter = new ConvertUtilsBean();

	public int integer(Object x) {
		return (Integer) typeConverter.convert(x, Integer.class);
	}

	public float decimal(Object x) {
		return (Float) typeConverter.convert(x, Float.class);
	}

	public Date date(Object x) {
		return (Date) typeConverter.convert(x, Date.class);
	}

	public Boolean bool(Object x) {
		return (Boolean) typeConverter.convert(x, Boolean.class);
	}

	public String str(Object x) {
		return x.toString();
	}

	public Collection list(Object x) {
		return Collections.singletonList(x);
	}

}