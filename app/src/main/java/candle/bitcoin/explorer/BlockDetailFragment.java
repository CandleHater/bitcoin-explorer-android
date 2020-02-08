package candle.bitcoin.explorer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import candle.bitcoin.explorer.esplora.EsploraBlock;
import candle.bitcoin.explorer.esplora.EsploraFormatter;

/**
 * A fragment representing a single Block detail screen.
 * This fragment is either contained in a {@link BlockListActivity} two-pane mode (on tablets)
 * or a {@link BlockDetailActivity} on handsets.
 */
@SuppressWarnings("WeakerAccess")
public class BlockDetailFragment extends Fragment {
    // the EsploraBlock object presented in this fragment
    private EsploraBlock esploraBlock;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BlockDetailFragment() {
    }

    /**
     * Initialize the block detail activity and retrieve the given {@link EsploraBlock} object (if
     * one is given).
     *
     * @param savedInstanceState Saved instance bundle from a previous instance.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get arguments
        final Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("block")) {
            return;
        }

        // get block
        esploraBlock = arguments.getParcelable("block");
        if (esploraBlock == null) {
            return;
        }
    }

    /**
     * Inflates the block detail view and populates it with the given EsploraBlock objects data.
     *
     * @param inflater              The LayoutInflater used to inflate the Layout.
     * @param container             The Container ViewGroup.
     * @param savedInstanceState    Saved instance bundle from a previous instance.
     * @return The inflated and populated block detail view.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        // inflate block detail view
        final View rootView = inflater.inflate(
                R.layout.block_detail,
                container,
                false
        );

        /*
         * Abort when no EsploraBlock was found. This happens when being in two pane mode (tablet)
         * and no block was selected yet.
         */
        if (esploraBlock == null) {
            // display an introduction text, so the right pane is not empty
            rootView.findViewById(R.id.introductionTextView)
                    .setVisibility(View.VISIBLE);

            return rootView;
        }

        // title
        final CollapsingToolbarLayout toolbarLayout = getActivity().findViewById(R.id.toolbar_layout);
        if (toolbarLayout != null) {
            final String title = getString(R.string.block)
                    + " "
                    + EsploraFormatter.blockHeight(esploraBlock.getHeight());

            toolbarLayout.setTitle(title);
        }

        // hash - header
        final TextView blockHashTextView = getActivity().findViewById(R.id.block_hash_header);
        if(blockHashTextView != null) {
            blockHashTextView.setText(
                    esploraBlock.getHash()
            );
        }

        // make detail TableView visible
        rootView.findViewById(R.id.table_layout).setVisibility(View.VISIBLE);

        // height
        final TextView heightTextView = rootView.findViewById(R.id.height);
        heightTextView.setText(
                EsploraFormatter.blockHeight(esploraBlock.getHeight())
        );

        // timestamp
        final TextView timestampTextView = rootView.findViewById(R.id.timestamp);
        timestampTextView.setText(
                EsploraFormatter.time(esploraBlock.getTime())
        );

        // transactions
        final TextView transactionsTextView = rootView.findViewById(R.id.transactions);
        transactionsTextView.setText(
                Integer.toString(esploraBlock.getTxCount())
        );

        // size
        final TextView sizeTextView = rootView.findViewById(R.id.size);
        sizeTextView.setText(
                String.format(
                        "%s %s",
                        EsploraFormatter.byteSize(esploraBlock.getSize()),
                        getString(R.string.size_unit)
                )
        );

        // size virtual
        final TextView sizeVirtualTextView = rootView.findViewById(R.id.size_virtual);
        sizeVirtualTextView.setText(
                String.format(
                        "%s %s",
                        EsploraFormatter.byteSizeVirtual(esploraBlock.getSizeVirtual()),
                        getString(R.string.size_virtual_unit)
                )
        );

        // weight
        final TextView weightTextView = rootView.findViewById(R.id.weight);
        weightTextView.setText(
                String.format(
                        "%s %s",
                        EsploraFormatter.byteSize(esploraBlock.getWeight()),
                        getString(R.string.weight_unit)
                )
        );

        // version
        final TextView versionTextView = rootView.findViewById(R.id.version);
        versionTextView.setText(
                EsploraFormatter.hex(esploraBlock.getVersion())
        );

        // bits
        final TextView bitsTextView = rootView.findViewById(R.id.bits);
        bitsTextView.setText(
                EsploraFormatter.hex(esploraBlock.getBits())
        );

        // nonce
        final TextView nonceTextView = rootView.findViewById(R.id.nonce);
        nonceTextView.setText(
                EsploraFormatter.hex(esploraBlock.getNonce())
        );

        // merkle_root
        final TextView merkleRootTextView = rootView.findViewById(R.id.merkle_root);
        merkleRootTextView.setText(
                esploraBlock.getMerkleRoot()
        );

        // hash
        final TextView hashTextView = rootView.findViewById(R.id.hash);
        hashTextView.setText(
                esploraBlock.getHash()
        );

        return rootView;
    }
}
