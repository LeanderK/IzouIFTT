package leanderk.izou.iftt;

import leanderk.izou.iftt.actions.ActionFlow;
import leanderk.izou.iftt.actions.PresenceInfo;
import org.intellimate.izou.activator.ActivatorModel;
import org.intellimate.izou.events.EventsControllerModel;
import org.intellimate.izou.output.OutputExtensionModel;
import org.intellimate.izou.output.OutputPluginModel;
import org.intellimate.izou.sdk.addon.AddOn;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;
import ro.fortsoft.pf4j.Extension;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * the base-addon class
 * @author LeanderK
 * @version 1.0
 */
@Extension
public class IFTT extends AddOn {
    public static final String ID = IFTT.class.getCanonicalName();
    private Parser parser;
    //used for uniwue ID's
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private PresenceInfo presenceInfo;
    private List<ActionFlow> actionFlows;

    private static PrintWriter printWriter = null;

    public IFTT() {
        super(ID);
    }

    @Override
    public void prepare() {
        presenceInfo = new PresenceInfo(getContext());
        Parser parser = new Parser(getContext());
        actionFlows = parser.parseFile(getContext().getPropertiesAssistant().getPropertiesFile(), presenceInfo, atomicInteger);
        //presenceInfo.test();
        getContext().getPropertiesAssistant().registerUpdateListener(propertiesAssistant -> {
            synchronized (this) {
                actionFlows.forEach(ActionFlow::unregister);
                actionFlows = parser.parseFile(propertiesAssistant.getPropertiesFile(), presenceInfo, atomicInteger);
            }
        });
        File file = new File(getContext().getFiles().getResourceLocation().getPath() + File.separator + "IFTT.log");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void write(String text) {
        if (printWriter != null) {
            printWriter.println(text);
            printWriter.flush();
        }
    }

    @Override
    public ActivatorModel[] registerActivator() {
        return new ActivatorModel[0];
    }

    @Override
    public ContentGenerator[] registerContentGenerator() {
        return new ContentGenerator[0];
    }

    @Override
    public EventsControllerModel[] registerEventController() {
        return new EventsControllerModel[0];
    }

    @Override
    public OutputPluginModel[] registerOutputPlugin() {
        return new OutputPluginModel[0];
    }

    @Override
    public OutputExtensionModel[] registerOutputExtension() {
        return new OutputExtensionModel[0];
    }
}
