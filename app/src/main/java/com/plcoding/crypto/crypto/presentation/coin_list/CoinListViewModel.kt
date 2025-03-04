package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {
    private val _state = MutableStateFlow(CoinListState())
    val state = _state
            .onStart { loadCoins() } // start execution when flow collection starts
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),  // this line makes the code executions
                // until the last subscriber becomes inactive for more than 5 seconds which is the
                // time less than the recreation of activity takes, this will save the loading time
                // and data
                CoinListState()
            )
    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }

            is CoinListAction.OnRefresh   -> {
                loadCoins()
            }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoin = coinUi) }

        viewModelScope.launch {
            coinDataSource
                    .getCoinHistory(
                        coinId = coinUi.id,
                        start = ZonedDateTime.now().minusDays(5),
                        end = ZonedDateTime.now()
                    )
                    .onSuccess { history ->
                        val dataPoints = history
                                .sortedBy { it.dateTime }
                                .map {
                                    DataPoint(
                                        x = it.dateTime.hour.toFloat(),
                                        y = it.priceUsd.toFloat(),
                                        xLabel = DateTimeFormatter
                                                .ofPattern("ha\nM/d")
                                                .format(it.dateTime)
                                    )
                                }
                        _state.update {
                            it.copy(
                                selectedCoin = it.selectedCoin?.copy(
                                    coinPriceHistory = dataPoints
                                )
                            )
                        }
                    }
                    .onError { error ->
                        _events.send(CoinListEvent.Error(error))
                    }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update {
                it.copy( // update function is used to update the state in a safe manner
                    isLoading = true
                )
            }
            coinDataSource.getCoins()
                    .onSuccess { coins ->
                        _state.update { it ->
                            it.copy(
                                isLoading = false,
                                coinList = coins.map { it.toCoinUi() }
                            )
                        }
                    }
                    .onError { error ->
                        _state.update {
                            it.copy(isLoading = false)
                        }
                        _events.send(CoinListEvent.Error(error))
                    }
        }
    }
}
//  here we used CoinDataSource which is a abstract class not a hard coded implementation because
//  we are following the mvi pattern and it enables us to pass different type of instances of
//  datasource whenever we need just like for testing