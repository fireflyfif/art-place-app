/*
 * PROJECT LICENSE
 * This project was submitted by Iva Ivanova as part of the Nanodegree at Udacity.
 *
 * According to Udacity Honor Code we agree that we will not plagiarize (a form of cheating) the work of others. :
 * Plagiarism at Udacity can range from submitting a project you didn’t create to copying code into a program without
 * citation. Any action in which you misleadingly claim an idea or piece of work as your own when it is not constitutes
 * plagiarism.
 * Read more here: https://udacity.zendesk.com/hc/en-us/articles/360001451091-What-is-plagiarism-
 *
 * MIT License
 *
 * Copyright (c) 2018 Iva Ivanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.example.android.artplace.ui.ArtistActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.artplace.R;
import com.example.android.artplace.model.Artists.Artist;
import com.example.android.artplace.repository.ArtsyRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistDetailActivity extends AppCompatActivity {

    private static final String ARTWORK_ID_KEY = "artwork_id";
    private static final String ARTIST_LINK_KEY = "artist_link";

    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.artist_home)
    TextView artistOrigin;
    @BindView(R.id.artist_image)
    ImageView artistImage;

    private ArtistsViewModel mArtistViewModel;
    private ArtistsViewModelFactory mArtistsViewModelFactory;
    private ArtsyRepository mRepository;
    private List<Artist> mArtistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        ButterKnife.bind(this);

        // TODO: Get the ID from the clicked artwork from the received Intent
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(ARTWORK_ID_KEY)) {
                String receivedArtworkId = getIntent().getStringExtra(ARTWORK_ID_KEY);

                mArtistViewModel = ViewModelProviders.of(this, new ArtistsViewModelFactory(mRepository))
                        .get(ArtistsViewModel.class);
                mArtistViewModel.init(receivedArtworkId);

                mArtistViewModel.getArtist().observe(this, new Observer<List<Artist>>() {
                    @Override
                    public void onChanged(@Nullable List<Artist> artists) {
                        if (artists != null) {
                            for (int i = 0; i < artists.size(); i++) {
                                Artist artistCurrent = artists.get(i);
                                String artistNameString = artistCurrent.getName();
                                artistName.setText(artistNameString);
                            }

                        }
                    }
                });

                /*mArtistViewModel.getArtist().observe(this, new Observer<Artist>() {
                    @Override
                    public void onChanged(@Nullable Artist artist) {
                        if (artist != null) {
                            artistName.setText(artist.getName());
                            artistOrigin.setText(artist.getHometown());
                        }

                    }
                });*/
            }


            if (getIntent().hasExtra(ARTIST_LINK_KEY)) {
                String artistLink = getIntent().getStringExtra(ARTIST_LINK_KEY);
            }
        }

        // TODO: Configure the ViewModel
    }
}
