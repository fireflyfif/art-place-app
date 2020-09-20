package dev.iotarho.artplace.app.ui.artworks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnArtistClickHandler;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.ui.NetworkStateItemViewHolder;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistViewHolder;
import dev.iotarho.artplace.app.utils.NetworkState;

public class TrendyArtistsAdapter extends PagedListAdapter<Artist, RecyclerView.ViewHolder> {

    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;

    private NetworkState networkState;
    private OnArtistClickHandler clickHandler;
    private OnRefreshListener onRefreshListener;
    private List<Artist> artistList;

    public TrendyArtistsAdapter(OnArtistClickHandler clickHandler,  OnRefreshListener onRefreshListener) {
        super(Artist.DIFF_CALLBACK);
        this.clickHandler = clickHandler;
        this.onRefreshListener = onRefreshListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            View view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(view, onRefreshListener);
        } else {
            View view = layoutInflater.inflate(R.layout.artwork_by_artist_item, parent, false);
            return new ArtistViewHolder(view, clickHandler);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArtistViewHolder) {
            Artist artist = getItem(position);
            ((ArtistViewHolder) holder).bindTo(artist);
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    @Nullable
    @Override
    protected Artist getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = networkState;
        boolean previousExtraRow = hasExtraRow();
        networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();

        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Artist> previousList, @Nullable PagedList<Artist> currentList) {
        super.onCurrentListChanged(previousList, currentList);

        artistList = currentList;
        notifyDataSetChanged();
    }

    public void swapCatalogue(List<Artist> artistList) {
        this.artistList = artistList;
        notifyDataSetChanged();
    }
}
