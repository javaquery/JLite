package io.popcorn.util.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class Numbers {

    /**
     * Round the decimal number.
     * example: roundDecimal(10.123456789, 2) = 10.12
     * example: roundDecimal(10.123456789, 4) = 10.1235
     * example: roundDecimal(10.576, 2) = 10.58
     *
     * @param number number to round
     * @param decimalPlaces decimal places to round
     * @return rounded number
     */
    public static Double roundDecimal(Double number, int decimalPlaces){
        return BigDecimal.valueOf(number)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
