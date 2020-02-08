package candle.bitcoin.explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import candle.bitcoin.explorer.esplora.EsploraBlock;

/**
 * An activity representing a single Block detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BlockListActivity}.
 */
public class BlockDetailActivity extends AppCompatActivity {

    // EsploraBlock object to present with this activity
    private EsploraBlock esploraBlock = null;

    /**
     * Called on creation of this activity view.
     *
     * @param savedInstanceState Not null when there is fragment state saved from previous
     *                           configurations of this activity (e.g. when rotating the screen
     *                           from portrait to landscape).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_detail);

        // initialize toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // show the back button in the action bar
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // fab (shares link to block detail page on https://blockstream.info)
        final FloatingActionButton fab = findViewById(R.id.share_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (esploraBlock == null) {
                    return;
                }

                ShareCompat.IntentBuilder
                        .from(BlockDetailActivity.this)
                        .setType("text/plain")
                        .setText("https://blockstream.info/block/" + esploraBlock.getHash())
                        .startChooser();
            }
        });

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // restore EsploraBlock object from intent
            final Intent intent = getIntent();
            if (!intent.hasExtra("block")) {
                return;
            }

            esploraBlock = intent.getParcelableExtra("block");

            // create the detail fragment and add it to the activity
            final Bundle arguments = new Bundle();
            arguments.putParcelable("block", esploraBlock);

            final BlockDetailFragment fragment = new BlockDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.block_detail_container, fragment)
                    .commit();
        } else {
            // restore EsploraBlock object from parcel
            esploraBlock = savedInstanceState.getParcelable("block");
        }
    }

    /**
     * Called when an options item was selected by the user (i.e. the toolbar back button).
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, BlockListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the state of this instance is saved (i.e. rotating the screen). Adds block data
     * in the {@link Parcelable} format to the state, in order to
     *
     * @param outState Instance state to be saved.
     */
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putParcelable("block", esploraBlock);
        super.onSaveInstanceState(outState);
    }
}
