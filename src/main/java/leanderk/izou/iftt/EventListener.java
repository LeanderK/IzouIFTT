package leanderk.izou.iftt;

import org.intellimate.izou.events.EventListenerModel;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.util.AddOnModule;

/**
 * @author LeanderK
 * @version 1.0
 */
public class EventListener extends AddOnModule implements EventListenerModel{
    public static final String ID = EventListener.class.getCanonicalName();

    public EventListener(Context context) {
        super(context, ID);
    }

    @Override
    public void eventFired(EventModel eventModel) {

    }
}
