package com.example.marvelapp.presentation.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("MaxLineLength")
@Composable
fun AboutScreen(
    onNextClick: () -> Unit
) {
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text (
//                        text = "About",
//                        color = Color.White,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                        },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(id = com.google.android.material.R.color.design_default_color_primary)
//                )
//            )
//        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Marvel App")
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = { onNextClick() }) {
                Text(text = "Teste Navigation Compose")
            }
//            Spacer(modifier = Modifier.height(50.dp))
//            Text (text = "New About", modifier = Modifier.padding(paddingValues))
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen(
        onNextClick = {}
    )
}