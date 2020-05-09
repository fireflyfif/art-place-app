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

package dev.iotarho.artplace.app.ui.searchresults.datasource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;


import dev.iotarho.artplace.app.model.search.Result;
import dev.iotarho.artplace.app.repository.ArtsyRepository;

public class SearchDataSourceFactory extends DataSource.Factory<Long, Result> {

    private MutableLiveData<SearchDataSource> searchDataSourceMutableLiveData;
    private SearchDataSource searchDataSource;
    private String searchKey;
    private String typeKey;
    private ArtsyRepository repository;

    public SearchDataSourceFactory(ArtsyRepository repository, String searchKey) {
        this.repository = repository;
        this.searchKey = searchKey;
//        this.typeKey = typeKey;

        searchDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, Result> create() {
        searchDataSource = new SearchDataSource(repository, searchKey, typeKey);
        searchDataSourceMutableLiveData.postValue(searchDataSource);

        return searchDataSource;
    }

    public MutableLiveData<SearchDataSource> getSearchDataSourceMutableLiveData() {
        return searchDataSourceMutableLiveData;
    }
}
