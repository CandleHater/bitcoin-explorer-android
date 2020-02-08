package candle.bitcoin.explorer.esplora;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * The EsploraBlock object is a representation of the block data provided from the Esplora API.
 */
public class EsploraBlock implements Parcelable {
    private String hash;
    private String hashPrevious;
    private int height;
    private int version;
    private Date time;
    private int txCount;
    private int size;
    private int weight;
    private String merkleRoot;
    private int nonce;
    private int bits;

    /**
     * Initializes and populates this EsploraBlock object from the given JSON data from a Esplora
     * API instance.
     *
     * @param JSONData JSON formatted Esplora block to populate this object.
     * @throws EsploraClientException Invalide Esplora API data supplied.
     */
    EsploraBlock(final JSONObject JSONData) throws EsploraClientException {
        try {
            setHash(JSONData.getString("id"));
            setHeight(JSONData.getInt("height"));

            // skip block 0 (has no previous block hash)
            if (getHeight() != 0) {
                setHashPrevious(JSONData.getString("previousblockhash"));
            }

            setVersion(JSONData.getInt("version"));
            setTime(JSONData.getInt("timestamp"));
            setTxCount(JSONData.getInt("tx_count"));
            setSize(JSONData.getInt("size"));
            setWeight(JSONData.getInt("weight"));

            setMerkleRoot(JSONData.getString("merkle_root"));
            setNonce(JSONData.getInt("nonce"));
            setBits(JSONData.getInt("bits"));
        } catch (JSONException e) {
            throw new EsploraClientException(e);
        }
    }

    /**
     * Initializes and populates this EsploraBlock object from the given Parcel data. The code
     * below was automatically generated with http://www.parcelabler.com/.
     *
     * @param in Input Parcel to populate the object.
     */
    private EsploraBlock(final Parcel in) {
        hash = in.readString();
        hashPrevious = in.readString();
        height = in.readInt();
        version = in.readInt();
        final long tmpTime = in.readLong();
        time = tmpTime != -1 ? new Date(tmpTime) : null;
        txCount = in.readInt();
        size = in.readInt();
        weight = in.readInt();
        merkleRoot = in.readString();
        nonce = in.readInt();
        bits = in.readInt();
    }

    /**
     * @return Hash of the block.
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash Hash of the block.
     * @throws EsploraClientException Supplied block hash is invalid.
     */
    private void setHash(final String hash) throws EsploraClientException {
        if (!this.isValidBlockHash(hash)) {
            throw new EsploraClientException("supplied block hash is invalid");
        }

        this.hash = hash;
    }

    // /**
    //  * @return Previous hash of the block.
    //  */
    // public String getHashPrevious() {
    //     return hashPrevious;
    // }

    /**
     * @param hashPrevious Previous hash of the block.
     * @throws EsploraClientException Supplied block hash is invalid.
     */
    private void setHashPrevious(final String hashPrevious) throws EsploraClientException {
        if (!this.isValidBlockHash(hashPrevious)) {
            throw new EsploraClientException("supplied previous block hash is invalid");
        }

        this.hashPrevious = hashPrevious;
    }

    /**
     * @return Height of the block.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height Height of the block.
     * @throws EsploraClientException Height must be an unsigned integer.
     */
    private void setHeight(int height) throws EsploraClientException {
        if (height < 0) {
            throw new EsploraClientException("height must be an unsigned integer");
        }

        this.height = height;
    }

    /**
     * @return Version of the block.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version Version of the block.
     */
    private void setVersion(final int version) {
        this.version = version;
    }

    /**
     * @return Time of the block creation.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the time of the block creation. Converts the given unix timestamp to a Date object.
     *
     * @param timestamp Time of the block creation.
     * @throws EsploraClientException Time must be an unsigned integer.
     */
    private void setTime(final int timestamp) throws EsploraClientException {
        if (timestamp < 0) {
            throw new EsploraClientException("timestamp must be an unsigned integer");
        }

        this.time = new Date((long) timestamp * 1000);
    }

    /**
     * @return Transaction count of the block.
     */
    public int getTxCount() {
        return txCount;
    }

    /**
     * @param txCount Transaction count of the block.
     * @throws EsploraClientException Transaction count must be an unsigned integer.
     */
    private void setTxCount(final int txCount) throws EsploraClientException {
        if (txCount < 0) {
            throw new EsploraClientException("txCount must be an unsigned integer");
        }

        this.txCount = txCount;
    }

    /**
     * @return Size of the block in byte.
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size Size of the block in byte.
     */
    private void setSize(final int size) {
        this.size = size;
    }

    /**
     * @return Virtual size of the block in byte.
     */
    public double getSizeVirtual() {
        return (double) weight / 4;
    }

    /**
     * @return Weight of the block.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight Weight of the block.
     */
    private void setWeight(final int weight) {
        this.weight = weight;
    }

    /**
     * @return Merkle root of the block.
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * @param merkleRoot Merkle root of the block.
     */
    private void setMerkleRoot(final String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * @return Nonce of the block.
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * @param nonce Nonce of the block.
     */
    private void setNonce(final int nonce) {
        this.nonce = nonce;
    }

    /**
     * @return Bits of the block.
     */
    public int getBits() {
        return bits;
    }

    /**
     * @param bits Bits of the block.
     */
    private void setBits(final Integer bits) {
        this.bits = bits;
    }

    /**
     * Checks if a given block hash seems to be valid. There's no guarantee that it actually is
     * a valid Bitcoin block hash.
     *
     * @param hash Hash to be validated.
     * @return A boolean value representing the validations result.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidBlockHash(final String hash) {
        if (!hash.matches("[A-Fa-f0-9]{64}")) {
            return false;
        }

        return hash.startsWith("00");
    }

    /**
     * Checks if a given object of a EsploraBlock seems to be the same as the current
     * instantiation. Returns true if the given object is a instance of an EsploraBlock object
     * and both share the same block hash.
     *
     * @param block Object to check against equalization to this instantiation.
     * @return A boolean value representing the equalization check result.
     */
    @Override
    public boolean equals(final Object block) {
        if (!(block instanceof EsploraBlock)) {
            return false;
        }

        return getHash().equals(
                ((EsploraBlock) block).getHash()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(hashPrevious);
        dest.writeInt(height);
        dest.writeInt(version);
        dest.writeLong(time != null ? time.getTime() : -1L);
        dest.writeInt(txCount);
        dest.writeInt(size);
        dest.writeInt(weight);
        dest.writeString(merkleRoot);
        dest.writeInt(nonce);
        dest.writeInt(bits);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EsploraBlock> CREATOR = new Parcelable.Creator<EsploraBlock>() {
        @Override
        public EsploraBlock createFromParcel(Parcel in) {
            return new EsploraBlock(in);
        }

        @Override
        public EsploraBlock[] newArray(int size) {
            return new EsploraBlock[size];
        }
    };
}
