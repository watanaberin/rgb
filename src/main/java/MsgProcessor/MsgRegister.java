package MsgProcessor;

import java.util.HashMap;
import java.util.Map;

public class MsgRegister {
    private static final Map<Integer, Class<?>> msgById = new HashMap<>();
    private static final Map<Class<?>, Integer> idByMsg = new HashMap<>();



    private static void register(int id, Class<?> msgClass) {
        msgById.put(id, msgClass);
        idByMsg.put(msgClass, id);
    }

    public static Class<?> getMsgClass(int id){
        return msgById.get(id);
    }
    public static int getMsgId(Class<?> msgClass){
        return idByMsg.get(msgClass);
    }
    public static int getMsgId(Object msg) {
        return getMsgId(msg);
    }
}
