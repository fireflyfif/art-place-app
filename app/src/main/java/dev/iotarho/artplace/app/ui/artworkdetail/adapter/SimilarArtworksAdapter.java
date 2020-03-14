package dev.iotarho.artplace.app.ui.artworkdetail.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.artworks.Artwork;

public class SimilarArtworksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ArtworksByArtistAdapter.class.getSimpleName();

    private List<Artwork> mArtworkList;

    public SimilarArtworksAdapter(List<Artwork> artworkList) {
        mArtworkList = artworkList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.similar_artwork_item, parent, false);
        return new ArtworksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Artwork artwork = mArtworkList.get(position);
        ((ArtworksViewHolder) holder).bindTo(artwork);
    }

    @Override
    public int getItemCount() {
        if (mArtworkList == null) {
            return 0;
        }
        Log.d(TAG, "Size of the Similar Artworks list: " + mArtworkList);
        return mArtworkList.size();
    }
}
