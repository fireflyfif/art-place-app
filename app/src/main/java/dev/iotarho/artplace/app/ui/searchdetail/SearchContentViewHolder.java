package dev.iotarho.artplace.app.ui.searchdetail;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;


public class SearchContentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.similar_artwork_thumbnail)
    ImageView similarArtworkThumbnail;

    @BindView(R.id.similar_artwork_title)
    TextView similarTitle;

    @BindView(R.id.similar_artwork_artist)
    TextView similarArtist;

    public SearchContentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindTo(Result searchArtist) {
        if (searchArtist != null) {
            String title = searchArtist.getTitle();
            similarTitle.setText(title);
            similarArtist.setText(searchArtist.getType());
            LinksResult links = searchArtist.getLinks();
            Thumbnail thumbnail = links.getThumbnail();
            String imageThumbnailString = thumbnail.getHref();
            Picasso.get()
                        .load(Uri.parse(imageThumbnailString))
                        .placeholder(R.color.color_primary)
                        .error(R.color.color_error)
                        .into(similarArtworkThumbnail);
        }
    }
}