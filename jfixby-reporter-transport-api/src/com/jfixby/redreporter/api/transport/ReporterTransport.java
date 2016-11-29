
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.ComponentInstaller;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.analytics.Report;

public class ReporterTransport {

	static private ComponentInstaller<ReporterTransportComponent> componentInstaller = new ComponentInstaller<ReporterTransportComponent>(
		"ReporterTransport");

	public static final void installComponent (final ReporterTransportComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final ReporterTransportComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ReporterTransportComponent component () {
		return componentInstaller.getComponent();
	}

	public static boolean sendReport (final Report message) {
		return invoke().sendReport(message);
	}

	public static void pingServers () {
		invoke().pingServers();
	}

	public static InstallationID getInstallationID () {
		return invoke().getInstallationID();
	}

	public static boolean deleteInstallationID () {
		return invoke().deleteInstallationID();
	}

}
