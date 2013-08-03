/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz.utils;

/**
 * Преобразования
 *
 * @author shmalevoz
 */
public class Conversion {

    public static final int DECIMAL = 0;
    public static final int HEX = 1;
    public static final int OCTAL = 2;
	public static final int DEFAULT = DECIMAL;

    /**
     * Преобразует массив byte в значение int
     *
     * @param source Исходный массив
     * @return Значение int массива
     */
    public static int ByteArray2Int(byte[] source) {

        return ByteArray2Int(source, 0);
    }

    /**
     * Преобразует массив byte в значение int
     *
     * @param source Исходный массив
     * @param pos Начальная позиция преобразования в массиве
     * @return Значение int
     */
    public static int ByteArray2Int(byte[] source, int pos) {

        return ByteArray2Int(source, pos, false);
    }

    /**
     * Преобразует массив байт в значение int
     *
     * @param source Исходный массив
     * @param pos Начальная позиция преобразования в массиве
     * @param reversed_bytes Признак обратного порядка байт в массиве
     * @return Значение int
     */
    public static int ByteArray2Int(byte[] source, int pos, boolean reversed_bytes) throws ArrayIndexOutOfBoundsException {

		int len = source.length - pos;
		len = Math.min(len, 4);
		if (len <= 0) throw new ArrayIndexOutOfBoundsException("Ошибка при преобразовании byte[] в int: индекс за пределами массива");
		
        int retval = 0;
        if (reversed_bytes) {
			if (len > 3) retval += ((source[pos + 3] & 0xFF) << 24);
			if (len > 2) retval += ((source[pos + 2] & 0xFF) << 16);
			if (len > 1) retval += ((source[pos + 1] & 0xFF) << 8);
			retval += (source[pos] & 0xFF);
        } else {
			if (len > 3) retval += (source[pos + 3] & 0xFF);
			if (len > 2) retval += ((source[pos + 2] & 0xFF) << 8);
			if (len > 1) retval += ((source[pos + 1] & 0xFF) << ((len - 2) * 8));
			retval += ((source[pos] & 0xFF) << ((len - 3) * 8));
        }

        return retval;
    }

    /**
     * Преобразует значение int в массив byte
     *
     * @param val Преобразуемое значение
     * @return Массив byte[4]
     */
    public static byte[] Int2ByteArray(int val) {

        return Int2ByteArray(val, false);
    }

    /**
     * Преобразует значение int в массив byte
     *
     * @param val Преобразуемое значение
     * @param reverse_bytes Признак обратного порядка байт
     * @return Массив byte[4]
     */
    public static byte[] Int2ByteArray(int val, boolean reverse_bytes) {

        byte[] retval = new byte[4];

        Int2ByteArray(val, retval, 0, reverse_bytes);

        return retval;
    }

    /**
     * Преобразует значение int в массив byte
     *
     * @param val Преобразуемое значение
     * @param buf Буфер для записи значения
     */
    public static void Int2ByteArray(int val, byte[] buf) {

        Int2ByteArray(val, buf, 0);
    }

    /**
     * Преобразует значение int в массив byte
     *
     * @param val Преобразуемое значение
     * @param buf Буфер для записи значения
     * @param offset Смещение записи в буфере
     */
    public static void Int2ByteArray(int val, byte[] buf, int offset) {

        Int2ByteArray(val, buf, offset, false);
    }

    /**
     * Преобразует значение int в массив byte
     *
     * @param val Преобразуемое значение
     * @param buf Буфер для записи значения
     * @param offset Смещение для записи в буфере
     * @param reverse_bytes Признак обратного порядка байт
     */
    public static void Int2ByteArray(int val, byte[] buf, int offset, boolean reverse_bytes) throws ArrayIndexOutOfBoundsException {

		int len = Math.min(4, buf.length - offset);
		if (len <= 0) throw new ArrayIndexOutOfBoundsException("Ошибка при преобразовании int в byte[]: индекс за пределами массива");
		
        if (reverse_bytes) {
			if (len > 3) buf[offset + 3] = (byte) (val >>> 24);
            if (len > 2) buf[offset + 2] = (byte) (val >>> 16);
            if (len > 1) buf[offset + 1] = (byte) (val >>> 8);
            buf[offset] = (byte) val;
        } else {
            buf[offset] = (byte) (val >>> ((len - 1) * 8)); // (byte) (val >>> 24); // 4 == 24; 2 == 8
            if (len > 1) buf[offset + 1] = (byte) (val >>> ((len - 2) * 8));   // (byte) (val >>> 16); 4 == 16; 2 == 0
            if (len > 2) buf[offset + 2] = (byte) (val >>> 8);
            if (len > 3) buf[offset + 3] = (byte) val;
        }
    }
	
	/**
	 * Преобразует строку десятичного числа в int
	 * 
	 * @param val число строкой в массиве byte
	 * @return int значение
	 */
	public static int String2Int(byte[] val) {

		return String2Int(val, DEFAULT);
	}
	
	/**
	 * Преобразует строку числа по типу в int
	 * 
	 * @param val число строкой в массиве byte
	 * @param type тип числа в строке
	 * @return int значение
	 */
	public static int String2Int(byte[] val, int type) {

		return String2Int(new String(val), type);
	}

    /**
     * Преобразует строку десятичного числа в int
     *
     * @param val Исходная строка
     * @return Преобразованное число
     */
    public static int String2Int(char[] val) {

        return String2Int(val, DEFAULT);
    }

    /**
     * Преобразует строку числа по типу в int
     *
     * @param val Исходная строка
     * @param type Тип числа в строке
     * @return Преобразованное число
     */
    public static int String2Int(char[] val, int type) {

        return String2Int(new String(val), type);
    }

    /**
     * Преобразует строку десятичного числа в int
     *
     * @param val преобразуемое значение
     * @return Преобразованное число
     */
    public static int String2Int(String val) {

        return String2Int(val, DEFAULT);

    }

    /**
     * Преобразует строку в int по указанному типу числа
     *
     * @param val Преобразуемое значение
     * @param type Тип числа в строке
     * @return Преобразованное число
     */
    public static int String2Int(String val, int type) {

        String prefix = "";

        switch (type) {

            case HEX:
                prefix = "0x";
                break;
            case OCTAL:
                prefix = "0";
        }
        return Integer.decode(prefix + val);
    }
}
