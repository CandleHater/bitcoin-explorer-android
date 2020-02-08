package candle.bitcoin.explorer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import candle.bitcoin.explorer.esplora.EsploraBlock;
import candle.bitcoin.explorer.esplora.EsploraFormatter;

/**
 * RecyclerView adapter to manage a list of EsploraBlock objects.
 */
public class BlockListRecyclerViewAdapter extends RecyclerView.Adapter<BlockListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<EsploraBlock> blockList = new ArrayList<>();
    private final View.OnClickListener onBlockClickListener;

    public BlockListRecyclerViewAdapter(final View.OnClickListener onBlockClickListener) {
        this.onBlockClickListener = onBlockClickListener;
    }

    /**
     * Get a block from the adaptors current block list.
     *
     * @param blockListIndex Index of desired block in block list
     * @return The desired EsploraBlock object
     */
    private EsploraBlock getBlock(final int blockListIndex) {
        if (blockListIndex < 0) {
            return null;
        }
        if (blockListIndex > getItemCount() - 1) {
            return null;
        }

        return blockList.get(blockListIndex);
    }

    public EsploraBlock getBlockOldest() {
        return getBlock(getItemCount() - 1);
    }

    /**
     * Add multiple EsploraBlock objects to the blockList.
     *
     * @param blocks    ArrayList of EsploraBlock objects to be added.
     * @param replace   Indicates if the items should be replaced.
     */
    public void addBlocks(final ArrayList<EsploraBlock> blocks, final boolean replace) {
        // skip empty
        if (blocks.size() == 0) {
            return;
        }

        // prevent duplications
        blockList.removeAll(blocks);

        // add
        if (replace) {
            blockList = new ArrayList<>();
            blockList.addAll(0, blocks);

        } else {
            // append
            blockList.addAll(blocks);
        }

        // notify change
        notifyDataSetChanged();
    }

    /**
     * Inflates a new block list item and creates a ViewHolder object with it.
     *
     * @param parent    Parent ViewGroup.
     * @param viewType  Type of view.
     * @return A new ViewHolder instance initialized with a newly inflated block list item.
     */
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_list_item, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to
     * represent a {@link EsploraBlock} object.
     *
     * @param holder    The used ViewHolder.
     * @param position  Current scrolling position.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // set background color
        holder.itemView.setBackgroundResource(
                position % 2 == 0 ? R.color.colorBackground : R.color.colorBackgroundLight
        );

        // get block
        final EsploraBlock block = blockList.get(position);

        // height
        final TextView heightTextView = holder.itemView.findViewById(R.id.height);
        heightTextView.setText(
                EsploraFormatter.blockHeight(block.getHeight())
        );

        // timestamp
        final TextView timestampTextView = holder.itemView.findViewById(R.id.timestamp);
        timestampTextView.setText(
                EsploraFormatter.time(block.getTime())
        );

        // transactions
        final TextView transactionsTextView = holder.itemView.findViewById(R.id.transactions);
        transactionsTextView.setText(
                Integer.toString(block.getTxCount())
        );

        // size
        final TextView sizeTextView = holder.itemView.findViewById(R.id.size);
        sizeTextView.setText(
                EsploraFormatter.byteSize(block.getSize())
        );

        // weight
        final TextView weightTextView = holder.itemView.findViewById(R.id.weight);
        weightTextView.setText(
                EsploraFormatter.byteSize(block.getWeight())
        );

        // set block as tag on this items view
        holder.itemView.setTag(block);

        // set click listener
        holder.itemView.setOnClickListener(onBlockClickListener);
    }

    /**
     * Provides the current amount of {@link EsploraBlock} objects managed.
     *
     * @return Amount of managed {@link EsploraBlock} objects.
     */
    @Override
    public int getItemCount() {
        return blockList.size();
    }

    /**
     * The ViewHolder of this RecyclerView adpater.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Constructor of the ViewHolder.
         *
         * @param view The view of this ViewHolder.
         */
        ViewHolder(final View view) {
            super(view);
        }
    }
}