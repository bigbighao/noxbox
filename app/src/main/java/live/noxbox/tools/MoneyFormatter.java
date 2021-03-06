package live.noxbox.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class MoneyFormatter {

    private static final int SCALE = 2;

    public static String format(BigDecimal number) {
        return format(number, SCALE);
    }

    public static String format(BigDecimal number, int scale) {
        return String.format(Locale.getDefault(), "%." + scale +  "f", scale(number, scale));
    }

    public static BigDecimal scale(String number) {
        return scale(new BigDecimal(number), SCALE);
    }

    public static BigDecimal scale(BigDecimal number) {
        return scale(number, SCALE);
    }

    public static BigDecimal scale(BigDecimal number, int scale) {
        return number.setScale(scale, RoundingMode.DOWN);
    }




}
