package candle.bitcoin.explorer;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A {@link RecyclerView} OnScrollListener that calls the provided onLoadMore method when the defined
 * threshold of minimum visible items got surpassed.
 */
abstract class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener {

    // minimum amount of items below the current scroll position, before calling onLoadMore
    private int visibleThreshold = 10;

    // amount of items after the last onLoadMore method call
    private int itemCountLast = 0;

    // indicates if currently more data are being loaded
    private boolean isLoading = true;

    // RecyclerViews LayoutManager
    private RecyclerView.LayoutManager layoutManager;

    // LinearLayout representation of the RecyclerViews LayoutManager
    private LinearLayoutManager layoutManagerLinear;

    // callback provided as argument in the onLoadMore method, indicating the loading is done
    final private LoadingFinishedCallback loadingFinishedCallback = new LoadingFinishedCallback();

    /**
     * Default constructor for RecyclerViewEndlessScrollListener. The visibleThreshold default value of
     * 10 items will be used.
     */
    public RecyclerViewEndlessScrollListener() {
    }

    /**
     * Constructor with the option to set a custom visible threshold.
     *
     * @param visibleThreshold Minimum amount of items below the current scroll position. When this
     *                         threshold is surpassed, the given onLoadMore method is called.
     */
    public RecyclerViewEndlessScrollListener(final int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    /**
     * Called several times while the user scrolls the {@link RecyclerView} this listener instance
     * has been added to.
     *
     * @param recyclerView The {@link RecyclerView} listening to.
     * @param x Current value of the horizontal scrolling position.
     * @param y Current value of the vertical scrolling position.
     */
    @Override
    public void onScrolled(final RecyclerView recyclerView, final int x, final int y) {
        // get layout manager from RecyclerView, if not already assigned
        if (layoutManager == null || layoutManagerLinear == null) {
            layoutManager = recyclerView.getLayoutManager();
            layoutManagerLinear = (LinearLayoutManager) layoutManager;
        }

        // retrieve current item count and last visible item position
        assert layoutManager != null;
        final int itemCountCurrent = layoutManager.getItemCount();

        // initialize when the item amount changed from 0 to >= 1
        if (isLoading && itemCountLast == 0 && itemCountCurrent > 0) {
            setLoading(false);
        }

        // load more
        checkLoadMore();
    }

    /**
     * Calls the onLoadMore method if not loading and the visibleThreshold got surpassed.
     */
    private void checkLoadMore() {
        // skip if it's currently loading
        if(isLoading) {
            return;
        }

        // check if the visibleThreshold got surpassed
        final int itemPositionLast = layoutManagerLinear.findLastVisibleItemPosition();
        if((itemPositionLast + visibleThreshold) < layoutManager.getItemCount()) {
            return;
        }

        // load more
        setLoading(true);
        onLoadMore(loadingFinishedCallback);
    }

    /**
     * Set the current loading state.
     *
     * @param loadingState Desired loading state.
     */
    private void setLoading(final boolean loadingState) {
        isLoading = loadingState;
        itemCountLast = layoutManager.getItemCount();

        // might need to load more
        checkLoadMore();
    }

    /**
     * Abstraction method that will be called every time the visibility threshold got surpassed.
     * @param loadingFinishedCallback Callback that needs to be invoked after the loading is done.
     *
     */
    protected abstract void onLoadMore(final LoadingFinishedCallback loadingFinishedCallback);

    /**
     * An instance of this subclass is passed as argument to the onLoadMore method.
     */
    final class LoadingFinishedCallback {
        /**
         * Callback method that must be invoked after the loading is finished.
         */
        void loadingDone() {
            setLoading(false);
        }
    }

}
