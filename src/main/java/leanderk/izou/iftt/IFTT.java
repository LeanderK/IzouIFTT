package leanderk.izou.iftt;

import org.intellimate.izou.activator.ActivatorModel;
import org.intellimate.izou.events.EventsControllerModel;
import org.intellimate.izou.output.OutputExtensionModel;
import org.intellimate.izou.output.OutputPluginModel;
import org.intellimate.izou.sdk.addon.AddOn;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;

/**
 * the base-addon class
 * @author LeanderK
 * @version 1.0
 */
public class IFTT extends AddOn {
    public static final String ID = IFTT.class.getCanonicalName();

    public IFTT() {
        super(ID);
    }

    @Override
    public void prepare() {

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
