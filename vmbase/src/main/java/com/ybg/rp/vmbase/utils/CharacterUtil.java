package com.ybg.rp.vmbase.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangbagang on 16/8/22.
 */
public class CharacterUtil {

    /**
     * 自动补零
     *
     * @param str    需补充的字符串
     * @param length 报文的总长度
     * @return
     */
    public static String autoCompZero(String str, int length) {
        String compStr = "0";
        int strLength = str.length();//字符串的长度
        String temp = "";
        for (int i = length - strLength; i > 0; i--) {
            temp = temp + compStr;
        }
        return temp + str;
    }

    /**
     * 低位在前.高位在后
     * @return
     */
    public static String getHignPreLowEndDecimalist(String str){
        if(str==null||str.length()%2!=0){
            return  "";
        }
        StringBuffer temp  = new StringBuffer();
        int status = 0;
        for (int i = str.length()-2; i>=0; i-=2) {
            temp.append(str.substring(i,i+2)+",");
        }
        String [] t = temp.toString().split(",");
        StringBuffer retHexValue  = new StringBuffer();
        for (int i = 0; i < t.length; i++) {
            if(t[i].equals("00")&&status ==0){
                continue;
            }else{
                status = 1;
                retHexValue.append(t[i]);
            }
        }
        if(retHexValue.toString()==null||retHexValue.toString().equals("")){
            retHexValue.append("0");
        }
        return retHexValue.toString();
    }

    /**
     * 十六进制字符串转换成十进制的Integer
     *
     * @param str
     */
    public static Integer strHexToInteger(String str) {
//        Double dd = Double.parseDouble(str);
//        return Integer.parseInt(String.valueOf(dd.intValue()), 16);
        return Integer.parseInt(str, 16);
    }

    /**
     * 字节十六进制字符串
     *
     * @param src
     * @param size
     * @return
     * @author liyuanming
     */
    public static String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            hex += " ";
            ret += hex;
        }
        return ret.toUpperCase();
    }


    /**
     * 规则
     *
     * @param str
     * @return
     * @author liyuanming
     */
    public static boolean Rule(String str) {
        boolean result;
        String reg = "[a-fA-F0-9 ]*";
        if (str.matches(reg)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 将两个ASCII字符合成一个字节
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 字符转16 进制
     *
     * @param s
     * @return
     */
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 转化十六进制编码为字符串
     * 16进制转字符串
     *
     * @param s
     * @return
     */
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 切割
     * 获取数组
     *
     */
    public static List<String> getDataList(String data){
        String[] types=data.split(",");
        List<String> workTypeList=new ArrayList<String>();
        for (int i=0;i<types.length;i++){
            workTypeList.add(types[i].trim());
        }
        return workTypeList;
    }

}
