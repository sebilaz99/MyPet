package com.example.mypet.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.LocationItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationLongClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.text.SimpleDateFormat
import java.util.*

class Map : AppCompatActivity() {

    private var mapView: MapView? = null
    private lateinit var name: TextView
    private lateinit var address: TextView
    private lateinit var phone: TextView
    private lateinit var website: TextView
    private lateinit var nested: NestedScrollView
    private lateinit var serviceName: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var ownerId: String
    private lateinit var mapReference: DatabaseReference
    private lateinit var serviceType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        mapView = findViewById(R.id.mapView)
        val homeBtn = findViewById<ImageView>(R.id.homeIV)

        serviceName = findViewById(R.id.serviceNameTV)
        val type = intent.getStringExtra("type")
        serviceName.text = type


        auth = FirebaseAuth.getInstance()
        ownerId = auth.currentUser!!.uid
        mapReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("map")


        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            serviceType = intent.getStringExtra("type").toString()
            supportActionBar?.title = serviceType
            when (serviceType) {
                "Veterinary" -> {
                    addAnnotationToMap(
                        Point.fromLngLat(23.6099308, 46.7771242),
                        LocationItem(
                            "Cabinet Veterinar Cromavet Marasti",
                            "Bulevardul 21 Decembrie 1989 148, Cluj-Napoca",
                            "www.cromavet.ro",
                            "+40737045106"
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.6213428, 46.7876121),
                        LocationItem(
                            "Happy Dragon Vet",
                            "Strada Teilor 3, Cluj-Napoca",
                            "happy-dragon-vet.business.site",
                            "+40770206201"
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.5634564, 46.7570519),
                        LocationItem(
                            "Clinica Veterinar?? Alvet",
                            "Strada C??mpului 51, Cluj-Napoca",
                            "alvetcluj.ro",
                            "+40723577475"
                        )
                    )
                }
                "Parks" -> {
                    addAnnotationToMap(
                        Point.fromLngLat(23.57315, 46.7566558),
                        LocationItem(
                            "Parc pentru c??ini",
                            "Strada Gheorghe Dima 29, Cluj-Napoca",
                            "www.usamvcluj.ro",
                            "+40264431910"
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.5578337, 46.7595241),
                        LocationItem(
                            "Dog Park \"La Calvaria\"",
                            "Cluj-Napoca",
                            "",
                            ""
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.5372018, 46.7537669),
                        LocationItem(
                            "Parcul Colina",
                            "Strada Bucium 15, Cluj-Napoca",
                            "",
                            ""
                        )
                    )
                }
                "Pet Shops" -> {
                    addAnnotationToMap(
                        Point.fromLngLat(23.6296948, 46.7691158),
                        LocationItem(
                            "Zoo Center",
                            "Strada Alexandru Vaida Voevod 59, Cluj-Napoca",
                            "www.zoocenter.ro",
                            "+40723283591"
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.6129484, 46.7809801),
                        LocationItem(
                            "Royal Canin",
                            "400620, Strada Fabricii 3, Cluj-Napoca",
                            "",
                            ""
                        )
                    )
                    addAnnotationToMap(
                        Point.fromLngLat(23.5387693, 46.7585162),
                        LocationItem(
                            "Animax",
                            "Bulevardul 1 Decembrie 1918 142, Cluj-Napoca (Cora)",
                            "www.animax.ro",
                            "+40725888851"
                        )
                    )
                }
            }

        }

        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun addAnnotationToMap(point: Point, location: LocationItem) {
        bitmapFromDrawableRes(
            this@Map,
            R.drawable.small_paw_pin
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener {

                name = findViewById(R.id.locationNameTV)
                address = findViewById(R.id.locationAddressTV)
                phone = findViewById(R.id.locationPhoneTV)
                website = findViewById(R.id.locationWebsiteTV)

                name.visibility = View.VISIBLE
                address.visibility = View.VISIBLE
                phone.visibility = View.VISIBLE
                website.visibility = View.VISIBLE

                name.text = location.name
                address.text = location.address
                phone.text = location.phone
                website.text = location.website
                true
            })

            pointAnnotationManager?.addLongClickListener(OnPointAnnotationLongClickListener {
                Snackbar.make(
                    window.decorView.rootView,
                    "You want to pay this place a visit?",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("YES") {
                        val currentDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
                        serviceType = intent.getStringExtra("type").toString()
                        mapReference.child(currentDate).setValue(name.text.toString())
                    }.show()
                true
            })
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}