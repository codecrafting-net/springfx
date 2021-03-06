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
package net.codecrafting.springfx.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class StageContextTest 
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void swapContentBadArgument()
	{
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("viewController must not be null");
		StageContext context = new StageContext("test", "test title") {
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() {return null;}
		};
		context.swapContent(null);
	}
	
	@Test
	public void swapContentBadContent()
	{
		StageContext context = new StageContext("test", "test title") {
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() {return null;}
		};
		ViewContext viewController = Mockito.mock(ViewContext.class);
		Mockito.when(viewController.getMainNode()).thenReturn(null);
		
		try {
			context.swapContent(viewController);
			assertFalse("NullPointerException not thrown", true);
		} catch(Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
			assertEquals("StageContext mainNode and content must not be null", e.getMessage());
		}
		
		context = new StageContext("test", "test title") {
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() {return null;}
		};
		Mockito.reset(viewController);
		Mockito.when(viewController.getMainNode()).thenReturn(new AnchorPane());
		
		try {
			context.swapContent(viewController);
			assertFalse("NullPointerException not thrown", true);
		} catch(Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
			assertEquals("StageContext mainNode and content must not be null", e.getMessage());
		}
		
		context = new StageContext("test", "test title") {
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() {return new AnchorPane();}
		};
		Mockito.reset(viewController);
		Mockito.when(viewController.getMainNode()).thenReturn(null);
		
		try {
			context.swapContent(viewController);
			assertFalse("NullPointerException not thrown", true);
		} catch(Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
			assertEquals("StageContext mainNode and content must not be null", e.getMessage());
		}		
	}
	
	@Test
	public void swapContent()
	{
		StageContext context = new StageContext("test", "test title") {
			
			private AnchorPane pane;
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() 
			{
				if(pane == null) {
					pane = new AnchorPane();
					pane.setId("stage-main-node");	
				}
				return pane;
			}
		};
		ViewContext viewController = Mockito.mock(ViewContext.class);
		FlowPane pane = new FlowPane();
		pane.setId("view-main-node");
		Mockito.when(viewController.getMainNode()).thenReturn(pane);
		Mockito.when(viewController.isFitWidth()).thenReturn(true);
		Mockito.when(viewController.isFitHeight()).thenReturn(true);
		context.swapContent(viewController);
		assertNotNull("viewController main node not found", context.getMainNode().lookup("#view-main-node"));
		Mockito.verify(viewController, Mockito.times(1)).swapAnimation(ArgumentMatchers.any());
	}
	
	@Test
	public void swapContentWithFitOptions()
	{
		StageContext context = new StageContext("test", "test title") {
			
			private AnchorPane pane;
			
			@Override
			protected void onStart() {}
			
			@Override
			protected void onCreate() {}
			
			@Override
			public void setViewStageTitle(String title) {}
			
			@Override
			public AnchorPane getMainNode() 
			{
				if(pane == null) {
					pane = new AnchorPane();
					pane.setId("stage-main-node");	
				}
				return pane;
			}
		};
		ViewContext viewController = Mockito.mock(ViewContext.class);
		FlowPane pane = new FlowPane();
		pane.setVisible(false);
		pane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		pane.setPrefSize(250, 250);
		Mockito.when(viewController.getMainNode()).thenReturn(pane);
		
		Mockito.when(viewController.isFitWidth()).thenReturn(true);
		Mockito.when(viewController.isFitHeight()).thenReturn(true);
		context.swapContent(viewController);
		assertEquals(0.0, pane.getProperties().get("pane-top-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-left-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-bottom-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-right-anchor"));
		
		Mockito.reset(viewController);
		Mockito.when(viewController.getMainNode()).thenReturn(pane);
		Mockito.when(viewController.isFitWidth()).thenReturn(false);
		Mockito.when(viewController.isFitHeight()).thenReturn(true);
		pane.getProperties().put("pane-top-anchor", null);
		pane.getProperties().put("pane-left-anchor", null);
		pane.getProperties().put("pane-bottom-anchor", null);
		pane.getProperties().put("pane-right-anchor", null);
		context.swapContent(viewController);
		assertEquals(0.0, pane.getProperties().get("pane-top-anchor"));
		assertNull(pane.getProperties().get("pane-left-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-bottom-anchor"));
		assertNull(pane.getProperties().get("pane-right-anchor"));
		
		Mockito.reset(viewController);
		Mockito.when(viewController.getMainNode()).thenReturn(pane);
		Mockito.when(viewController.isFitWidth()).thenReturn(true);
		Mockito.when(viewController.isFitHeight()).thenReturn(false);
		pane.getProperties().put("pane-top-anchor", null);
		pane.getProperties().put("pane-left-anchor", null);
		pane.getProperties().put("pane-bottom-anchor", null);
		pane.getProperties().put("pane-right-anchor", null);
		context.swapContent(viewController);
		assertNull(pane.getProperties().get("pane-top-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-left-anchor"));
		assertNull(pane.getProperties().get("pane-bottom-anchor"));
		assertEquals(0.0, pane.getProperties().get("pane-right-anchor"));
		
		Mockito.reset(viewController);
		Mockito.when(viewController.getMainNode()).thenReturn(pane);
		Mockito.when(viewController.isFitWidth()).thenReturn(false);
		Mockito.when(viewController.isFitHeight()).thenReturn(false);
		pane.getProperties().put("pane-top-anchor", null);
		pane.getProperties().put("pane-left-anchor", null);
		pane.getProperties().put("pane-bottom-anchor", null);
		pane.getProperties().put("pane-right-anchor", null);
		context.swapContent(viewController);
		assertNull(pane.getProperties().get("pane-top-anchor"));
		assertNull(pane.getProperties().get("pane-left-anchor"));
		assertNull(pane.getProperties().get("pane-bottom-anchor"));
		assertNull(pane.getProperties().get("pane-right-anchor"));
	}
}
