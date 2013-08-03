/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.shmalevoz.utils;

import java.util.*;
import java.util.logging.Logger;

/**
 * Разбор и дальнейшая выборка опций командной строки
 * @author shmalevoz
 */
public class GetOpt {
	
	private static final Logger log = Log.getLogger(GetOpt.class.getName());
	
	/**
	 * Тип аргумента опции - не требует значения
	 */
	public static final int NO_ARGUMENT = 0;
	/**
	 * Тип аргумента опции - опциональное значение
	 */
	public static final int OPTIONAL_ARGUMENT = 1;
	/**
	 * Тип аргумента опции - обязательное значение
	 */
	public static final int REQUIRED_ARGUMENT = 2; 
	/**
	 * Тип опции - обязательная
	 */
	public static final boolean REQUIRED_OPTION = true;
	/**
	 * Тип опции - необязательная
	 */
	public static final boolean OPTIONAL_OPTION = false;
	
	/**
	 * Описание параметра
	 */
	protected class Option implements Comparable<Option> {
		public String name;
		public boolean request;
		public int type;
		public String description;
		public String long_name;
		public boolean present;
		public String value;
		
		/**
		 * Конструктор
		 * @param arg0 Краткое имя параметра
		 * @param arg1 Обязательный параметр
		 * @param arg2 Тип аргумента
		 * @param arg3 Описание параметра
		 * @param arg4 Длинное имя параметра
		 */
		public Option(String arg0, boolean arg1, int arg2, String arg3, String arg4) {
			this.name = arg0;
			this.request = arg1;
			this.type = arg2;
			this.description = arg3;
			this.long_name = arg4;
			this.present = false;
			this.value = "";
		}

		public int compareTo(Option o) {
			return (request && !o.request ? -1 : (!request && o.request ? 1 : 0));
		}
	}
	
	protected int _optind;
	protected List<Option> _options;
	protected String _error_string;
	protected String _progname;
	protected String[] _no_opt_args;
	
	/**
	 * Конструктор
	 * @param progname Имя программы
	 */
	public GetOpt(String progname) {
		_options = new ArrayList<Option>();
		_optind = -1;
		_progname = progname;
		_error_string = "Arguments no parsed";
		_no_opt_args = new String[0];
		log.config("Create GetOpt to " + progname);
	}
	
	/**
	 * Конструктор
	 */
	public GetOpt() {
		this("Jar packet");
	}
	
	/**
	 * Добавляет описание параметра
	 * @param name Имя
	 * @param request Обязательный
	 * @param type Тип аргумента
	 */
	public void addSpec(String name, boolean request, int type) {
		this.addSpec(name, request, type, "", "");
	}
	
	/**
	 * Добавляет описание параметра
	 * @param name Имя
	 * @param request Обязательный
	 * @param type Тип аргумента
	 * @param description Описание
	 */
	public void addSpec(String name, boolean request, int type, String description) {
		this.addSpec(name, request, type, description, "");
	}
	
	/**
	 * Добавляет описание параметра
	 * @param name Имя
	 * @param request Обязательный
	 * @param type Тип аргумента
	 * @param description Описание
	 * @param long_name Длинное имя
	 */
	public void addSpec(String name, boolean request, int type, String description, String long_name) {
		
		// проверим аргументы
		String err = "";
		if (type != NO_ARGUMENT && type != OPTIONAL_ARGUMENT && type != REQUIRED_ARGUMENT) {
			err = "Illegal argmunt value type " + type + " at option " + name;
		}
		if (name.length() != 1) {
			err = "Illegal option name " + name + " must be 1 chracter";
		}
		if (!err.isEmpty()) {
			log.warning(err);
			throw new IllegalArgumentException(err);
		}
		log.config("Add opt spec \n\tName " + name + "\n\tRequest " + request + "\n\tType " + type + "\n\tDesc " + description + "\n\tLong name " + long_name);
		_options.add(new Option(name, request, type, description, long_name));
	}

	/**
	 * Возвращает количество опций с длинными именами
	 * @return
	 */
	private int getLongNamesCount() {
		
		int retval = 0;
		
		int args_count = _options.size();
		for (int i = 0; i < args_count; i++) {
			Option opt = (Option) _options.get(i);
			if (!opt.long_name.isEmpty()) retval++;
		}
		
		return retval;
	}
	
