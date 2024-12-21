package eu.tutorial.myshoppinglistapp

import android.icu.text.CaseMap.Title
import android.icu.text.CaseMap.toTitle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
//once class created then it can be used as a type for fucntions
// data class defines structure of a shopping item
data class ShoppingItems(   //it is a class which is called in every function for accessing its variables
    var id:Int,
    var name:String,
    var quantity:Int,
    var isEditing:Boolean=false
)

@Composable
fun ShoppingListApp() {
//    The state variables (sItems, showDialog, itemName, itemQuantity) are managed using remember and mutableStateOf to handle UI reactivity.
    var sItems by remember { mutableStateOf(listOf<ShoppingItems>()) }   //here ShoppingItems is a type provideed
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {showDialog=true},
                modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item",style= MaterialTheme.typography.headlineLarge)
        }

//      LazyColumn lists the items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //items: Represents the individual data item from your list. For each item in
            // yourItemList, the items function will call the provided lambda
            // function to generate a corresponding composable UI element.
            items(sItems) {//items is a funcion present in lazy Column
                item ->
                if (item.isEditing){   //this is by default true
                    ShoppingItemEdit(item = item, onEditComplete ={  //function call
                        editedName,editedQuantity ->
                        //here using a map for iterating over the list
                        //also using a copy keyword for copying and changing the values in the list
                        // here it is items of sItems due to iteration using map keyword
                        sItems = sItems.map { it.copy(isEditing = false) }
                        val editedItem = sItems.find{it.id==item.id}
                        editedItem?.let {
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    } )
                }else{
                    ShoppingItemList(item =item ,
                        onEditClick = {
                        sItems=sItems.map{it.copy(isEditing=it.id==item.id)}  //this shows if we not got
                            // the id same then the condtions get false or also if the id not found then the condition
                            //still gets false
                    },
                        onDeleteClick = {
                            sItems=sItems-item
                        })
                }
            }
        }
    }
    if (showDialog){
//        AlertDialog is a composable .used for confirming actions, showing
//        messages, or getting simple input from the user.
        AlertDialog(
            onDismissRequest = { showDialog=false },
            confirmButton = {
                            Row(modifier= Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Button(onClick = {
                                    if(itemName.isNotBlank()){
                                        var newItem=ShoppingItems(   //here creating an object(class Awaken) of the data class Shopping Items
                                            id=sItems.size+1,
                                            name=itemName,
                                            quantity=itemQuantity.toInt(),
                                        )
                                        sItems=sItems+ newItem //in kotlin a data can be added directly to the list by just + sign
                                        showDialog=false
                                        itemName=""  //for adding new Item the box of ItemName should be again empty
                                        itemQuantity=""
                                    }
                                }) {
                                    Text(text = "Add")
                                }
                                Button(onClick = { showDialog=false }) {
                                    Text(text = "Cancle")
                                }
                            }
                            },
            title = { Text(text = "Add Shopping Item") },
            text={
                Column {
                    OutlinedTextField(value = itemName, onValueChange = {itemName=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))

                    OutlinedTextField(value = itemQuantity, onValueChange = {itemQuantity=it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))
                }
                }
        )
    }

}

@Composable
fun ShoppingItemEdit(item: ShoppingItems,onEditComplete:(String,Int) -> Unit){  //a lambda function created
    var editedName by remember { mutableStateOf(item.name)}
    var editedQuantity by remember { mutableStateOf(item.quantity.toString())}
    var isEditing by remember { mutableStateOf(item.isEditing)}

    Row (modifier= Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .background(color = Color.White),
         horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Column{
            BasicTextField(  // text field without border
                value = editedName,
                onValueChange = {editedName=it},  //this it keyword is used when the variable is
                                                  //state variable that is created using remember
                singleLine = true,
                modifier= Modifier
                    .wrapContentSize()
                    .padding(8.dp)
                )
            BasicTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity=it},
                singleLine = true,
                modifier= Modifier
                    .wrapContentSize()  //only the required space is taken not the whole width or height
                    .padding(8.dp)
                )
        }
        Button(
            onClick = {
                isEditing=false
                onEditComplete(editedName,editedQuantity.toIntOrNull() ?: 1)   //if not Int or Null then by defalut 1
        }
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun ShoppingItemList(
    //item: ShoppingItems is a way to declare a function parameter.
    // Specifically, it means that the item parameter is of the type ShoppingItems
    item:ShoppingItems, //here item is of ShoppingItem type
    onEditClick : () -> Unit,  //onEditClick is a  function type parameter declared with no input and null output
    onDeleteClick :() -> Unit  //used in the ShoppingListApp function
){
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF18786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
                Text(text = item.name, modifier = Modifier.padding(8.dp))
                Text(text = "Qty : ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row(modifier=Modifier.padding(8.dp)) {

            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription =null )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription =null)

            }
        }
    }
}

