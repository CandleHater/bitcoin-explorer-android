package candle.bitcoin.explorer.esplora;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * The EsploraClient helps requesting Bitcoin Blockchain data from an Esplora HTTP API endpoint.
 * See https://github.com/Blockstream/esplora/blob/master/API.md for a documentation of the Esplora
 * HTTP API endpoint.
 */
public class EsploraClient {
    private static final AsyncHttpClient client = new AsyncHttpClient();

    private static final String baseUrl = "https://blockstream.info/api/";

    /**
     * Get a ArrayList of EsploraBlock instances. When a block height is supplied the following
     * blocks after that height are requested. When block height is not supplied (null), the
     * latest Bitcoin blocks will be requested.
     *
     * @param startBlockHeight The block height to start the request from. If null is supplied
     *                         the most recent blocks will be requested.
     * @param callback         A EsploraResponseCallback object to be called after the request
     *                         succeeded or had a failure.
     */
    public static void getBlockList(
            final Integer startBlockHeight,
            final EsploraResponseCallback callback
    ) {
        String relativeUrl = "blocks";

        // start block height
        if (startBlockHeight != null) {
            // prevent request when given start block height is a negative number
            if (startBlockHeight < 0) {
                callback.onError();
                return;
            }

            relativeUrl += "/" + startBlockHeight.toString();
        }

        // request API
        client.get(getAbsoluteUrl(relativeUrl), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(
                    final int statusCode,
                    final Header[] headers,
                    final JSONArray JSONBlocks
            ) {
                // skip empty list
                if (JSONBlocks.length() == 0) {
                    callback.onError();
                    return;
                }

                // create EsploraBlock objects and collaborate them in an ArrayList
                final ArrayList<EsploraBlock> blocks = new ArrayList<>();

                for (int i = 0; i < JSONBlocks.length(); i++) {
                    try {
                        final EsploraBlock block = parseJSONBlock(JSONBlocks.getJSONObject(i));
                        blocks.add(block);
                    } catch (JSONException e) {
                        callback.onError();
                        return;
                    }
                }

                // invoke success callback
                callback.onSuccess(blocks);
            }

            @Override
            public void onFailure(
                    final int statusCode,
                    final Header[] headers,
                    final String responseString,
                    final Throwable throwable
            ) {
                super.onFailure(statusCode, headers, responseString, throwable);

                // invoke error
                callback.onError();
            }
        });
    }

    /**
     * Requests and instantiates a EsploraBlock by a given block height.
     *
     * @param blockHeight The block height to be requested.
     * @param callback    A EsploraResponseCallback object to be called after the request
     *                    succeeded or had a failure.
     */
    public static void getBlock(final int blockHeight, final EsploraResponseCallback callback) {
        // prevent request when given block height is a negative number
        if (blockHeight < 0) {
            callback.onError();
            return;
        }

        // request API
        final String relativeUrl = "block-height/" + blockHeight;

        client.get(getAbsoluteUrl(relativeUrl), new TextHttpResponseHandler() {
            @Override
            public void onSuccess(
                    final int statusCode,
                    final Header[] headers,
                    final String blockHash
            ) {
                if (blockHash.isEmpty()) {
                    callback.onError();
                    return;
                }

                // request the block by hash
                getBlock(blockHash, callback);
            }

            @Override
            public void onFailure(
                    final int statusCode,
                    final Header[] headers,
                    final String responseString,
                    final Throwable throwable
            ) {
                callback.onError();
            }
        });
    }

    /**
     * Requests and instantiates a EsploraBlock by a given block hash.
     *
     * @param blockHash The block hash to be requested.
     * @param callback  A {@link EsploraResponseCallback} object to be called after the request
     *                  succeeded or had a failure.
     */
    private static void getBlock(final String blockHash, final EsploraResponseCallback callback) {
        final String relativeUrl = "block/" + blockHash;

        // request API
        client.get(getAbsoluteUrl(relativeUrl), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(
                    final int statusCode,
                    final Header[] headers,
                    final JSONObject blockJSON
            ) {
                final EsploraBlock block = parseJSONBlock(blockJSON);
                if (block == null) {
                    callback.onError();
                    return;
                }

                final ArrayList<EsploraBlock> blocks = new ArrayList<>();
                blocks.add(block);

                callback.onSuccess(blocks);
            }

            @Override
            public void onFailure(
                    final int statusCode,
                    final Header[] headers,
                    final String responseString,
                    final Throwable throwable
            ) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.onError();
            }
        });
    }

    /**
     * Creates a new {@link EsploraBlock} object by parsing given JSON data.
     *
     * @param blockJSON JSON formatted data used to populate the new {@link EsploraBlock} object.
     * @return A new instance of an {@link EsploraBlock} object.
     */
    private static EsploraBlock parseJSONBlock(final JSONObject blockJSON) {
        final EsploraBlock block;

        try {
            block = new EsploraBlock(blockJSON);
        } catch (EsploraClientException e) {
            return null;
        }

        return block;
    }

    /**
     * Converts a given relative Esplora HTTP API URL to an absolute URL.
     *
     * @param relativeUrl The relative Esplora HTTP API URL url.
     * @return A absolute Esplora HTTP API url.
     */
    private static String getAbsoluteUrl(final String relativeUrl) {
        return baseUrl + relativeUrl;
    }
}
