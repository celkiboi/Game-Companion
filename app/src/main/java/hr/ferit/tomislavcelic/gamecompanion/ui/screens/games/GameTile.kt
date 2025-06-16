package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController

@Composable
fun GameTile(game: Game, nav: NavHostController) {
    val ctx = LocalContext.current

    val resId = remember(game.key) {
        val name = "ic_game_${game.key}"
        val id   = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        if (id != 0) id else hr.ferit.tomislavcelic.gamecompanion.R.drawable.ic_game_default
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable {
                nav.navigate("game/${game.key}/${Uri.encode(game.name)}")
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(resId),
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    game.name,
                    style     = MaterialTheme.typography.labelLarge,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    modifier  = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}