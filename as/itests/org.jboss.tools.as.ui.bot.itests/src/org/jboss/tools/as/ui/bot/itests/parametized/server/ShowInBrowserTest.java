package org.jboss.tools.as.ui.bot.itests.parametized.server;

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
import org.jboss.tools.as.ui.bot.itests.AbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
@JBossServer(state=ServerRequirementState.RUNNING)
public class ShowInBrowserTest extends AbstractTest {
	
	@Test
	public void checkBrowserText() throws InterruptedException {
		ServersView2 sv = new ServersView2();
		sv.activate();
		Server server = sv.getServer("WildFly 24+ Server");
		server.select();
		
		Menu menu = new ContextMenu();
		menu.getItem("Show In", "Web Browser").select();
		
		Browser browser = new InternalBrowser();
		browser.setFocus();
		Thread.sleep(200);
		
		assertTrue(browser.getText().contains("WildFly")); 
	
	}
}
