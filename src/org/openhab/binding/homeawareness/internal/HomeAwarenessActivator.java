package org.openhab.binding.homeawareness.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeAwarenessActivator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(HomeAwarenessActivator.class);
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		HomeAwarenessActivator.context = bundleContext;

		logger.debug("Home Awareness activator started");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		HomeAwarenessActivator.context = null;

		logger.debug("Home Awareness activator stopped");
	}

}
