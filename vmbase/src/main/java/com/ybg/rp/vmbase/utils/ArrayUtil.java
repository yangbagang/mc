package com.ybg.rp.vmbase.utils;

/**
 * Created by yangbagang on 16/8/22.
 */
public class ArrayUtil {

    /**
     * byte[] 拼接
     *
     * @param byte1 原始数据
     * @param byte2 新增数据
     * @return
     */
    public static byte[] append(byte[] byte1, byte[] byte2) {
        if (byte2 == null || byte2.length == 0) {
            return byte1;
        }
        return append(byte1, byte2, byte2.length);
    }

    /**
     * BYTE[] 添加
     *
     * @param byte1 当前数据
     * @param byte2 数据
     * @param length          数据长度
     * @return
     */
    public static byte[] append(byte[] byte1, byte[] byte2, int length) {
        if ((byte1 == null) && (byte2 == null))
            return null;
        if (byte1 == null) {
            byte[] arrayOfByte3 = new byte[length];
            System.arraycopy(byte2, 0, arrayOfByte3, 0, length);
            return arrayOfByte3;
        }
        if (byte2 == null) {
            byte[] arrayOfByte2 = new byte[byte1.length];
            System.arraycopy(byte1, 0, arrayOfByte2, 0, byte1.length);
            return arrayOfByte2;
        }
        byte[] arrayOfByte1 = new byte[length + byte1.length];
        System.arraycopy(byte1, 0, arrayOfByte1, 0, byte1.length);
        System.arraycopy(byte2, 0, arrayOfByte1, byte1.length, length);
        return arrayOfByte1;
    }

}
