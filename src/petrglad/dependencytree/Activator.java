package petrglad.dependencytree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle.
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "petrglad.dependencytree";

    /**
     * The shared instance
     */
    private static Activator plugin;

    public Activator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
    }

    /**
     * @return The shared instance of the plugin.
     */
    public static Activator getDefault() {
	return plugin;
    }

    /**
     * @param path
     *            The path
     * @return Image descriptor for the image file at the given plug-in relative
     *         path
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
