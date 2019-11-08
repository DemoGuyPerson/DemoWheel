package com.example.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyClass {

    public static void main(String[] args) {
//        String s = "m%3D70254350%26trackId%3D254027038%26directToPlaybackOrAutoplay%3Dtrue%26source_type_payload%3DgroupIndex%253D1%2526tileIndex%253D0%2526action%253Dplayback%2526category%253DPreapp%2526movieId%253D70254350%2526trackId%253D254027038%26source_type%3D27";
//        System.out.println(convertHathwayEpgStartStopTime2Second("2019-08-01 01:31:00"));

        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);

        list.add(list.size(),4);
        for (int i : list){
            System.out.println(i);
        }
    }

    public static long convertHathwayEpgStartStopTime2Second(String time) {
        //"2018-11-13T15:30:00"
        System.out.println(" time : "+time);
        String dateFormatter = "";
        dateFormatter = "yyyy-MM-dd HH:mm:ss";
//        dateFormatter = dateFormatter.replaceAll("T", "-");
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatter);
//        time = time.replaceAll("T", "-");
            Date date = simpleDateFormat.parse(time);
            //Log.d("wujiang", "convertHathwayEpgStartStopTime2Second: second = " + date.getTime() / 1000);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

      static String A(String s){
        String result = "";
          try {
             result = URLDecoder.decode(s,"UTF-8");
             System.out.println(" result : "+result);
             if (result.contains("%")){
                 result = A(result);
             }
          }catch (IllegalArgumentException e){
             e.printStackTrace();
             result = s;
          } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
              result = s;
          }
          return result;
      }
    public static int stringToInt(String string){
        int result = -1;
        if (string != null){
            try {
                if (string.startsWith("0x")){
                    result = Integer.parseInt(string.substring(2),16);
                }else{
                    result = Integer.parseInt(string,10);
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return result;
    }

}