	/**
	 * Ищет параметр по имени/длинному имени
	 * @param name Искомое
	 * @return Индекс параметра
	 */
	private int getOptionIndex(String name) {
		
		int retval = -1;
		int count = _options.size();
		boolean is_short_name = (name.length() == 1);
		
		for (int i = 0; i < count; i++) {
			Option opt = (Option) _options.get(i);
			
			if (is_short_name) {
				if (opt.name.equals(name)) {
					retval = i;
				}
			} else if (!opt.long_name.isEmpty()) {
				if (opt.long_name.equals(name)) {
					retval = i;
				}
			}
			
			if (retval != -1) break;
		}
		
		return retval;
	}
	
	/**
	 * Обновляет значение опции по имени, либо длинному имени
	 * @param name Имя
	 * @param long_name Длиное имя
	 * @param value Значение
	 */
	private void updateOption(int index, String value) {
		
		Option opt = _options.get(index);
		opt.value = value;
		opt.present = true;
		
		/**
		int index = getOptionIndex(name);
		if (index != -1) {
			Option opt = (Option) _options.get(index);
			opt.value = value;
			opt.present = true;
			_options.set(index, opt);
		}
		return index != -1;
		*/
	}
	
	/**
	 * Проверка корректности опций
	 */
	private void checkOptions() {
		
		int count = _options.size();
		String opt_name;
		
		for (int i = 0; i < count; i++) {
			Option opt = (Option) _options.get(i);
			
			opt_name = "-" + opt.name;
			if (!opt.long_name.isEmpty()) opt_name += " (--" + opt.long_name + ")";
			
			if (opt.request && !opt.present) {
				_error_string += "No present required option " + opt_name + "\n";
			} else if (opt.present && opt.type == REQUIRED_ARGUMENT && opt.value.isEmpty()) {
				_error_string += "No present required arg at option " + opt_name + "\n";
			}
		}
	}
	
	/**
	 * Анализ командной строки по объявленным спецификациям
	 * @param args аргументы командной строки
	 */
	public boolean parse(String[] args) {
		
		_error_string = "";
		Collections.sort(_options);
		
		boolean is_no_opt_args = false;
		int opt_index;
		Option opt;
		String opt_name;
		String opt_value;
		boolean opt_update;
		String arg;
		
		ArrayList<String> no_opt_args = new ArrayList<String>();
		
		for (int index = 0; index < args.length; index++) {
			arg = args[index];
			
			opt_name = "";
			opt_value = "";
			opt_update = false;
			
			if (is_no_opt_args) {
				no_opt_args.add(arg);
			} else if (arg.startsWith("-")) {
				opt_name = arg.substring(1);
			} else if (arg.startsWith("--")) {
				opt_name = arg.substring(2);
			}
			
			if (!opt_name.isEmpty()) {
				opt_index = getOptionIndex(opt_name);
				if (opt_index == -1) {
					_error_string += "Unknow option " + arg + "\n";
				} else {
					opt = _options.get(opt_index);
					if (opt.type == NO_ARGUMENT) {
						opt_update = true;
					} else {
						index++;
						if (index != args.length) {
							arg = args[index];
							if (arg.startsWith("-")) {
								if (opt.type == REQUIRED_ARGUMENT) {
									_error_string += "No present required argument at " + opt_name + " option";
								} else {
									// Опция с отсутствием необязательного аргумента
									opt_update = true;
								}
								index--;
							} else {
								// Аргумент опции
								opt_value = arg;
								opt_update = true;
							}
						} else if (opt.type == OPTIONAL_ARGUMENT) {
							opt_update = true;
						}
					}
					
					if (opt_update) {
						updateOption(opt_index, opt_value);
					}
				}
			} else {
				// Пошли аргументы без опций
				is_no_opt_args = true;
				no_opt_args.add(arg);
			}
		}

		// Перепишем аргументы без опций в массив
		no_opt_args.toArray(_no_opt_args);
		checkOptions();

		return !hasError();
	} // parse
	
	/**
	 * Возвращает массив аргументов, не являющихся опциями
	 * @return массив строк
	 */
	public String[] getNoOptArgs() {
		return _no_opt_args;
	}
	
