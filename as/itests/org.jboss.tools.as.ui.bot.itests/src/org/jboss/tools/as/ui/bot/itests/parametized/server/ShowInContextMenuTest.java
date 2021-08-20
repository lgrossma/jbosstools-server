 /*******************************************************************************
 * Copyright (c) 2007-2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v 1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.as.ui.bot.itests.parametized.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.Server;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.server.ServerRequirementState;
import org.eclipse.reddeer.swt.api.Browser;
import org.eclipse.reddeer.swt.api.Menu;
import org.eclipse.reddeer.swt.impl.browser.InternalBrowser;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.ide.eclipse.as.reddeer.server.requirement.ServerRequirement.JBossServer;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for testing options in Show In context menu
 * 
 * @author Lukas Grossmann
 * 
 * TODO: Add more tests for Show In context menu options
 */
@RunWith(RedDeerSuite.class)
@JBossServer(state=ServerRequirementState.PRESENT)
public class ShowInContextMenuTest {
	
	private final ServersView2 sv = new ServersView2();
	private final Menu menu = new ContextMenu();
	private final Server server = sv.getServer("WildFly 24+ Server");
	
	@Test
	public void ShowInWebBrowserIsDisabled(){
		sv.activate();
		
		server.select();
		
		if(server.getLabel().getState().isRunningState()) {
			server.stop();
		}
		
		assertFalse("Web Browser option in Show In context menu is active even though server is stopped",
				menu.getItem("Show In", "Web Browser").isEnabled());
	}
	
	@Test
	public void ShowInWebBrowserIsEnabled() {
		sv.activate();
		
		server.select();
		
		if(!server.getLabel().getState().isRunningState()) {
			server.start();
		}
		
		assertTrue("Web Browser option in Show In context menu is inactive even though server is running",
				menu.getItem("Show In", "Web Browser").isEnabled());
	}
	@Test
	public void BrowserContainsCorrectContent() throws InterruptedException {
		sv.activate();
		
		server.select();
		
		if(!server.getLabel().getState().isRunningState()) {
			server.start();
		}
		
		menu.getItem("Show In", "Web Browser").select();
		
		Browser browser = new InternalBrowser();
		browser.setFocus();
		Thread.sleep(200);
		
		assertTrue("Web browser does not show right content",browser.getText().contains("WildFly")); 
	}
}
