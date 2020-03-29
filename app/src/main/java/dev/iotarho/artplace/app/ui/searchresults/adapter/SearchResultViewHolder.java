package dev.iotarho.artplace.app.ui.searchresults.adapter;

import android.util.Log;
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
import dev.iotarho.artplace.app.utils.Utils;

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
    private OnResultClickListener mClickHandler;

    public SearchResultViewHolder(View itemView, OnResultClickListener callback) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        mClickHandler = callback;
    }

    public void bindTo(Result result, int position) {

        currentResult = result;
        if (result != null) {

            if (result.getLinks() != null) {
                LinksResult linksResult = result.getLinks();
                Thumbnail thumbnail = linksResult.getThumbnail();
                if (thumbnail != null) {
                    String thumbnailPathString = thumbnail.getHref();
                    Log.d(TAG, "Current thumbnail string: " + thumbnailPathString);

                    Picasso.get()
                            .load(thumbnailPathString)
                            .placeholder(R.color.color_surface)
                            .error(R.color.color_error)
                            .into(searchThumbnail);
                }
            }

            String titleString = result.getTitle();
            searchTitle.setText(titleString);
            Log.d(TAG, "Current search title: " + titleString);

            String typeString = result.getType();
            searchType.setText(typeString);
            Log.d(TAG, "Current search type: " + typeString);
        }
    }

    @Override
    public void onClick(View v) {
        mClickHandler.onResultClick(currentResult);
    }
}