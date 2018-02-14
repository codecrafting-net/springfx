package net.codecrafting.springfx.utils;

import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import net.codecrafting.springfx.exceptions.DialogBuilderInitializationException;

public class DialogBuilder 
{
	private static Properties dialogProps;
	private static ObservableList<String> cachedStylesheets;
	private static Image cachedIcon;
	public static final Properties DEFAULT_PROPS = new Properties();
	private static final Log LOGGER = LogFactory.getLog(DialogBuilder.class);
	
	public static void init()
	{
		if(!DEFAULT_PROPS.containsKey("springfx.dialog.info.title")) {
			DEFAULT_PROPS.put("springfx.dialog.info.title", "INFO");
			DEFAULT_PROPS.put("springfx.dialog.warn.title", "WARN");
			DEFAULT_PROPS.put("springfx.dialog.error.title", "ERROR");
			DEFAULT_PROPS.put("springfx.dialog.info.header", "NOTICE");
			DEFAULT_PROPS.put("springfx.dialog.warn.header", "ATTENTION - WARNING");
			DEFAULT_PROPS.put("springfx.dialog.error.header", "ATTENTION - ERROR");
			DEFAULT_PROPS.put("springfx.dialog.icon", "");
			DEFAULT_PROPS.put("springfx.dialog.stylesheets", "");
		}
		dialogProps = new Properties();
		dialogProps.putAll(DEFAULT_PROPS);
		cachedStylesheets = FXCollections.observableArrayList();
		cachedIcon = null;
	}
	
	public static void setProperties(Properties props)
	{
		if(props != null && !props.isEmpty()) {
			dialogProps.putAll(props);
			if(!props.isEmpty()) {
				try {
					if(!dialogProps.getProperty("springfx.dialog.icon").isEmpty()) {
						cachedIcon = new Image(dialogProps.getProperty("springfx.dialog.icon")); 	
					}
					if(!dialogProps.getProperty("springfx.dialog.stylesheets").isEmpty()) {
						addStylesheets(dialogProps.getProperty("springfx.dialog.stylesheets"));
					}
				} catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}	
			}
		}		
	}
	
	public static Properties getProperties()
	{
		Properties props = new Properties();
		if(dialogProps != null && !dialogProps.isEmpty()) {
			props.putAll(dialogProps);
		} else {
			props.putAll(DEFAULT_PROPS);
		}
		return props;
	}
	
	public static void addStylesheets(String stylePath)
	{
		URL styles = null;
		try {
			styles = DialogBuilder.class.getResource(stylePath);
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(styles != null) {
			cachedStylesheets.add(styles.toExternalForm());
		}
	}
	
	public static Dialog build(AlertType type)
	{
		if(isInitialized()) {
			Dialog dialog = new Dialog(type);
			if(cachedIcon != null) dialog.setIcon(cachedIcon);
			if(cachedStylesheets != null) dialog.setStylesheets(cachedStylesheets);
			return dialog;
		} else {
			throw new DialogBuilderInitializationException();
		}
	}	
	
	public static Optional<ButtonType> showInfo(String content)
	{
		return build(AlertType.INFORMATION)
				.setTitle(dialogProps.getProperty("springfx.dialog.info.title"))
				.setHeader(dialogProps.getProperty("springfx.dialog.info.header"))
				.setContent(content).show();
	}
	
	public static Optional<ButtonType> showWarn(String content)
	{
		return build(AlertType.WARNING)
				.setTitle(dialogProps.getProperty("springfx.dialog.warn.title"))
				.setHeader(dialogProps.getProperty("springfx.dialog.warn.header"))
				.setContent(content).show();
	}

	public static Optional<ButtonType> showError(String content)
	{
		return build(AlertType.ERROR)
				.setTitle(dialogProps.getProperty("springfx.dialog.error.title"))
				.setHeader(dialogProps.getProperty("springfx.dialog.error.header"))
				.setContent(content).show();
	}	

	public static boolean isInitialized()
	{
		return (dialogProps != null);
	}
}