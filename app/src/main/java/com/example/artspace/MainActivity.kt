package com.example.artspace

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.teal_700, null)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //navigation controller
            val navController = rememberNavController()
            ArtSpaceTheme {
                //initialize the navigation host NavHost
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route + "/{id}"
                ) {
                    //Home page composable
                    composable(
                        Screen.Home.route + "/{id}", arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) {

                        HomePage(navController = navController)
                    }

                    //Artist page composable
                    composable(
                        Screen.Artist.route + "/{id}", arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                        })
                    ) {

                        ArtistPage(navController = navController)
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomePage(navController: NavController) {

        var currentArtWork by remember {
            mutableIntStateOf(
                navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
            )
        }

        val art = DataSource.arts[currentArtWork]

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B5D1E),
                        titleContentColor = Color(0xFF000000)
                    )
                )
            }) { innerPadding ->

            /**The children without weight (a) are measured first.After tha,t the remaining space in the
             * column is spread among the children with weights (b), proportional to their weight.
             * If you have 2 children weight weight 1f, each will take half the remaining space.
             * **/

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) //children with weight (b)
                ) {
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_extra_large)))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ArtWall(currentArtWork, art.artworkImageId, art.descriptionId, navController)
                    }
                }

                // (a) children without weight.
                ArtDescriptor(art.titleId, art.artistId, art.yearId)
                DisplayController(currentArtWork){
                    currentArtWork = if (it !in 0 ..<DataSource.arts.size) 0 else it
                }
            }


        }
    }

    @Composable
    fun DisplayController(currentArtWork: Int, updateCurrent: (Int) -> Unit) {
        //Home Page section C

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF92E873), // Use ARGB format (0xFF is the alpha)
                contentColor = Color(0xFF000000),   // Black
                disabledContentColor = Color(0xFF100B00),
                disabledContainerColor = Color(0xFFA5CBC3)
            ),enabled = currentArtWork != 0, onClick = {
                if(currentArtWork !=0 ) {
                    updateCurrent(currentArtWork - 1)
                }
            }) {
                Text(text = stringResource(id = R.string.previous))
            }

            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF92E873), // Use ARGB format (0xFF is the alpha)
                contentColor = Color(0xFF000000),
                disabledContentColor = Color(0xFF100B00),
                disabledContainerColor = Color(0xFFA5CBC3)
            ), enabled = currentArtWork != DataSource.arts.size - 1, onClick = {
                if(currentArtWork != DataSource.arts.size - 1) {
                    updateCurrent(currentArtWork + 1)
                }
            }) {
                Text(text = stringResource(id = R.string.next))
            }
        }

            /** note: The buttons should be disabled is there is no previous or next
            artwork to navigate to. Use the following code for that:
             enabled = current != 0 for previous
             enabled = current != DataSource.arts.size - 1 // for next
            */

        /** You can use the following code to navigate to the previous or next artwork
         *  updateCurrent(current - 1) this is for the previous button
         *   updateCurrent(current + 1) this is for the next button*/

    }

    @Composable
    fun ArtDescriptor(titleId: Int, artistId: Int, yearId: Int) {
        //Home Page section B
        val titleOfArt = stringResource(id = titleId)
        val artistName = stringResource(id = artistId)
        val yearOfArt = stringResource(id = yearId)

        Text(text = titleOfArt, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Text(text = "$artistName ($yearOfArt)", fontWeight = FontWeight.Light, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }

    @Composable
    fun ArtWall(artistId: Int, artworkImageId: Int, descriptionId: Int, navController: NavController) {
        //Home Page section A

        // TODO 1: Add image of art work
        Image(painter = painterResource(id = artworkImageId), contentDescription = stringResource(id = descriptionId), modifier = Modifier.clickable { navController.navigate(Screen.Artist.route + "/$artistId") } )
        //TODO 2: Add a click listener to the image to get to artist page
            //Note: use the following code on your click event
        //navController.navigate(Screen.Artist.route + "/$artistId")
    }

    @Composable
    fun ArtistPage(navController: NavController) {
        //get Id so we know which artwork to go back to
        val id = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
        //Artist Page section A

        val current = DataSource.arts[id]


       Scaffold(
           bottomBar = {
               BottomAppBar {
                   Button(onClick = { navController.navigate(Screen.Home.route + "/$id")}) {
                       Text(text = stringResource(id = R.string.back))
                   }
               }
           }
       ){ innerPadding ->

           Column(
               modifier = Modifier
                   .padding(innerPadding)
                   .verticalScroll(rememberScrollState())
           ) {
               Row(
                   modifier = Modifier
                       .fillMaxWidth(),
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.Center
               ) {
                   Image(painter = painterResource(id = current.artistImageId),
                       contentDescription = null,
                       modifier = Modifier
                           .size(150.dp)
                           .border(BorderStroke(2.dp, Color(0xFF85CB33)), CircleShape)
                           .clip(CircleShape)

                   )
                   Spacer(modifier = Modifier.size(10.dp))
                   Column{
                       Text(text = stringResource(id = current.artistId), fontWeight = FontWeight.Bold)
                       Text(text = stringResource(id = current.artistInfoId))
                   }
               }
               Spacer(modifier = Modifier.size(20.dp))
               Text(text = stringResource(id = current.artistBioId))
           }

           

    
       }
        
        //Artist Page section B
        

        
        //Artist Page section C
        //DO NOT MODIFY THE FOLLOWING CODE:


    }

}

