/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.shmalevoz.utils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Сервисные методы работы с файловой системой
 * @author shmalevoz
 */
public class IO {
	
	private static final Logger log = Log.getLogger(IO.class.getName());
	
	private static File getJarFile(Object o) {
		return new File(o.getClass().getProtectionDomain().getCodeSource().getLocation().toString());
	}
	
	/**
	 * Определяет каталог jar файла, содержащий переданный объект
	 * @param o исследуемый объект
	 * @return каталог jar файла или null в случае если не удалось определить
	 */
	public static String getJarPath(Object o) {
		
		String retval = null;
		try {
			File f = getJarFile(o);
			retval = f.getParent() + File.separator;
			String rep = "file:";
			if (System.getProperty("os.name").startsWith("Windows")) rep += File.separator;
			retval = retval.replace(rep, "");
			
		} catch (Exception e) {
		}
		
		return retval;
	}
	
	/**
	 * Возвращает имя jar файла, содержащего переданный объект
	 * @param o - Исследуемый объект
	 * @return Имя jar файла
	 */
	public static String getJarName(Object o) {
		return getJarFile(o).getName();
	}
	
	/**
	 * Возвращает поток вывода с установленной кодовой страницей
	 * @param o Исходный поток вывода
	 * @param codepage Кодовая страница
	 * @return Поток вывода с установленной кодовой страницей
	 * @throws UnsupportedEncodingException 
	 */
	private static PrintStream getPrintStream(OutputStream o, String codepage) throws UnsupportedEncodingException {
		return new PrintStream(o, true, codepage);
	}
	
	/**
	 * Устанавливает кодовую страницу стандартных потоков stdout и stderr в зависимости от текущей системы
	 */
	public static void setStdCodepage() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			try {
				System.setOut(getPrintStream(System.out, "Cp866"));
				System.setErr(getPrintStream(System.err, "Cp866"));
			} catch (UnsupportedEncodingException ex) {
				log.severe(ex.getMessage());
			}
		}
	}

}
