package dev.iotarho.artplace.app.ui.searchdetail;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder;
import dev.iotarho.artplace.app.utils.NetworkState;

public class SearchArtistListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = SearchArtistListAdapter.class.getSimpleName();

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState mNetworkState;

    private List<Result> artistList;

    public SearchArtistListAdapter(List<Result> artistList) {
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.similar_artwork_item, parent, false);
        return new SearchContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchContentViewHolder) {
            Result artistResult = artistList.get(position);
            ((SearchContentViewHolder) holder).bindTo(artistResult);
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(mNetworkState);
        }
    }

    @Override
    public int getItemCount() {
        if (artistList == null) {
            return 0;
        }
        Log.d(TAG, "Size of the Similar Artworks list: " + artistList.size());
        return artistList.size();
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }
}