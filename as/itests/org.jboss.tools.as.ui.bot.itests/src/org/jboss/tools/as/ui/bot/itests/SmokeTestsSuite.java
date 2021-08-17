package org.jboss.tools.as.ui.bot.itests;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.as.ui.bot.itests.parametized.server.ShowInBrowserTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(RedDeerSuite.class)
@Suite.SuiteClasses({
	ShowInBrowserTest.class
})
public class SmokeTestsSuite {

	
}
