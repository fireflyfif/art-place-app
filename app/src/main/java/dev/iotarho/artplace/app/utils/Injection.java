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

package dev.iotarho.artplace.app.utils;

import dev.iotarho.artplace.app.repository.ArtsyRepository;
import dev.iotarho.artplace.app.ui.artworks.ArtworksFragmentViewModelFactory;
import dev.iotarho.artplace.app.ui.searchresults.SearchFragmentViewModelFactory;

public class Injection {

    private static ArtsyRepository provideRepository(TokenManager tokenManager) {
        return ArtsyRepository.getInstance(tokenManager);
    }

    public static ArtworksFragmentViewModelFactory provideArtworksViewModelFactory(TokenManager tokenManager) {
        ArtsyRepository repository = provideRepository(tokenManager);
        return new ArtworksFragmentViewModelFactory(repository);
    }

    public static SearchFragmentViewModelFactory provideSearchViewModelFactory(
            TokenManager tokenManager,
            String queryWord,
            String typeWord) {
        ArtsyRepository repository = provideRepository(tokenManager);
        return new SearchFragmentViewModelFactory(repository, queryWord, typeWord);
    }
}
