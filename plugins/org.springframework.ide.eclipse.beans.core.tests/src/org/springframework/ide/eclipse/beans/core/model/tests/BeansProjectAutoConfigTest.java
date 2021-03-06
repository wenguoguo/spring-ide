/*******************************************************************************
 * Copyright (c) 2013 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.beans.core.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansModel;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansProject;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfigSet;
import org.springframework.ide.eclipse.beans.core.model.locate.BeansConfigLocatorDefinition;
import org.springframework.ide.eclipse.beans.core.model.locate.BeansConfigLocatorFactory;
import org.springsource.ide.eclipse.commons.tests.util.StsTestUtil;

/**
 * @author Martin Lippert
 * @since 3.5.0
 */
public class BeansProjectAutoConfigTest {

	private BeansModel model;
	private BeansModel realModel;

	@Before
	public void createProject() throws Exception {
		model = new BeansModel();
		realModel = (BeansModel) BeansCorePlugin.getModel();
		BeansCorePlugin.setModel(model);
	}

	@After
	public void deleteProject() throws Exception {
		BeansCorePlugin.setModel(realModel);
	}

	@Test
	public void testBeansProjectNoAutoConfig() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-config-tests", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);
			
			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			assertEquals(0, beansProject.getConfigs().size());
			assertEquals(0, beansProject.getAutoConfigNames().size());
			assertEquals(0, beansProject.getAutoConfigSetNames().size());
		} finally {
			project.delete(true, null);
		}
	}

	@Test
	public void testBeansProjectOneManualConfigOneSpringBootAutoConfig() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-autoconfig-java-tests", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);

			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			Set<IBeansConfig> configs = beansProject.getConfigs();
			assertEquals(2, configs.size());

			boolean manualConfig = false;
			boolean autoConfig = false;

			for (IBeansConfig config : configs) {
				if (config.getType() == IBeansConfig.Type.MANUAL) {
					manualConfig = true;
				} else if (config.getType() == IBeansConfig.Type.AUTO_DETECTED) {
					autoConfig = true;
				}
			}

			assertTrue(manualConfig);
			assertTrue(autoConfig);

			Set<String> autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());

			Set<String> autoConfigSetNames = beansProject.getAutoConfigSetNames();
			assertEquals(0, autoConfigSetNames.size());
		} finally {
			project.delete(true, null);
		}

	}

	@Test
	public void testBeansProjectOneXMLAutoConfig() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-autoconfig-xml-tests", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);

			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			Set<IBeansConfig> configs = beansProject.getConfigs();
			assertEquals(1, configs.size());

			boolean manualConfig = false;
			boolean autoConfig = false;

			for (IBeansConfig config : configs) {
				if (config.getType() == IBeansConfig.Type.MANUAL) {
					manualConfig = true;
				} else if (config.getType() == IBeansConfig.Type.AUTO_DETECTED) {
					autoConfig = true;
				}
			}

			assertFalse(manualConfig);
			assertTrue(autoConfig);

			Set<String> autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());

			Set<String> autoConfigSetNames = beansProject.getAutoConfigSetNames();
			assertEquals(0, autoConfigSetNames.size());
		} finally {
			project.delete(true, null);
		}
	}

	@Test
	public void testBeansProjectDisableEnableXMLAutoConfigurator() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-autoconfig-xml-tests", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);

			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			Set<IBeansConfig> configs = beansProject.getConfigs();
			assertEquals(1, configs.size());

			Set<String> autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());

			List<BeansConfigLocatorDefinition> definitions = BeansConfigLocatorFactory.getBeansConfigLocatorDefinitions();
			BeansConfigLocatorDefinition webAppAutoConfigurator = null;
			for (BeansConfigLocatorDefinition definition : definitions) {
				if (definition.getId().equals("webAppBeansConfigLocator")) {
					webAppAutoConfigurator = definition;
				}
			}

			assertNotNull(webAppAutoConfigurator);

			// disable the configurator
			webAppAutoConfigurator.setEnabled(false, project);

			beansProject.getConfigs();
			jobMan.join("populateAutoConfigsJobFamily", null);

			configs = beansProject.getConfigs();
			assertEquals(0, configs.size());

			autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(0, autoConfigs.size());

			// enable the configurator again
			webAppAutoConfigurator.setEnabled(true, project);

			beansProject.getConfigs();
			jobMan.join("populateAutoConfigsJobFamily", null);

			configs = beansProject.getConfigs();
			assertEquals(1, configs.size());

			autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());
		} finally {
			project.delete(true, null);
		}
	}
	
	@Test
	public void testBeansProjectOneXMLAutoConfigIncludedInConfigSet() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-autoconfig-xml-in-config-set-tests", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);

			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			Set<IBeansConfig> configs = beansProject.getConfigs();
			assertEquals(1, configs.size());

			Set<String> autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());

			Set<String> autoConfigSetNames = beansProject.getAutoConfigSetNames();
			assertEquals(0, autoConfigSetNames.size());
			
			IBeansConfigSet set = beansProject.getConfigSet("test-set");
			Set<String> configNamesInSet = set.getConfigNames();
			assertEquals(1, configNamesInSet.size());
			assertEquals("WebContent/WEB-INF/spring/root-context.xml", configNamesInSet.iterator().next());
		} finally {
			project.delete(true, null);
		}
	}

	@Test
	public void testBeansProjectOneXMLAutoConfigPersisted() throws Exception {
		IProject project = StsTestUtil.createPredefinedProject("beans-autoconfig-xml-tests-persisted", "org.springframework.ide.eclipse.beans.core.tests");

		try {
			BeansProject beansProject = new BeansProject(model, project);
			model.addProject(beansProject);
			
			beansProject.getConfigs();
			IJobManager jobMan = Job.getJobManager();
			jobMan.join("populateAutoConfigsJobFamily", null);

			Set<IBeansConfig> configs = beansProject.getConfigs();
			assertEquals(1, configs.size());
			
			assertTrue(beansProject.isAutoConfigStatePersisted());

			boolean manualConfig = false;
			boolean autoConfig = false;

			for (IBeansConfig config : configs) {
				if (config.getType() == IBeansConfig.Type.MANUAL) {
					manualConfig = true;
				} else if (config.getType() == IBeansConfig.Type.AUTO_DETECTED) {
					autoConfig = true;
				}
			}

			assertFalse(manualConfig);
			assertTrue(autoConfig);

			Set<String> autoConfigs = beansProject.getAutoConfigNames();
			assertEquals(1, autoConfigs.size());

			Set<String> autoConfigSetNames = beansProject.getAutoConfigSetNames();
			assertEquals(0, autoConfigSetNames.size());
		} finally {
			project.delete(true, null);
		}
	}

}
