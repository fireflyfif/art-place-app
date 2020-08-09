package dev.iotarho.artplace.app.ui.searchresults.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnResultClickListener;
import dev.iotarho.artplace.app.model.Thumbnail;
import dev.iotarho.artplace.app.model.search.LinksResult;
import dev.iotarho.artplace.app.model.search.Result;

public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = SearchResultViewHolder.class.getSimpleName();

    @BindView(R.id.search_cardview)
    CardView cardView;
    @BindView(R.id.search_thumbnail)
    ImageView searchThumbnail;
    @BindView(R.id.search_title)
    TextView searchTitle;
    @BindView(R.id.search_type)
    TextView searchType;

    private Result currentResult;
    private OnResultClickListener clickHandler;

    public SearchResultViewHolder(View itemView, OnResultClickListener callback) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        clickHandler = callback;
    }

    public void bindTo(Result result) {
        currentResult = result;

        LinksResult linksResult = result.getLinks();
        Thumbnail thumbnail = linksResult.getThumbnail();
        String thumbnailPathString = thumbnail.getHref();

        Picasso.get()
                .load(thumbnailPathString)
                .placeholder(R.color.color_surface)
                .error(R.color.color_error)
                .into(searchThumbnail);

        String titleString = result.getTitle();
        searchTitle.setText(titleString);

        String typeString = result.getType();
        searchType.setText(typeString);
    }

    @Override
    public void onClick(View v) {
        clickHandler.onResultClick(currentResult);
    }
}