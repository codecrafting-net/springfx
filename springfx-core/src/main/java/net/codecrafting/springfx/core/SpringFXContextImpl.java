/*
 * Copyright 2018 Lucas Lara Marotta
 * Copyright 2018-2019 The SpringFX Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.codecrafting.springfx.core;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import net.codecrafting.springfx.annotation.HeadlessApplication;


/**
 * Implementation class of {@link SpringFXContext} that is used for the initialization of the Spring Boot framework 
 * and as a context for the SpringFX. This is the default implementation used on {@link SpringFXLauncher}
 * 
 * @author Lucas Marotta
 * @see #getSpringContext()
 * @see #getApplication()
 * @see #getEnvironment()
 * @see #run(String[])
 * @see #isHeadless()
 */
public class SpringFXContextImpl implements SpringFXContext
{
	/**
	 * The Spring {@link ConfigurableApplicationContext}
	 */
	private ConfigurableApplicationContext springContext;	
	
	/**
	 * The user application
	 */
	private SpringFXApplication application;
	
	/**
	 * The user application class.
	 */
	private Class<? extends SpringFXApplication> appClass;
	private SpringApplicationBuilder springBuilder;
	
	
	/**
	 * Create a new {@link SpringFXContextImpl} instance.
	 * Here will be created a internal {@link SpringApplicationBuilder} to initialize 
	 * and configure a {@link ConfigurableApplicationContext}
	 * @param appClass the user application class
	 */
	public SpringFXContextImpl(Class<? extends SpringFXApplication> appClass)
	{
		if(appClass != null) {
			this.appClass = appClass;
			springBuilder = new SpringApplicationBuilder()
					.sources(appClass)
					.web(WebApplicationType.NONE)
					.headless(isHeadless());
		} else {
			throw new IllegalArgumentException("Application class must not be null");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConfigurableApplicationContext getSpringContext()
	{
		return springContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpringFXApplication getApplication()
	{
		return application;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment getEnvironment()
	{
		if(springContext != null) {
			return springContext.getEnvironment();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(String args[])
	{
		if(args != null) {
			springContext = springBuilder.run(args);
			application = springContext.getBean(appClass);	
		} else {
			throw new IllegalArgumentException("Args must not be null");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop()
	{
		if(springContext != null) {
			if(springContext.isActive() && springContext.isRunning())
				springContext.stop();	
		}
	}

	/**
	 * Check if the user application have {@link HeadlessApplication} annotation and its value
	 * @return the value of {@link HeadlessApplication} annotation if present.
	 * @defaultValue true
	 */
	public boolean isHeadless() 
	{
		HeadlessApplication headlessAnn = appClass.getAnnotation(HeadlessApplication.class);
		return (headlessAnn != null) ? headlessAnn.value() : true;
	}	
}
