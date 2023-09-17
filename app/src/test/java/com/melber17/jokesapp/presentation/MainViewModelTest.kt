package com.melber17.jokesapp.presentation

import com.melber17.jokesapp.data.Error
import com.melber17.jokesapp.data.Joke
import com.melber17.jokesapp.data.Repository
import com.melber17.jokesapp.data.cache.JokeResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: MainViewModel
    private lateinit var toFavoriteMapper: FakeMapper
    private lateinit var toBaseMapper: FakeMapper
    private lateinit var jokeUICallback: FakeJokeUiCallback
    private lateinit var dispatcherList: DispatcherList


    @Before
    fun setUp() {
        repository = FakeRepository()
        toFavoriteMapper = FakeMapper(true)
        toBaseMapper = FakeMapper(false)
        jokeUICallback = FakeJokeUiCallback()
        dispatcherList = FakeDispatchers()

        viewModel = MainViewModel(repository, toFavoriteMapper, toBaseMapper, FakeDispatchers())
        viewModel.init(jokeUICallback)
    }

    @Test
    fun test_successful_not_favorite() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                joke = FakeJoke("test_type", "fakeText", "test_punchline", 12),
                toFavorite = false,
                successful = true,
                errorMessage = "test error message"
            )
        viewModel.getJoke()
        val expectedText = "fakeText_test_punchline"
        val expectedId = 12

        assertEquals(expectedText, jokeUICallback.provideTextList[0])
        assertEquals(expectedId, jokeUICallback.provideIconResIdList[0])

        assertEquals(1, jokeUICallback.provideTextList.size)
        assertEquals(1, jokeUICallback.provideIconResIdList.size)
    }

    @Test
    fun test_successful_favorite() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                joke = FakeJoke("test_type", "fakeText", "test_punchline", 12),
                toFavorite = true,
                successful = true,
                errorMessage = "test error message"
            )
        viewModel.getJoke()
        val expectedText = "fakeText_test_punchline"
        val expectedId = 13

        assertEquals(expectedText, jokeUICallback.provideTextList[0])
        assertEquals(expectedId, jokeUICallback.provideIconResIdList[0])

        assertEquals(1, jokeUICallback.provideTextList.size)
        assertEquals(1, jokeUICallback.provideIconResIdList.size)
    }

    @Test
    fun test_not_successful() {
        repository.returnFetchJokeResult =
            FakeJokeResult(
                joke = FakeJoke("test_type", "fakeText", "test_punchline", 12),
                toFavorite = false,
                successful = false,
                errorMessage = "test error message"
            )
        viewModel.getJoke()
        val expectedText = "test error message\n"
        val expectedId = 0

        assertEquals(expectedText, jokeUICallback.provideTextList[0])
        assertEquals(expectedId, jokeUICallback.provideIconResIdList[0])

        assertEquals(1, jokeUICallback.provideTextList.size)
        assertEquals(1, jokeUICallback.provideIconResIdList.size)
    }

    @Test
    fun test_change_joke_status() {
        repository.returnChangeJokeStatus = FakeJokeUi("testText","testPunchline", 15, false)
        viewModel.changeJokeStatus()


        val expectedText = "testText_testPunchline"
        val expectedId = 15

        assertEquals(expectedText, jokeUICallback.provideTextList[0])
        assertEquals(expectedId, jokeUICallback.provideIconResIdList[0])

        assertEquals(1, jokeUICallback.provideTextList.size)
        assertEquals(1, jokeUICallback.provideIconResIdList.size)
    }

    @Test
    fun test_choose_favorite() {
        viewModel.chooseFavorite(true)

        assertEquals(true, repository.chooseFavoritesList[0])
        assertEquals(1, repository.chooseFavoritesList.size)

        viewModel.chooseFavorite(false)

        assertEquals(false, repository.chooseFavoritesList[1])
        assertEquals(2, repository.chooseFavoritesList.size)
    }
}

private class FakeJokeUiCallback : JokeUICallback {
    val provideTextList = mutableListOf<String>()
    override fun provideText(text: String) {
        provideTextList.add(text)
    }

    val provideIconResIdList = mutableListOf<Int>()
    override fun provideIconResId(iconResId: Int) {
        provideIconResIdList.add(iconResId)
    }

}

private class FakeDispatchers : DispatcherList {
    private val dispatcher = TestCoroutineDispatcher()
    override fun io(): CoroutineDispatcher = dispatcher

    override fun ui(): CoroutineDispatcher = dispatcher

}

private class FakeMapper(private val toFavorite: Boolean) : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return FakeJokeUi(mainText, punchline, id, toFavorite)
    }

}

private data class FakeJokeUi(
    private val text: String,
    private val punchline: String,
    private val id: Int,
    private val toFavorite: Boolean
) :
    JokeUi.Abstract(text, punchline, id) {
        override fun show(jokeUiCallback: JokeUICallback) = with(jokeUiCallback) {
            provideText(text+ "_" + punchline)
            provideIconResId(if (toFavorite) id + 1 else id)
        }
    }

private data class FakeJoke(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int
) : Joke {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return mapper.map(type, mainText, punchline, id)
    }
}


private data class FakeJokeResult(
    private val joke: Joke,
    private val toFavorite: Boolean,
    private val successful: Boolean,
    private val errorMessage: String
) : JokeResult {
    override fun toFavorite(): Boolean = toFavorite
    override fun isSuccessful(): Boolean = successful

    override fun errorMessage(): String = errorMessage
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return joke.map(mapper)
    }

}

private

class FakeRepository : Repository<JokeUi, Error> {
    var returnFetchJokeResult: JokeResult? = null
    override suspend fun fetch(): JokeResult {
        return returnFetchJokeResult!!
    }

    var returnChangeJokeStatus: JokeUi? = null

    override suspend fun changeJokeStatus(): JokeUi = returnChangeJokeStatus!!

    var chooseFavoritesList = mutableListOf<Boolean>()
    override fun chooseFavorites(isFavorite: Boolean) {
        chooseFavoritesList.add(isFavorite)
    }

}