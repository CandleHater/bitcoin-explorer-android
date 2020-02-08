package candle.bitcoin.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import candle.bitcoin.explorer.esplora.EsploraBlock;
import candle.bitcoin.explorer.esplora.EsploraClient;
import candle.bitcoin.explorer.esplora.EsploraResponseCallback;

/**
 * An activity representing a list of Bitcoin blocks. This activity
 * has different presentations for handset and tablet sized devices.
 * <p>
 * Handsets:    The activity presents a list of {@link EsploraBlock} items,
 * which when touched, lead to a BlockDetailActivity.
 * <p>
 * Tablets:     The activity presents the list of {@link EsploraBlock} items
 * and the selected block details side-by-side, using two vertical panes.
 */
public class BlockListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    // the RecyclerView used to display the list of EsploraBlocks
    private BlockListRecyclerViewAdapter recyclerViewAdapter;

    // swipe refresh used to provide a pull-to-refresh for requesting recent blocks
    private SwipeRefreshLayout swipeRefreshLayout;

    // search view used to search for a certain block height
    private SearchView searchView;

    // two-pane mode (i.e. running on a tablet)
    private boolean isTwoPaneMode;

    // loader Snackbar
    private Snackbar loaderSnackbar;

    // delayed handler used to dismiss the loaderSnackbar
    private final Handler loaderSnackbarDelayedDismissHandler = new Handler();

    /**
     * Called on creation of this activity view.
     *
     * @param savedInstanceState Not null when there is fragment state saved from previous
     *                           configurations of this activity (e.g. when rotating the screen
     *                           from portrait to landscape).
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);

        // initialize toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        // toolbar.setCollapseIcon(R.drawable.ic_launcher_foreground);

        setSupportActionBar(toolbar);

        // the detail container view will be present only in the large-screen layouts
        if (findViewById(R.id.block_detail_container) != null) {
            isTwoPaneMode = true;

            /*
             * In two pane mode (tablets) load an empty block detail container. This will make
             * sure that the right pane isn't empty and shows a short introduction text.
             */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.block_detail_container, new BlockDetailFragment())
                    .commit();
        }

        // initialize
        initPullToRefresh();
        initBlockList();

        // load latest blocks
        doLoadMoreBlocks();
    }

    /**
     * Initializes the {@link RecyclerView} list with an {@link BlockListRecyclerViewAdapter} and
     * the {@link RecyclerViewEndlessScrollListener}.
     */
    private void initBlockList() {
        final RecyclerView recyclerView = findViewById(R.id.block_list);
        recyclerView.setHasFixedSize(true);

        // initialize and assign the RecyclerViewAdapter
        recyclerViewAdapter = new BlockListRecyclerViewAdapter(new View.OnClickListener() {
            // on block click
            @Override
            public void onClick(View itemView) {
                final EsploraBlock block = (EsploraBlock) itemView.getTag();
                showBlockDetail(block);
            }
        });

        recyclerView.setAdapter(recyclerViewAdapter);

        // add a scroll listener to RecyclerViewAdapter
        recyclerView.addOnScrollListener(new RecyclerViewEndlessScrollListener(20) {
            @Override
            protected void onLoadMore(final LoadingFinishedCallback loadingFinishedCallback) {
                final EsploraBlock blockOldest = recyclerViewAdapter.getBlockOldest();
                if (blockOldest == null) {
                    loadingFinishedCallback.loadingDone();
                    return;
                }

                // load more blocks
                final int startBlockHeight = blockOldest.getHeight() - 1;
                doLoadMoreBlocks(startBlockHeight, loadingFinishedCallback);
            }
        });
    }

    /**
     * Initialize the swipe refresher to offer pull-to-refresh.
     */
    private void initPullToRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doLoadMoreBlocks();
            }
        });
    }

    /**
     * Load more blocks starting from the given block height.
     *
     * @param startBlockHeight          Block height to request older blocks from.
     * @param loadingFinishedCallback   Callback to indicate the loading was finished.
     */
    private void doLoadMoreBlocks(
            final Integer startBlockHeight,
            final RecyclerViewEndlessScrollListener.LoadingFinishedCallback loadingFinishedCallback
    ) {

        Log.d("test", "startBlockHeight " + startBlockHeight);

        // remove the Snackbar delayed dismiss callback (prevents flickering)
        loaderSnackbarDelayedDismissHandler.removeCallbacksAndMessages(null);

        // make a loader Snackbar
        if(loaderSnackbar == null || !loaderSnackbar.isShownOrQueued()) {
            if(loaderSnackbar == null) {
                loaderSnackbar = Snackbar.make(
                        swipeRefreshLayout,
                        getString(R.string.loading_blocks),
                        Snackbar.LENGTH_INDEFINITE
                );
            } else {
                loaderSnackbar.setText(getString(R.string.loading_blocks));
            }

            // show Snackbar
            loaderSnackbar.show();
        }

        // request more blocks
        EsploraClient.getBlockList(startBlockHeight, new EsploraResponseCallback() {
            @Override
            public void onSuccess(final ArrayList<EsploraBlock> blocks) {
                loaderSnackbar.setText(getString(R.string.loading_blocks_success));

                // add loaded blocks
                recyclerViewAdapter.addBlocks(blocks, startBlockHeight == null);

                // finish loading
                loadingDone();
            }

            @Override
            public void onError() {
                loaderSnackbar.setText(getString(R.string.loading_blocks_error));
                loadingDone();
            }

            private void loadingDone() {
                // signal to the RecyclerViewEndlessScrollListener that loading is done
                if (loadingFinishedCallback != null) {
                    loadingFinishedCallback.loadingDone();
                }

                // stop the pull-to-refresh loading animation
                swipeRefreshLayout.setRefreshing(false);

                // delayed dismiss of the loader Snackbar
                loaderSnackbarDelayedDismissHandler.postDelayed(
                    new Runnable() {
                        public void run() {
                            loaderSnackbar.dismiss();
                        }
                    },1000
                );
            }
        });
    }

    /**
     * Request the most recently known blocks.
     */
    private void doLoadMoreBlocks() {
        doLoadMoreBlocks(null, null);
    }

    /**
     * Show the details of a block. Depending on the screen size the details are either displayed
     * on the right pane (tablet) or the BlockDetailAcivity will be started (phone).
     *
     * @param block The {@link EsploraBlock} object to show the details from.
     */
    private void showBlockDetail(final EsploraBlock block) {
        // reset search view
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        // show block details
        if (isTwoPaneMode) {
            // on right pane
            final Bundle arguments = new Bundle();
            arguments.putParcelable("block", block);

            final BlockDetailFragment fragment = new BlockDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.block_detail_container, fragment)
                    .commit();

        } else {
            // start activity
            final Intent intent = new Intent(this, BlockDetailActivity.class);
            intent.putExtra("block", block);

            startActivity(intent);
        }
    }

    /**
     * Inflates the toolbars menu and initializes the SearchView.
     *
     * @param menu  The menu of the toolbar.
     * @return      Always true.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // inflate the menu
        getMenuInflater().inflate(R.menu.menu, menu);

        // init block search
        final MenuItem searchItem = menu.findItem(R.id.block_search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.block_search_hint));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Called when the user submits a search in the SearchView.
     *
     * @param query Text entered in the SearchView.
     * @return Always true.
     */
    @Override
    public boolean onQueryTextSubmit(final String query) {
        int blockHeight;

        try {
            blockHeight = Integer.parseInt(query);
        } catch(Exception e) {
            // overflow
            return false;
        }

        // indicate to user that the search has been started
        final Snackbar searchSnackbar = Snackbar.make(
                swipeRefreshLayout,
                getString(R.string.searching),
                Snackbar.LENGTH_LONG
        );

        searchSnackbar.show();

        // get block data
        EsploraClient.getBlock(blockHeight, new EsploraResponseCallback() {
            @Override
            public void onSuccess(ArrayList<EsploraBlock> blocks) {
                if (blocks.isEmpty()) {
                    onError();
                    return;
                }

                showBlockDetail(blocks.get(0));
                searchSnackbar.dismiss();
            }

            @Override
            public void onError() {
                Snackbar.make(
                        swipeRefreshLayout,
                        getString(R.string.loading_blocks_error),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });

        return true;
    }

    /**
     * Unused onQueryTextChange.
     *
     * @param newText The text entered in the SearchView.
     * @return Always false.
     */
    @Override
    public boolean onQueryTextChange(final String newText) {
        return false;
    }
}
