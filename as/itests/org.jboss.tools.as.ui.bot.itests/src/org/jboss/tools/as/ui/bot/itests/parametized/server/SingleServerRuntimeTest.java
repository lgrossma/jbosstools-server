package org.jboss.tools.as.ui.bot.itests.parametized.server;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.jboss.ide.eclipse.as.reddeer.server.view.JBossServer;
import org.jboss.ide.eclipse.as.reddeer.server.view.JBossServerView;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.eclipse.jdt.ui.preferences.JREsPreferencePage;
import org.jboss.reddeer.eclipse.wst.server.ui.view.Server;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.as.ui.bot.itests.Activator;
import org.jboss.tools.as.ui.bot.itests.download.RuntimeDownloadTestUtility;
import org.jboss.tools.as.ui.bot.itests.reddeer.util.DeployJSPProjectTemplate;
import org.jboss.tools.as.ui.bot.itests.reddeer.util.DetectRuntimeTemplate;
import org.jboss.tools.as.ui.bot.itests.reddeer.util.DisableSecureStorageRequirement.DisableSecureStorage;
import org.jboss.tools.as.ui.bot.itests.reddeer.util.OperateServerTemplate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;



/**
 * This test is trying to optimize and clean up the huge number of tests that were here. 
 * 
 * Pre-reqs:
 *   1) -Djbosstools.test.jre.6={java6home}
 *   2) -Djbosstools.test.jre.7={java7home}
 *   3) -Djbosstools.test.jre.8={java8home}
 *   4) -Djboss.org.username={yourusername}
 *   5) -Djboss.org.password={hunter2}
 *   6) -Druntimes.suite.scope={smoke | latestMajors | allFree | all}
 *   
 *  Items 4 and 5 are optional and only necessary if testing runtimes that 
 *  require $0 subscription. 
 *  
 *  This test no longer needs to download any runtimes in advance. It will download them all via UI.
 *  
 *  There are 3 primary tests here:
 *    1) acquireAndDetect	(download the runtime via ui and make sure it appears)
 *    2) detect				(use the just-downloaded fs-location and use runtime detection)
 *    3) operate			(import via rt-detection and start/stop it)
 *    
 *  JRE requirement is no longer necessary. We simply make sure to add a JRE
 *  for java 6, 7, and 8 in advance, and check to make sure that the given server
 *  starts without the user needing to customize or change the jre at all. 
 *  
 *  Adding new runtimes to the test is as easy as modifying 
 *  ServerRuntimeUIConstants. You should:
 *    1) Declare a constant representing the UI string to download a given runtime
 *    2) Add that constant to the various arrays it should belong to (free, smoke, etc)
 *    3) Add a line to the initialize() method representing the various expected dl-rt values
 */


@RunWith(RedDeerSuite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)//first acquireAndDetect, then detect, then operate
@DisableSecureStorage
public class SingleServerRuntimeTest {

    @BeforeClass
    public static void addJREs() {
    	String jre6 =  System.getProperty("jbosstools.test.jre.6");
    	String jre7 =  System.getProperty("jbosstools.test.jre.7");
    	String jre8 =  System.getProperty("jbosstools.test.jre.8");
    	if( jre6 == null || jre6.isEmpty() || !(new File(jre6)).exists()) {
    		throw new RuntimeException("Expected requirement JRE-6 is not set, is empty, or does not exist. Please set via system property -Djbosstools.test.jre.6");
    	}
    	if( jre7 == null || jre7.isEmpty() || !(new File(jre7)).exists()) {
    		throw new RuntimeException("Expected requirement JRE-7 is not set, is empty, or does not exist. Please set via system property -Djbosstools.test.jre.7");
    	}
    	if( jre8 == null || jre8.isEmpty() || !(new File(jre8)).exists()) {
    		throw new RuntimeException("Expected requirement JRE-8 is not set, is empty, or does not exist. Please set via system property -Djbosstools.test.jre.8");
    	}
    	addJRE("JRE6",jre6);
    	addJRE("JRE7",jre7);
    	addJRE("JRE8",jre8);
    }
    
