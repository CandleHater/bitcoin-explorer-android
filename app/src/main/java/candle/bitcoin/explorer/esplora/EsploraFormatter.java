package candle.bitcoin.explorer.esplora;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A util class to help with formatting EsploraBlock data as a human readable string. It formats
 * and converts the supplied data analog to the output seen on https://blockstream.info.
 */
public class EsploraFormatter {
    final private static DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy, h:m:s a z", Locale.US);
    final private static DecimalFormat blockHeightNumberFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    final private static DecimalFormat byteSizeNumberFormat = new DecimalFormat("###.###");

    // initialize the block height number formatter
    static {
        final DecimalFormatSymbols symbols = blockHeightNumberFormat.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        blockHeightNumberFormat.setDecimalFormatSymbols(symbols);
    }

    /**
     * Formats a given block height (i.e. 133 700).
     *
     * @param blockHeight Block height to format.
     * @return Formatted block height.
     */
    public static String blockHeight(final int blockHeight) {
        return blockHeightNumberFormat.format(blockHeight);
    }

    /**
     * Formats a given Date object (i.e. 3/1/2009, 7:15:05 PM GMT+1)
     *
     * @param time Block creation time to format.
     * @return Formatted block time.
     */
    public static String time(final Date time) {
        return dateFormat.format(time);
    }

    /**
     * Formats a given byte size and converts it to kilobyte.
     *
     * @param size Block size in bytes.
     * @return Formatted block size in kilobyte.
     */
    public static String byteSize(final int size) {
        // convert size from byte to kilobyte
        final double sizeKB = (double) size / 1000;

        // format number
        return byteSizeNumberFormat.format(sizeKB);
    }

    /**
     * Formats a given virtual byte size and converts it to kilobyte.
     *
     * @param size Virtual size in byte.
     * @return Formatted block virtuel size in kilobyte.
     */
    public static String byteSizeVirtual(final double size) {
        // convert size from byte to kilobyte
        final Integer sizeKB = (int) (size / 1000);

        // format number
        return sizeKB.toString();
    }

    /**
     * Formats a given int value as a hex string (i.e. 0x01). Makes sure that the resulting hex
     * string does not consist of an uneven length, by adding a leading zero.
     *
     * @param number Number to be represented as a hex string.
     * @return Formatted hex string (incl. "0x" appending).
     */
    public static String hex(final int number) {
        final String hex = Integer.toHexString(number);
        return (hex.length() % 2 == 0 ? "0x" : "0x0") + hex;
    }

}