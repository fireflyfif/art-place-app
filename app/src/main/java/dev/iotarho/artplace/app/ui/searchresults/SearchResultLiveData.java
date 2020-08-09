package dev.iotarho.artplace.app.ui.searchresults;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

// Custom Mediator Live Data is used in order to use two different mutables: search query and a type
// https://stackoverflow.com/a/49580000/8132331
public class SearchResultLiveData extends MediatorLiveData<Pair<String, String>> {

    public SearchResultLiveData(LiveData<String> searchKey, LiveData<String> typeKey) {
        addSource(searchKey, first -> setValue(Pair.create(first, typeKey.getValue())));
        addSource(typeKey, second -> setValue(Pair.create(searchKey.getValue(), second)));
    }
}

