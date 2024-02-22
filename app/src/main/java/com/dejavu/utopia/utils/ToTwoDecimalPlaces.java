package com.dejavu.utopia.utils;

import java.text.DecimalFormat;

public class ToTwoDecimalPlaces {
   public static DecimalFormat df = new DecimalFormat("#.00");
    public static String StringType(Double value){
        return  df.format(value);
    }

    public static Double DoubleType(Double value){
        return Double.parseDouble(df.format(value));
    }

    public static void main(String[] args) {
        Double val = 12.36985;
        StringType(val);
        DoubleType(val);
    }
}
