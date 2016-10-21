package com.ybg.rp.vm.serial.yf;

/**
 * Created by yangbagang on 16/8/25.
 */
public class TrackManager {

    public String getRealTrack(String trackNo){
        String no = trackNo.substring(1);
        String track="";

        if (no.equals("60")){
            track="17";
        }else if (no.equals("61")){
            track="18";
        }else if (no.equals("62")){
            track="19";
        }else if (no.equals("63")){
            track="20";
        }else if (no.equals("64")){
            track="21";
        }else if (no.equals("65")){
            track="22";
        }else if (no.equals("66")){
            track="23";
        }else if (no.equals("67")){
            track="24";
        }else if (no.equals("50")){
            track="25";
        }else if (no.equals("51")){
            track="26";
        }else if (no.equals("52")){
            track="27";
        }else if (no.equals("53")){
            track="28";
        }else if (no.equals("54")){
            track="29";
        }else if (no.equals("55")){
            track="30";
        }else if (no.equals("56")){
            track="31";
        }else if (no.equals("57")){
            track="32";
        }else if (no.equals("40")){
            track="33";
        }else if (no.equals("41")){
            track="34";
        }else if (no.equals("42")){
            track="35";
        }else if (no.equals("43")){
            track="36";
        }else if (no.equals("44")){
            track="37";
        }else if (no.equals("45")){
            track="38";
        }else if (no.equals("46")){
            track="39";
        }else if (no.equals("47")){
            track="40";
        }else if (no.equals("30")){
            track="41";
        }else if (no.equals("31")){
            track="42";
        }else if (no.equals("32")){
            track="43";
        }else if (no.equals("33")){
            track="44";
        }else if (no.equals("34")){
            track="45";
        }else if (no.equals("35")){
            track="46";
        }else if (no.equals("36")){
            track="47";
        }else if (no.equals("37")){
            track="48";
        }else if (no.equals("20")){
            track="49";
        }else if (no.equals("21")){
            track="50";
        }else if (no.equals("22")){
            track="51";
        }else if (no.equals("23")){
            track="52";
        }else if (no.equals("24")){
            track="53";
        }else if (no.equals("25")){
            track="54";
        }else if (no.equals("26")){
            track="55";
        }else if (no.equals("27")){
            track="56";
        }else if (no.equals("10")){
            track="57";
        }else if (no.equals("11")){
            track="58";
        }else if (no.equals("12")){
            track="59";
        }else if (no.equals("13")){
            track="60";
        }else if (no.equals("14")){
            track="61";
        }else if (no.equals("15")){
            track="62";
        }else if (no.equals("16")){
            track="63";
        }else if (no.equals("17")){
            track="64";
        }

        return track;
    }

}
