package es.jcyl.ita.formic.core.context.impl;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/**
 *
 * Data structure to store the information of a date operation (add/substract, access to date field,
 * ..) obtained by the DateExpressionInterpreter during the parsing process.
 *
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */


public class CalendarOperation {

	public enum OperType {
		ADD,
		/** Add, substract time range to current date object */
		FORMAT,
		/** Format date object */
		EXTRACT /** Extract value from date object */
	};

	private OperType type;
	private int operation; // 0, nothing, 1 add, -1 substract
	private int value;
	private String symbol;
	private String format;

	public CalendarOperation(OperType type) {
		this.type = type;
	}

	public CalendarOperation(OperType type, int operation, int value,
			String symbol) {
		this.type = type;
		this.operation = operation;
		this.value = value;
		this.symbol = symbol;
	}

	public static CalendarOperation extract(String symbol) {
		CalendarOperation co = new CalendarOperation(OperType.EXTRACT);
		co.setSymbol(symbol);
		return co;
	}

	public static CalendarOperation addOpr(int operation, int value,
			String scale) {
		return new CalendarOperation(OperType.ADD, operation, value, scale);
	}

	public static CalendarOperation format(String format) {
		CalendarOperation co = new CalendarOperation(OperType.FORMAT);
		co.setFormat(format);
		return co;
	}

	public int getOperation() {
		return this.operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public OperType getType() {
		return this.type;
	}

	public void setType(OperType type) {
		this.type = type;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		switch (this.type) {
		case ADD:
			return String.format("%s%s%s", (this.operation > 0) ? "+" : "-",
					this.value, this.symbol);
		case EXTRACT:
			return this.symbol;
		case FORMAT:
			return "format_" + this.format;
		}
		return null;
	}

}
