package com.example.job

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.job.ui.theme.JobTheme
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
//                    LoginComposable()
//                    RegisterComposable()
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("Job App")
                                }
                            )
                        }
                    ) {innerPadding->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ){
                            NavHost(navController=navController, startDestination="viewJobScreen") {
                                composable("viewJobScreen"){
                                    ViewJobs(goLogin={navController.navigate("loginScreen")});
                                }
                                composable("loginScreen"){
                                    LoginComposable(goRegister={navController.navigate("registerScreen")},goBack = {navController.popBackStack()})
                                }
                                composable("registerScreen"){
                                    RegisterComposable(goBack = {navController.popBackStack()})
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    
    @Composable
    fun ViewJobs(goLogin: () -> Unit){
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "viewing")
                Button(onClick = { goLogin() }) {
                    Text(text = "Login")
                }
            }
        }
    }

    @Composable
    fun LoginComposable(goRegister:()->Unit,goBack: () -> Unit) {
        var account by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var responseText by remember { mutableStateOf("") }
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(text = "Welcome Login", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = account,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Account") },
                    onValueChange = { account = it })
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = password,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it })
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    val url = "http://10.0.2.2:3000/user/login"
                    val postData = listOf("account" to account, "password" to password)
                    url.httpPost(postData).response { request, response, result ->
                        when (result) {
                            is Result.Success -> {
//                                responseText = result.get().decodeToString()
                                goBack()
                            }

                            is Result.Failure -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ERROR ${result.error.message}",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Login")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    goRegister()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Register")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    goBack()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Back")
                }
                Text(text = responseText)
            }
        }
    }

    @Composable
    fun RegisterComposable(goBack: ()->Unit) {
        var account by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var balance by remember { mutableStateOf("0") }
        var responseText by remember { mutableStateOf("") }
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(text = "Welcome Register", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = account,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Account") },
                    onValueChange = { account = it })
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = password,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it })
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = balance,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { newText ->
                        val filteredText = newText.filter { it.isDigit() }
                        balance = filteredText
                    },
                    label = { Text("Balance") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number // digital keyboard
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    val url = "http://10.0.2.2:3000/user/register"
                    if(balance.equals("")){
                        balance = "0"
                    }
                    val postData = listOf("account" to account, "password" to password,"balance" to balance)
                    url.httpPost(postData).response { request, response, result ->
                        when (result) {
                            is Result.Success -> {
//                                responseText = result.get().decodeToString()
                                goBack()
                            }

                            is Result.Failure -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "ERROR ${result.error.message}",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Register")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                     goBack();
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Back")
                }
                Text(text = responseText)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JobTheme {
        Greeting("Android")
    }
}