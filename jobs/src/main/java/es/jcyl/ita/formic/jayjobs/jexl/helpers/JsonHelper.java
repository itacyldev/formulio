package es.jcyl.ita.formic.jayjobs.jexl.helpers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
	public static final ObjectMapper mapper = new ObjectMapper();

	public String toStr(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public JsonNode toJson(String str) throws IOException {
		return mapper.readTree(str);
	}

}
