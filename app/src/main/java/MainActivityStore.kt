package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme
import  com.example.room_setup_composables.com.example.room_setup_composables.ui.theme.StoreList
class MainActivity : ComponentActivity() {
    //private val viewModel: BookingViewModel by viewModels()

    //  override fun onCreate(savedInstanceState: Bundle?) {
    //   super.onCreate(savedInstanceState)
    //   setContent {
    //       RoomDatabaseSetupTheme {
    //          HomePageNavigation(viewModel);
    //      }
    //   }
    //  }
//}
    private val storeViewModel: StoreViewModel by viewModels {
        StoreViewModelFactory(AppDatabase.getDatabase(applicationContext).storeDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val stores = listOf(
        //    Store(
          //      name = "Juicy Grill",
         //       info = "Best burgers in town",
         //       avDays = "Sunday",
        //        avHours = "9PM",
        //        location = "Athens",
                //availability = 5
         //   ),
         //   Store(
         //       name = "Juicy Grill",
        //        info = "Best burgers in Monastiraki",
         //       avDays = "Sunday",
         //       avHours = "8PM",
         //       location = "Athens",
                //availability = 6
           // ),
          //  Store(
         //       name = "Juicy Grill",
          //      info = "Best burgers in Athens",
          //      avDays = "Sunday",
          //      avHours = "10PM",
         //       location = "Athens",
                //availability = 8
         //   )
        //)

        // Insert each store in the list into the database
        //stores.forEach { store ->
        //    storeViewModel.insertStore(store)
       // }

        setContent {
            RoomDatabaseSetupTheme {
                StoreList(viewModel = storeViewModel, filtername = "Juicy Grill")
            }
            //storeViewModel.insertStore(
            //   Store(
            //      name = "Juicy Grill",
            //       info = "Best burgers in town",
            //       avDays = "Sunday",
            //       avHours = "9 AM",
            //      location = "Athens"
            //   )
            // )

            // setContent {
            //     RoomDatabaseSetupTheme {
            //         StoreList(viewModel = storeViewModel)
            //     }
            //   }
        }
    }
}

