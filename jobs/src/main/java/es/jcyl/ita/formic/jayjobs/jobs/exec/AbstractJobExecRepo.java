package es.jcyl.ita.formic.jayjobs.jobs.exec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Log;

public abstract class AbstractJobExecRepo implements Serializable, JobExecRepo {
    protected static final Map<Long, List<String>> messages = new HashMap<>();

    protected void addMessage(Long execId, String msg) {
        List<String> msgList;
        if (messages.containsKey(execId)) {
            msgList = messages.get(execId);
        } else {
            msgList = new ArrayList<>();
            messages.put(execId, msgList);
        }
        Log.debug("Message added: "+msg);
        msgList.add(msg);
    }

    public List<String> getMessages(Long jobExecId) {
        List<String> jobMessages = messages.get(jobExecId);
        return jobMessages;
    }
}
