package es.jcyl.ita.formic.jayjobs.jexl.helpers;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import es.jcyl.ita.formic.jayjobs.jexl.JexlExpressionException;

public class StringHelper {

	public boolean emptyStr(String str) {
		return StringUtils.isBlank(str);
	}

	public String escapeQuote(Object value) {
		String str = String.valueOf(value);
		return str.replace("\"", "\\\"");
	}

	public String decode(String str) {
		return decode(str, StandardCharsets.UTF_8.name());
	}

	public String decode(String str, String charset) {
		try {
			return java.net.URLDecoder.decode(str, charset);
		} catch (UnsupportedEncodingException e) {
			throw new JexlExpressionException("Error while decoding string: " + str);
		}
	}

	public String encode(String str) {
		return encode(str, StandardCharsets.UTF_8.name());
	}

	public String encode(String str, String charset) {
		try {
			return java.net.URLEncoder.encode(str, charset);
		} catch (UnsupportedEncodingException e) {
			throw new JexlExpressionException("Error while encoding string: " + str);
		}
	}
}
