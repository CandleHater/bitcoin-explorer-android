package candle.bitcoin.explorer.esplora;

import java.util.ArrayList;

/**
 * Callback interface used by EsploraClient to respond to requests.
 */
public interface EsploraResponseCallback {
    /**
     * Called when the desired request got successfully fulfilled.
     *
     * @param blocks ArrayList of EsploraBlock objects.
     */
    void onSuccess(ArrayList<EsploraBlock> blocks);

    /**
     * Called when the desired request did not get successfully fulfilled.
     */
    void onError();
}
