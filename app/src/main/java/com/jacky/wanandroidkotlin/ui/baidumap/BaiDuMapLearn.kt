package com.jacky.wanandroidkotlin.ui.baidumap

/**
 * @author:Hzj
 * @date  :2021/9/16
 * desc  ：
 * record：
 */
import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.local.GeoCoderModel
import com.jacky.wanandroidkotlin.model.local.LocationModel
import com.jacky.wanandroidkotlin.util.ApkUtils
import com.jacky.wanandroidkotlin.wrapper.orNotNullNotEmpty
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.jacky.wanandroidkotlin.wrapper.viewVisibleExt
import com.zenchn.support.permission.applySelfPermissionsStrict
import com.zenchn.support.permission.checkSelfPermission
import com.zenchn.support.router.Router
import com.zenchn.support.utils.LoggerKit
import com.zenchn.support.widget.TitleBar
import java.math.BigDecimal
import java.util.*


class BaiDuMapLearnActivity : BaseVMActivity<BaiDuMapLearnViewModel>(),
    TitleBar.OnLeftClickListener, BaiduMap.OnMapLoadedCallback {

    private lateinit var mTitleBar: TitleBar
    private lateinit var tvLatLng: TextView
    private lateinit var tvLocation: TextView
    private lateinit var mFlCommit: FrameLayout
    private lateinit var mBdMapView: MapView
    private var mLocationClient: LocationClient? = null

    private var mCameraCenterPosition: LatLng? = null
    private var mEditable = true
    private var mIsLastDrag = true //是否是最后一次拖拽，true 就记录最后一次在拖拽坐标
    private var mPreLatLng: LatLng? = null//第一次进来的坐标
    private var mPreDraggedLatLng: LatLng? = null//最后一次拖拽的坐标

    override fun getLayoutId(): Int = R.layout.activity_baidu_coordinate_pickup

    override fun onResume() {
        super.onResume()
        mBdMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBdMapView.onPause()
    }

    override fun onDestroy() {
        mBdMapView.map?.setMyLocationEnabled(false)
        mBdMapView.onDestroy()
        mLocationClient?.stop()
        super.onDestroy()
    }

    override fun initWidget() {
        mTitleBar = findViewById(R.id.mTitleBar)!!
        tvLatLng = findViewById(R.id.tv_latLng)!!
        tvLocation = findViewById(R.id.tv_location)!!
        mFlCommit = findViewById(R.id.fl_commit)!!
        mBdMapView = findViewById(R.id.map_view)!!

        mTitleBar.setOnLeftClickListener(this)
        mEditable = intent.getBooleanExtra(EXTRA_EDITABLE, false)
        mFlCommit.visibility = if (mEditable) View.VISIBLE else View.GONE

        viewClickListener(R.id.btChoose) {
//            val data = Intent()
//            mCameraCenterPosition?.let { latLng ->
//                data.putExtra("latLng", latLng)
//                data.putExtra("address", tvLocation.text.toString())
//            }
//            setResult(Activity.RESULT_OK, data)
//            finish()
        }
        initBaiduMap()
        initPermissions()
    }

    private fun initPermissions() {
        //申请定位权限
        checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            onGranted = { mViewModel.initLocation() },
            onDenied = {
                showMessage("请授予定位权限")
                applySelfPermissionsStrict(Manifest.permission.ACCESS_FINE_LOCATION) {}
            })
    }

    private fun initBaiduMap() {
        showSatelliteLayer()
        mBdMapView.map?.apply {
            //显示定位图层
            setMyLocationEnabled(true)
            setOnMapLoadedCallback(this@BaiDuMapLearnActivity)
        }
        //开启定位
        mLocationClient = LocationModel.getInstance().getBaiduLocation(false,
            object : LocationModel.BaiduLocationCallback {
                override fun onLocationSuccess(bdLocation: BDLocation) {
//                    moveMapTo(bdLocation)
                    //mapView 销毁后不在处理新接收的位置
                    val locData = MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(bdLocation.getDirection())
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude())
                        .build()
                    mBdMapView.map?.setMyLocationData(locData)
                }

                override fun onLocationFailure() {}

            })
    }

    private fun showSatelliteLayer() {
        mBdMapView.map?.apply {
            //卫星图层
            mapType = BaiduMap.MAP_TYPE_SATELLITE
        }
    }

    private fun showMyLocation(bdLocation: BDLocation) {
        mPreLatLng = LatLng(bdLocation.latitude, bdLocation.longitude)
        showEditableVision(mPreLatLng)
    }

    /**
     * 移动地图到指定位置
     */
    private fun moveMapTo(myLocationData: MyLocationData) {
        val targetLatLng = LatLng(myLocationData.latitude, myLocationData.longitude)
        var update = MapStatusUpdateFactory.newLatLng(targetLatLng)
        mBdMapView.map?.animateMapStatus(update)
        // 缩放地图大小
        update = MapStatusUpdateFactory.zoomTo(18f)
        mBdMapView.map?.animateMapStatus(update)
    }

    override fun onMapLoaded() {
        mPreLatLng = intent.getParcelableExtra("latLng")
        Log.d(TAG, "扔进来的坐标: $mPreLatLng")
        if (mEditable) {
            if (mPreLatLng == null) {
                //同户主新增进来时坐标已被清空，此时重新定位
                mViewModel.initLocation()
            } else {
                showEditableVision(mPreLatLng)
            }
        } else {
            showNoEditVision(mPreLatLng)
        }
    }

    /**
     * 选取坐标
     *
     * @param preLatLng
     */
    private fun showEditableVision(preLatLng: LatLng?) {
        viewVisibleExt(R.id.iv_fixed, true)
        val baiduMap = mBdMapView?.map
        if (baiduMap != null) {
            //设置Marker在屏幕中心上,不跟随地图移动

            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(preLatLng, 19f))
            // 设置可视范围变化时的回调的接口方法
            baiduMap.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
                override fun onMapStatusChangeStart(mapstatus: MapStatus?) {
                }

                override fun onMapStatusChangeStart(mapstatus: MapStatus?, p1: Int) {
                }

                override fun onMapStatusChange(mapstatus: MapStatus?) {
                    //回调不在主线程
                    mapstatus?.target?.let { latLng ->
                        mCameraCenterPosition = latLng
                        //将坐标四舍五入,保留小数点后6位
                        val bigLat = BigDecimal(latLng.latitude.toString())
                        val lat = bigLat.setScale(6, BigDecimal.ROUND_HALF_UP).toDouble()
                        val bigLng = BigDecimal(latLng.longitude.toString())
                        val lng = bigLng.setScale(6, BigDecimal.ROUND_HALF_UP).toDouble()
                        runOnUiThread {
                            tvLatLng.text =
                                String.format(Locale.CHINA, "当前坐标：%1\$f,%2\$f", lng, lat)
                        }
                    }
                }

                override fun onMapStatusChangeFinish(mapstatus: MapStatus?) {
                    val certificateSHA1Fingerprint =
                        ApkUtils.getCertificateSHA1Fingerprint(this@BaiDuMapLearnActivity)
                    LoggerKit.d(certificateSHA1Fingerprint)
                    mCameraCenterPosition?.let { position ->
                        //todo 百度逆地理编码报错，尚未解决
                        mViewModel.analysisLocation(position)
                        if (mIsLastDrag) {
                            mPreDraggedLatLng = position
                        }
                    }
                }

            })
        }
    }

    /**
     * 只是查看，不可重新选取坐标
     *
     * @param preLatLng
     */
    private fun showNoEditVision(preLatLng: LatLng?) {
        if (preLatLng != null) {
            val markerOptions: MarkerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_fix))
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .position(preLatLng)
            mBdMapView.map?.apply {
                addOverlay(markerOptions)
                animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(preLatLng, 19f))
            }
            tvLatLng.text = String.format(
                Locale.CHINA,
                "当前坐标：%1\$f,%2\$f",
                preLatLng.longitude,
                preLatLng.latitude
            )
            mViewModel.analysisLocation(preLatLng)
        }
    }

    override fun onLeftViewClick(v: View?) {
        onBackPressed()
    }


    override val startObserve: BaiDuMapLearnViewModel.() -> Unit = {
        mLocationData.observe(this@BaiDuMapLearnActivity) {
            showMyLocation(it)
        }
        mGeoCoderData.observe(this@BaiDuMapLearnActivity) { address ->
            tvLocation.text = address
        }
    }


    companion object {
        private const val EXTRA_EDITABLE = "EXTRA_EDITABLE"
        private const val TAG = "CoordinatePickupActivit"

        @JvmStatic
        fun obtainLatLng(data: Intent): LatLng? {
            return data.getParcelableExtra("latLng")
        }

        @JvmStatic
        fun obtainAddress(data: Intent): String? {
            return data.getStringExtra("address")
        }

        @JvmStatic
        fun launch(
            from: Activity,
            requestCode: Int,
            editable: Boolean,
            latLng: LatLng?
        ) {
            Router
                .newInstance()
                .from(from)
                .putBoolean(EXTRA_EDITABLE, editable)
                .putParcelable("latLng", latLng)
                .to(BaiDuMapLearnActivity::class.java)
                .requestCode(requestCode)
                .launch()
        }
    }
}

class BaiDuMapLearnViewModel(application: Application) : BaseViewModel(application) {
    val mLocationData: MutableLiveData<BDLocation> = MutableLiveData()
    val mGeoCoderData: MutableLiveData<String> = MutableLiveData()

    fun initLocation() {
        LocationModel.getInstance()
            .getBaiduLocation(false, object : LocationModel.BaiduLocationCallback {
                override fun onLocationSuccess(bdLocation: BDLocation) {
                    mLocationData.value = bdLocation
                }

                override fun onLocationFailure() {
                }

            })
    }

    fun analysisLocation(latLng: LatLng?) {
        GeoCoderModel.getInstance().getBdMapGeocoderAddress(latLng) { address ->
            mGeoCoderData.postValue(address.orNotNullNotEmpty("解析失败")) }
    }
}