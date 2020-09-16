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
import dev.iotarho.artplace.app.model.artists.Artist;
import dev.iotarho.artplace.app.model.artworks.Artwork;
import dev.iotarho.artplace.app.ui.artistdetail.ArtistViewHolder;

public class TrendyArtistsAdapter extends PagedListAdapter<Artist, RecyclerView.ViewHolder> {

    private OnArtistClickHandler clickHandler;
    private List<Artist> artistList;

    public TrendyArtistsAdapter(OnArtistClickHandler clickHandler) {
        super(Artist.DIFF_CALLBACK);
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.artwork_by_artist_item, parent, false);
        return new ArtistViewHolder(view, clickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArtistViewHolder) {
            Artist artist = artistList.get(position);
            ((ArtistViewHolder) holder).bindTo(artist);
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
