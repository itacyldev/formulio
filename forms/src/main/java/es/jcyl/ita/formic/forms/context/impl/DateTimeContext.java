package es.jcyl.ita.formic.forms.context.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.AbstractMapContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextException;

/**
 * Context Wrapper to give access to date and time basic funcions using context notation (date.now,
 * date.now.1d, etc).
 *
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class DateTimeContext extends AbstractMapContext implements Context {

    private static final long serialVersionUID = -9146740176167308983L;

    private static Map<String, DateFormat> _formatters = new HashMap<String, DateFormat>();
    private Calendar calendar = Calendar.getInstance();

    private DateExpressionInterpreter dei = new DateExpressionInterpreter();

    public DateTimeContext() {
        super("date");
    }

    public DateTimeContext(String prefix) {
        super(prefix);
    }

    /**
     * The key is the date expression that the DateTimeContext must resolve
     */
    @Override
    public Object get(Object key) {
        return get((String) key);
    }

    @Override
    public Object get(String key) {
        this.calendar.setTime(new Date());
        List<CalendarOperation> lst = this.dei.interpret(key);
        int field;
        for (CalendarOperation oper : lst) {
            switch (oper.getType()) {
                case ADD:
                    field = CalendarField.getBySymbol(oper.getSymbol()).getCode();
                    this.calendar.add(field, oper.getOperation() * oper.getValue());
                    break;
                case EXTRACT:
                    field = CalendarField.getBySymbol(oper.getSymbol()).getCode();
                    return this.calendar.get(field);
                case FORMAT:
                    DateFormat df = getFormatter(oper.getFormat());
                    return df.format(this.calendar.getTime());
            }
        }
        return this.calendar.getTime();
    }

    private DateFormat getFormatter(String key) {
        String pattern = key.replace("now.", "");
        if (!_formatters.containsKey(pattern)) {
            DateFormat df = null;
            try {
                df = new SimpleDateFormat(pattern);

            } catch (Exception e) {
                throw new ContextException(String.format(
                        "There was and error while trying "
                                + "to create the Date formatter with the specified pattern: [%s]",
                        pattern));
            }
            _formatters.put(pattern, df);
        }
        return _formatters.get(pattern);
    }

}