	/**
	 * Возвращает признак наличия ошибки
	 * @return
	 */
	public boolean hasError() {
		return (!_error_string.isEmpty());
	}
	
	/**
	 * Возвращает описание ошибки
	 * @return
	 */
	public String getError() {
		return _error_string;
	}

	/**
	 * Позиционирует список в начало
	 */
	public void toBegin() {
		_optind = -1;
	}
	
	/**
	 * Позиционирует список в конец
	 */
	public void toEnd() {
		_optind = _options.size();
		_optind++;
	}
	
	/**
	 * Проверка на пустой список
	 * @return
	 */
	public boolean isEmpty() {
		return (_options.isEmpty());
	}
	
	/**
	 * Выбирает следующую опции
	 * @return Флаг успех
	 */
	public boolean next() {
		
		boolean retval = false;
		
		if (!isEmpty()) {
			_optind++;
			int opt_count = _options.size();
			_optind = (_optind > opt_count) ? ++opt_count : _optind;
			retval = (_optind <= opt_count);
		}
		
		return retval;
	}
	
	/**
	 * Выбирает предыдущую опции
	 * @return Флаг успеха
	 */
	public boolean prev() {
		
		boolean retval = false;
		
		if (!isEmpty()) {
			_optind--;
			_optind = (_optind < 0) ? -1 : _optind;
			retval = (_optind < 0);
		}
		
		return retval;
	}
	
	/**
	 * Проверка нахождения в начале списка
	 * @return Флаг начала списка
	 */
	public boolean atBegin() {
		return (_optind < 0);
	}
	
	/**
	 * Проверка нахождения в конце списка
	 * @return Флан конца списка
	 */
	public boolean atEnd() {
		return (_optind > _options.size() || isEmpty());
	}
	
	/**
	 * Возвращает значение текущей опции
	 * @return Текущее значение списка
	 */
	public String value() {
		
		String retval = "";
		
		if (!atBegin() && !atEnd()) {
			Option opt = (Option) _options.get(_optind);
			retval = opt.value;
		}
		
		return retval;
	}
	
	/**
	 * Проверяет наличие опции
	 * @param name Искомая опция
	 * @return Флаг наличия
	 */
	public boolean presentOpt(String name) {
		
		boolean retval = false;
		int index = getOptionIndex(name);
		
		if (index != -1) {
			Option opt = _options.get(index);
			retval = opt.present;
		}
		
		return retval;
	}
	
	/**
	 * Возвращает значение опции
	 * @param name Имя опции
	 * @return Значение опции
	 */
	public String getOptValue(String name) {
		
		String retval = "";
		int index = getOptionIndex(name);
		
		if (index != -1) {
			Option opt = _options.get(index);
			retval = opt.value;
		}
		
		return retval;
	}

	/**
	 * Возвращает помощь об описанных опциях
	 * @return строка помощи по опциям
	 */
	public String getOptHelp() {
		
		String retval = "Usage\n" + _progname + " ";
		String opts_desc = "";
		String _tmp = "";
		int opt_count = _options.size();
		
		for (int i = 0; i < opt_count; i++) {
			
			Option opt = _options.get(i);
			_tmp = "-" + opt.name;
			
			retval += (opt.request ? "" : "[") + _tmp + 
					(opt.type == REQUIRED_ARGUMENT ? " arg " : (opt.type == OPTIONAL_ARGUMENT ? " [arg] " : "")) + 
					(opt.request ? "" : "]") + " ";
			opts_desc += _tmp;
			if (!opt.long_name.isEmpty()) opts_desc += ", --" + opt.long_name;
			opts_desc += "\n";
			opts_desc += "\tОпция ";
			if (!opt.request) opts_desc += "не";
			opts_desc += "обязательна, аргумент";
			switch (opt.type) {
				case REQUIRED_ARGUMENT:
					opts_desc += " обязателен";
					break;
				case OPTIONAL_ARGUMENT:
					opts_desc += " необязателен";
					break;
				case NO_ARGUMENT:
					opts_desc += "а не требуется";
					break;
				default:
			}
			opts_desc += "\n";
			if (!opt.description.isEmpty()) {
				opts_desc += "\t" + opt.description + "\n";
			}
		}
		
		return retval + "\n" + opts_desc;
	}
}
