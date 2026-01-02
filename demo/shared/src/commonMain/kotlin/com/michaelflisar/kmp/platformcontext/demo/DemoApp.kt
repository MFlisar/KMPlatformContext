package com.michaelflisar.kmp.platformcontext.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.democomposables.layout.DemoCollapsibleRegion
import com.michaelflisar.democomposables.layout.DemoColumn
import com.michaelflisar.democomposables.layout.DemoRegion
import com.michaelflisar.democomposables.layout.rememberDemoExpandedRegions
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.kmp.platformcontext.PlatformIOContext
import com.michaelflisar.kmp.platformcontext.shared.resources.Res
import com.michaelflisar.kmp.platformcontext.shared.resources.app_name
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp(
    platform: String
) {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            topBar = {
                val appName = stringResource(Res.string.app_name)
                TopAppBar(
                    title = { Text(appName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            DemoContent(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                platform = platform,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun DemoContent(
    modifier: Modifier,
    platform: String,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val regionState = rememberDemoExpandedRegions(ids = listOf(1, 2))

    val context = PlatformContextProvider.get()
    val ioContext = PlatformIOContext()

    DemoColumn(
        modifier = modifier.padding(all = 16.dp)
    ) {
        DemoRegion("Platform: $platform")
        DemoCollapsibleRegion(
            title = "Demos", regionId = 1, state = regionState
        ) {
            Text("PlatformContext::simpleName = " + context::class.simpleName)
            Text("ioContext::simpleName = " + ioContext::class.simpleName)
        }
    }
}