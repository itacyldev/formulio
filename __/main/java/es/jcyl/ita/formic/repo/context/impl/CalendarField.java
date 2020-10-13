package es.jcyl.ita.formic.forms.context.impl;


/**
 * Stores the Calendar field codes related to the format constant to used them
 * in the expression processor.
 *
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 *         https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 */

public enum CalendarField {
	ERA(0, "G"), YEAR(1, "y"), MONTH(2, "M"), WEEK_OF_YEAR(3,
			"w"), WEEK_OF_MONTH(4, "W"), DAY_OF_MONTH(5, "d"), DAY_OF_YEAR(6,
					"D"), DAY_OF_WEEK(7, "d"), DAY_OF_WEEK_IN_MONTH(8,
							"F"), AM_PM(9, "a"), HOUR(10, "h"), HOUR_OF_DAY(11,
									"H"), MINUTE(12, "m"), SECOND(13,
											"s"), MILLISECOND(14, "S");
	private int code;
	private static String _valid_values = null;
	private String symbol;

	CalendarField(int code, String symbol) {
		this.code = code;
		this.symbol = symbol;
	}

	public int getCode() {
		return this.code;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public static CalendarField getBySymbol(String symb) {
		for (CalendarField cf : CalendarField.values()) {
			if (cf.symbol.equals(symb)) {
				return cf;
			}
		}
		throw new IllegalArgumentException(String.format(
				"Invalid symbol reference: %s. Valid values are: [%s]", symb,
				validValues()));
	}

	private static String validValues() {
		if (_valid_values == null) {
			StringBuffer stb = new StringBuffer();
			for (CalendarField cf : CalendarField.values()) {
				stb.append(cf.symbol + ",");
			}
			_valid_values = stb.toString().substring(0, stb.length() - 1);
		}
		return _valid_values;
	}

}