    @AfterClass
    public static void removeJREs() {
    	removeJRE("JRE6");
    	removeJRE("JRE7");
    	removeJRE("JRE8");
    }
    
    public static void removeJRE(String name) {
		JREsPreferencePage page = new JREsPreferencePage();
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		dialog.open();
		dialog.select(page);
		page.deleteJRE(name);
		dialog.ok();
    }
    
    private static void addJRE(String name, String path) {
		JREsPreferencePage page = new JREsPreferencePage();
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		dialog.open();
		dialog.select(page);
		page.addJRE(path, name);
		dialog.ok();
    }



    private String location;
    private String serverName;

    public SingleServerRuntimeTest() {
    	location = System.getProperty("jbosstools.test.single.runtime.location");
    }
    
    protected File getDownloadPath() {
    	return new File(location);
    }
    
    @Test
    public void detect(){
    	DetectRuntimeTemplate.detectRuntime(getDownloadPath().getAbsolutePath());
    	DetectRuntimeTemplate.removePath(getDownloadPath().getAbsolutePath());
    	new JBossServerView().open();
    	List<Server> all = new JBossServerView().getServers();
    	assertThat(all.size(), is(1));
    	serverName = all.get(0).getLabel().getName();
    }
    

    

    
    
    @Test
    public void operate(){
    	DetectRuntimeTemplate.detectRuntime(getDownloadPath().getAbsolutePath());
    	DetectRuntimeTemplate.removePath(getDownloadPath().getAbsolutePath());
    	new JBossServerView().open();
    	List<Server> all = new JBossServerView().getServers();
    	assertThat(all.size(), is(1));
    	serverName = all.get(0).getLabel().getName();
    	OperateServerTemplate operate = new OperateServerTemplate(serverName);
    	operate.setUp();
    	try {
    		operate.operateServer();
    	} finally {
    		operate.cleanServerAndConsoleView();
    	}
    }

    @Test
    public void operateDeploy(){
    	// Right now there's only 2 versions of each, and one is only for very old app servers
    	// So we'll use the JB7+ / WF / EAP5+ strings
		String undepString = ServerRuntimeUIConstants.getUndeployString(ServerRuntimeUIConstants.JBEAP_700, "jsp-project", ".war");
		String depString = ServerRuntimeUIConstants.getDeployString(ServerRuntimeUIConstants.JBEAP_700, "jsp-project", ".war");
    	
    	
    	DetectRuntimeTemplate.detectRuntime(getDownloadPath().getAbsolutePath());
    	DetectRuntimeTemplate.removePath(getDownloadPath().getAbsolutePath());
    	new JBossServerView().open();
    	List<Server> all = new JBossServerView().getServers();
    	assertThat(all.size(), is(1));
    	serverName = all.get(0).getLabel().getName();
    	OperateServerTemplate operate = new OperateServerTemplate(serverName);
    	operate.setUp();
    	try {
    		operate.startServerSafe();
    		DeployJSPProjectTemplate djsppt = new DeployJSPProjectTemplate();
    		djsppt.clearConsole();
    		JBossServer jbs = djsppt.getServer(serverName);
    		djsppt.importProject("jsp-project", "projects/jsp-project.zip", serverName + " Runtime");
    		
    		djsppt.deployProject("jsp-project", serverName, depString);
    		
    		// Now try a hot-deploy
    		djsppt.hotDeployment("jsp-project");
    		
    		// Now try to undeploy
    		djsppt.undeployProject(serverName, "jsp-project", undepString);
    		
    	} finally {
    		// Cleanup everything
    		operate.stopAndDeleteServer();
    		operate.cleanServerAndConsoleView();
    	}
    }

    

    @After
    public void postTest() {
    	Display.syncExec(new Runnable() {
			@Override
			public void run() {
		    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
			}
		});
    	new RuntimeDownloadTestUtility(getDownloadPath()).clean(false);
    }
    
    @AfterClass
    public static void postClass() {
    	new RuntimeDownloadTestUtility(Activator.getStateFolder().toFile()).clean(true);
    }
        

}
