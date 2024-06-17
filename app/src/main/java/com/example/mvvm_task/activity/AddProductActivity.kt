package com.example.mvvm_task.activity


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvm_task.databinding.ActivityAdduserBinding
import com.example.mvvm_task.model.ProductModel
import com.example.mvvm_task.repository.ProductRepositoryImpl
import com.example.mvvm_task.utlis.ImageUtils
import com.example.mvvm_task.utlis.LoadingUtils
import com.squareup.picasso.Picasso

import com.example.mvvm_task.viewmodel.ProductViewModel

import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    lateinit var loadigUtils: LoadingUtils

    lateinit var addProductBinding: ActivityAdduserBinding


    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null
    lateinit var imageUtils: ImageUtils
    lateinit var productViewModel: ProductViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Binding
        addProductBinding = ActivityAdduserBinding.inflate(layoutInflater)

        setContentView(addProductBinding.root)

        //from gist file week eight lesson 2
        loadigUtils = LoadingUtils(this)

        imageUtils = ImageUtils(this)
        imageUtils.registerActivity {url->
            url.let {
                imageUri = it
                Picasso.get().load(url).into(addProductBinding.imageBrowser)
            }
        }
        var repo= ProductRepositoryImpl()
        productViewModel=ProductViewModel(repo)

        addProductBinding.imageBrowser.setOnClickListener {
            imageUtils.launchGallery(this)

        }

        addProductBinding.addButton.setOnClickListener {
            if (imageUri != null){
                uploadImage()
            }else{
                Toast.makeText(applicationContext,"please upload image first",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    fun uploadImage() {
        loadigUtils.showLoading()
        val imageName = UUID.randomUUID().toString()
        imageUri?.let {
            productViewModel.uploadImage(imageName,it) { success, imageUrl ->
                if (success) {
                    addProduct(imageUrl.toString(), imageName.toString())
                }
            }
        }
    }

    fun addProduct(url: String, imageName: String) {
        var name: String = addProductBinding.newname.text.toString()
        var price: Int = addProductBinding.newprice.text.toString().toInt()
        var description: String = addProductBinding.newdescription.text.toString()
        var data = ProductModel("",name,price,description,url,imageName)

        productViewModel.addProduct(data){ success, message ->
            if (success){
                loadigUtils.dismiss()
                Toast.makeText(applicationContext,message,
                    Toast.LENGTH_LONG).show()
                finish()
            }else{
                loadigUtils.dismiss()
                Toast.makeText(applicationContext,message,
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}
