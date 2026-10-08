package it.s8.awesomepizza;

import java.text.NumberFormat;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class CurrencyUtils {

    public static @NonNull String getTotalAsString(long total) {
        return NumberFormat.getCurrencyInstance().format(total / 100.0);
    }
}
