package es.jcyl.ita.formic.jayjobs.jexl.helpers;

/**
 * Utilidades para evitar boilerplate code en la definici�n de jobs json
 * 
 * @author ita-riobrigu
 *
 */
public class SyntaxHelper {

	public Object nvl(Object value, Object defaultValue) {
		return (value != null) ? value : defaultValue;
	}

	/**
	 * devuelve el primero valor no nulo
	 * 
	 * @param values
	 * @return
	 */
	public Object nvl(Object... values) {
		for (Object value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

}
