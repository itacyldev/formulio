package es.jcyl.ita.formic.forms.context.impl;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Interprets a codified sequence of date expression in the form:
 * 
 * now.operation.operation.operation.[format_]
 * 
 * Operation is an operation to apply to the previous result, supported
 * operations:
 * <ul>
 * <li>Add/substract: [-|+][integer][reference symbol: d|h|y|Y|...] <br/>
 * Example: +1d.+1w add one day and one week to the current date.</li>
 * 
 * <li>Format: define a format for current date object, any format supported by
 * SimpleDateFormat is supported. The format must be preceed by the prefix
 * "format_". <br/>
 * Example: format_yyyyMMdd/HH:mm</li>
 * <li>Extract information: get a field from the current date object: day,
 * month, year, ... Just need to specify the proper symbol:<br/>
 * Example: now.d --> day, now.y --> year,</li>
 * </ul>
 * 
 * The add/substract operations can be concatenated, and the resulting object
 * will be a Date object. At the end, a format or extract info operation can be
 * defined to transform the Date.<br/>
 * Example: now.+1d.format_YYYY/mm/dd --> format next day, now.-1m.m --> last
 * month.
 * 
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class DateExpressionInterpreter {

	private static final String DELIM = ".";
	private static final String ERROR_MSG = "The current date operation has an invalid format: "
			+ "[+|-[integer][<symbol>] is expected, found: [%s].";

	public List<CalendarOperation> interpret(String expression) {
		List<CalendarOperation> lst = new ArrayList<CalendarOperation>();

		StringTokenizer stk = new StringTokenizer(expression, DELIM);

		while (stk.hasMoreElements()) {
			String token = stk.nextToken();
			if (token.equalsIgnoreCase("now")) {
				// base reference date object, discard it
				continue;
			}
			// TODO: if this grows, apply polymorphism patter with
			// support(?) method
			if (token.startsWith("+") || token.startsWith("-")) {
				lst.add(parseAddOperation(token));
			} else if (token.startsWith("format_")) {
				lst.add(parseFormatOperation(token));
			} else {
				// try extraction op
				lst.add(parseExtractOperation(token));
			}
		}

		return lst;
	}

	private CalendarOperation parseExtractOperation(String token) {
		if (token.length() != 1) {
			throw new IllegalArgumentException(String
					.format(ERROR_MSG + " Invalid scale reference.", token));
		}
		return CalendarOperation.extract(token);
	}

	public CalendarOperation parseFormatOperation(String token) {
		// remote prefix "format_"
		return CalendarOperation.format(token.replace("format_", ""));
	}

	public CalendarOperation parseAddOperation(String token) {
		// validate
		if (token.length() < 3) {
			throw new IllegalArgumentException(String.format(ERROR_MSG, token));
		}
		// extract operation type, quantity and scale
		int addOperation = 0;
		if (token.charAt(0) == '+') {
			addOperation = 1;
		} else if (token.charAt(0) == '-') {
			addOperation = -1;
		} else {
			throw new IllegalArgumentException(
					String.format(ERROR_MSG + " Invalid operation sign [%s].",
							token, token.charAt(0)));
		}
		// get last character
		char symb = token.charAt(token.length() - 1);
		CalendarField f = CalendarField.getBySymbol(String.valueOf(symb));

		String valueStr = token.substring(1, token.length() - 1);
		if (!StringUtils.isNumeric(valueStr)) {
			throw new IllegalArgumentException(String.format(
					ERROR_MSG + " Invalid Integer [%s].", token, valueStr));
		}
		return CalendarOperation.addOpr(addOperation, Integer.valueOf(valueStr),
				String.valueOf(symb));
	}

}
