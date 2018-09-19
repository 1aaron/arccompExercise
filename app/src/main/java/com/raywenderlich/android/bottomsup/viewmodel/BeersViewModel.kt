package com.raywenderlich.android.bottomsup.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.raywenderlich.android.bottomsup.App
import com.raywenderlich.android.bottomsup.model.Beer
import com.raywenderlich.android.bottomsup.model.BeerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeersViewModel: ViewModel() {
    private val interactor by lazy { App.component.breweryInteractor() }
    val errorData = MutableLiveData<Boolean>()
    val loadingData = MutableLiveData<Boolean>()
    val pageData = MutableLiveData<Int>()
    val beerData = MutableLiveData<List<Beer>>()

    fun getBeers() {
        interactor.getBeers(pageData.value ?: 1,beersCallback())
    }
    private fun beersCallback() = object: Callback<BeerResponse> {
        override fun onFailure(call: Call<BeerResponse>?, t: Throwable?){
            loadingData.value = false
            errorData.value = true
        }

        override fun onResponse(call: Call<BeerResponse>, response: Response<BeerResponse>) {
            loadingData.value = false
            errorData.value = false
            response.body()?.run {
                updateData(this)
            }
        }
    }

    fun onRefresh(){
        pageData.value = 1
        getBeers()
    }
    private fun updateData(data: BeerResponse) {
        pageData.value = data.currentPage + 1
        beerData.value = data.beers
    }
}