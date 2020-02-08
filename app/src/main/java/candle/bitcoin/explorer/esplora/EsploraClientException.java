package candle.bitcoin.explorer.esplora;

/**
 * Exception thrown by EsploraClient.
 */
class EsploraClientException extends Exception {
    /**
     * @param exeption Provided Exception.
     */
    public EsploraClientException(final Exception exeption) {
        super(exeption);
    }

    /**
     * @param message Provided message of the Exception.
     */
    public EsploraClientException(final String message) {
        super(message);
    }
}
