package com.example.marvelapp.presentation.detail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.marvelapp.R
import com.example.marvelapp.databinding.FragmentDetailBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.extensions.showShortToast
import com.example.marvelapp.presentation.ui.FavoriteButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    private val args by navArgs<DetailFragmentArgs>()

    @Inject
    lateinit var imageLoader: ImageLoader

    private var detailParentAdapter: DetailParentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val detailViewArg = args.detailViewArg
        binding.imageCharacter.run {
            transitionName = detailViewArg.name
            imageLoader.load(
                this,
                detailViewArg.imageUrl
            )
        }
        setSharedElementTransitionOnEnter()

        loadCategoriesAndObserveCharacterUiState(detailViewArg)
        setAndObserveFavoriteUiState(detailViewArg)
    }

    private fun loadCategoriesAndObserveCharacterUiState(detailViewArg: DetailViewArg) {
        viewModel.charactersCategories.load(detailViewArg.characterId, viewModel.offset)
        viewModel.charactersCategories.state.observe(viewLifecycleOwner) { uiState ->
            binding.flipperDetail.displayedChild = when (uiState) {
                CharactersUiActionStateLiveData.UiState.Loading -> FLIPPER_CHILD_POSITION_LOADING
                is CharactersUiActionStateLiveData.UiState.Success -> {
                    detailParentAdapter = DetailParentAdapter(uiState.detailParentList, imageLoader) {
                        viewModel.offset += INCREMENT_OFFSET
                        viewModel.charactersCategories.loadMoreCategories(detailViewArg.characterId, viewModel.offset)
                    }
                    binding.recyclerParentDetail.run {
                        setHasFixedSize(true)
                        adapter = detailParentAdapter
                    }

                    FLIPPER_CHILD_POSITION_DETAIL
                }

                is CharactersUiActionStateLiveData.UiState.SuccessUpdateChildList -> {

                    if (uiState.detailParentList.isNotEmpty()) {
                        uiState.detailParentList.map {
                            if(it.detailChildList.isNotEmpty()){
                                detailParentAdapter?.addItems(it.detailChildList)
                            }
                        }
                    } else {
                        showShortToast(R.string.common_no_more_results_found)
                    }

                    FLIPPER_CHILD_POSITION_DETAIL
                }

                CharactersUiActionStateLiveData.UiState.Error -> {
                    binding.includeErrorView.buttonRetry.setOnClickListener {
                        viewModel.charactersCategories.retry(args.detailViewArg.characterId, viewModel.offset)
                    }
                    FLIPPER_CHILD_POSITION_ERROR
                }

                CharactersUiActionStateLiveData.UiState.Empty -> FLIPPER_CHILD_POSITION_EMPTY
            }
        }
    }

    private fun setAndObserveFavoriteUiState(detailViewArg: DetailViewArg) {
        viewModel.favoritesCategories.run {
            checkFavorite(detailViewArg.characterId)

            binding.buttonFavorite.setContent {
                val uiState by state.observeAsState(FavoritesUiActionStateLiveData.UiState.Icon(favoriteCharacter))
                FavoriteButton(
                    uiState = uiState,
                    onClick = { update(detailViewArg) },
                )
            }
        }
    }

    // Define a animação da transição como "move"
    private fun setSharedElementTransitionOnEnter() {
        TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move).apply {
                sharedElementEnterTransition = this
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val FLIPPER_CHILD_POSITION_LOADING = 0
        private const val FLIPPER_CHILD_POSITION_DETAIL = 1
        private const val FLIPPER_CHILD_POSITION_ERROR = 2
        private const val FLIPPER_CHILD_POSITION_EMPTY = 3
        private const val INCREMENT_OFFSET = 20
    }

}