package es.jcyl.ita.formic.jayjobs.jexl.helpers;

import org.apache.commons.lang3.math.NumberUtils;

public class Mather {
	public double cosine(double x) {
		return Math.cos(x);
	}

	public boolean isNumeric(String str) {
		boolean isNum = NumberUtils.isCreatable(str);
		return (isNum) ? isNum : NumberUtils.isCreatable(str.replace(",", "."));
	}
}
