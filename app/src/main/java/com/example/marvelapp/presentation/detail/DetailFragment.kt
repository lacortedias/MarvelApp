package com.example.marvelapp.presentation.detail

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.marvelapp.R
import com.example.marvelapp.databinding.FragmentDetailBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.extensions.showShortToast
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
        viewModel.charactersCategories.load(detailViewArg.characterId)
        viewModel.charactersCategories.state.observe(viewLifecycleOwner) { uiState ->
            binding.flipperDetail.displayedChild = when (uiState) {
                CharactersUiActionStateLiveData.UiState.Loading -> FLIPPER_CHILD_POSITION_LOADING
                is CharactersUiActionStateLiveData.UiState.Success -> {
                    binding.recyclerParentDetail.run {
                        setHasFixedSize(true)
                        adapter = DetailParentAdapter(uiState.detailParentList, imageLoader)
                    }
                    FLIPPER_CHILD_POSITION_DETAIL
                }
                CharactersUiActionStateLiveData.UiState.Error -> {
                    binding.includeErrorView.buttonRetry.setOnClickListener {
                        viewModel.charactersCategories.load(args.detailViewArg.characterId)
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

            binding.imageFavoriteIcon.setOnClickListener {
                update(detailViewArg)

                val drawable = VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_favorite_checked,
                    null
                ) as Drawable

// Defina a nova cor usando ContextCompat.getColor() ou resources.getColor()
                val newColor = ContextCompat.getColor(requireContext(), R.color.black)

// Define a nova cor usando setTint()
                drawable.setTint(newColor)

// Define o drawable atualizado como a imagem de um ImageView
                binding.imageFavoriteIcon.setImageDrawable(drawable)
//                path.setTint(ContextCompat.getColor(requireContext(), R.color.yellow))
//                binding.imageFavoriteIcon.backgroundTintList = ContextCompat.getColorStateList(
//                    requireContext(), R.color.black
//                )
            }

            state.observe(viewLifecycleOwner) { uiState ->
                binding.flipperFavorite.displayedChild = when (uiState) {
                    FavoritesUiActionStateLiveData.UiState.Loading -> FLIPPER_FAVORITE_CHILD_POSITION_LOADING
                    is FavoritesUiActionStateLiveData.UiState.Icon -> {
                        binding.imageFavoriteIcon.setImageResource(uiState.icon)
                        FLIPPER_FAVORITE_CHILD_POSITION_SUCCESS_OR_ERROR_IMAGE
                    }
                    is FavoritesUiActionStateLiveData.UiState.Error -> {
                        showShortToast(uiState.messageResId)
                        FLIPPER_FAVORITE_CHILD_POSITION_SUCCESS_OR_ERROR_IMAGE
                    }
                }
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
        private const val FLIPPER_FAVORITE_CHILD_POSITION_SUCCESS_OR_ERROR_IMAGE = 0
        private const val FLIPPER_FAVORITE_CHILD_POSITION_LOADING = 1
    }

}