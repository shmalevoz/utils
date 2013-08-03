/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сервисная обертка над java.util.Logging
 * @author shmalevoz
 */
public class Log {
	
	public static int LEVEL_DEFAULT = 2;
	public static final int LEVEL_CONFIG = 7;
	public static final int LEVEL_VERBOSE_DEFAULT = 3;
	public static final int LEVEL_WARNING = 2;
	
	/**
	 * Устанавливает уровень логгирования по-умолчанию
	 * @param level Новый уровень
	 */
	public static void setDefaultLevel(String level) {
		int l = LEVEL_DEFAULT;
		try {
			l = Integer.parseInt(level);
		} catch (NumberFormatException e) {
		}
		setDefaultLevel(l);
	}
	
	/**
	 * Устанавливает уровень логгирования по-умолчанию
	 * @param level Новый уровень
	 */
	public static void setDefaultLevel(int level) {
		LEVEL_DEFAULT = level;
	}
	
	/**
	 * Возвращает именованный поток лога
	 * @param name Имя потока
	 * @return Объект лога
	 */
	public static Logger getLogger(String name) {
		return getLogger(name, LEVEL_DEFAULT);
	}
	
	/**
	 * Возвращает именованный поток лога
	 * @param name Имя потока
	 * @param level Назначаемый уровень логгирования
	 * @return Объект лога
	 */
	public static Logger getLogger(String name, int level) {
		Logger log = Logger.getLogger(name);
		init(log, level);
		return log;
	}
	
	/**
	 * Инициализируем объект лога
	 * @param log Логгер
	 */
	public static void init(Logger log) {
		init(log, LEVEL_DEFAULT);
	}
	
	/**
	 * Инициализирует объект лога
	 * @param log Логгер
	 * @param level Уровень логгирования
	 */
	public static void init(Logger log, int level) {
		for (Handler handler:log.getParent().getHandlers()) {
			log.getParent().removeHandler(handler);
		}
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new ru.shmalevoz.utils.LogFormatter());
		log.addHandler(handler);
		setLevel(log, level);
	}
	
	/**
	 * Устанавливает логгеру уровень логгирования по-умолчанию
	 * @param log Логгер
	 */
	public static void setLevel(java.util.logging.Logger log) {
		setLevel(log, LEVEL_DEFAULT);
	}
	
	/**
	 * Устанавливает логгеру новый уровень логгирования
	 * @param log Логгер
	 * @param level Новый уровень
	 */
	public static void setLevel(java.util.logging.Logger log, String level) {
		int l = LEVEL_DEFAULT;
		try {
			l = Integer.parseInt(level);
		} catch (NumberFormatException e) {
			log.severe("Error set log level: " + e.getMessage());
			l = LEVEL_DEFAULT;
		}
		setLevel(log, l);
	}
	
	/**
	 * Устанавливает логгеру новый уровень логгирования
	 * @param log Логгер
	 * @param level Новый уровень
	 */
	public static void setLevel(java.util.logging.Logger log, int level) {
		int l = level;
		if (l < 0 || l > 8) {
			log.warning("Error set log level " + l + ". Present levels 0-8. Set log level to default value " + LEVEL_DEFAULT);
			l = LEVEL_DEFAULT;
		}
		
		switch (l) {
			case 0:
				log.setLevel(Level.OFF); break;
			case 1:
				log.setLevel(Level.SEVERE); break;
			case 2:
				log.setLevel(Level.WARNING); break;
			case 3:
				log.setLevel(Level.INFO); break;
			case 4:
				log.setLevel(Level.CONFIG); break;
			case 5:
				log.setLevel(Level.FINE); break;
			case 6:
				log.setLevel(Level.FINER); break;
			case 7:
				log.setLevel(Level.FINEST); break;
			case 8:
				log.setLevel(Level.ALL); break;
		}
		for (Handler h:log.getHandlers()) {
			h.setLevel(log.getLevel());
		}
		log.config("Set log level of " + log.getName() + " : " + log.getLevel().toString());
	}
}
