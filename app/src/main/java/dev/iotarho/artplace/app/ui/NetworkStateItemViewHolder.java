package dev.iotarho.artplace.app.ui;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.iotarho.artplace.app.R;
import dev.iotarho.artplace.app.callbacks.OnRefreshListener;
import dev.iotarho.artplace.app.utils.NetworkState;

public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnRefreshListener refreshHandler;

    @BindView(R.id.network_state_layout)
    LinearLayout networkLayout;
    @BindView(R.id.network_state_pb)
    ProgressBar progressBar;
    @BindView(R.id.network_state_error_msg)
    TextView errorMessage;
    @BindView(R.id.network_state_refresh_bt)
    ImageButton refreshButton;

    public NetworkStateItemViewHolder(View itemView, OnRefreshListener refreshListener) {
        super(itemView);
        refreshHandler = refreshListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindView(NetworkState networkState) {
        if (networkState != null) {
            switch (networkState.getStatus()) {
                case RUNNING:
                    networkLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                    refreshButton.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    networkLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.GONE);
                    refreshButton.setVisibility(View.GONE);
                    break;
                case FAILED:
                    networkLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    errorMessage.setVisibility(View.VISIBLE);
                    refreshButton.setVisibility(View.VISIBLE);
                    refreshButton.setOnClickListener(this); // Set the click listener here
                    break;
            }
        } else {
            networkLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
            refreshButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        refreshHandler.onRefreshConnection();
    }
}
