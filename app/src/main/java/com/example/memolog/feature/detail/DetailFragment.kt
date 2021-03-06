package com.example.memolog.feature.detail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.net.Uri.parse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentDetailBinding
import com.example.memolog.repository.MemoRepository
import android.view.inputmethod.InputMethodManager

import android.os.Build
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi

import androidx.navigation.fragment.findNavController
import com.example.memolog.getCurrentTime
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil.setContentView
import com.example.memolog.event.ImageEvent
import com.google.android.gms.tasks.Task
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.io.File
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import android.graphics.Bitmap


import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.os.Handler
import android.os.Looper


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var detailViewModel: DetailViewModel
    private var isEditMode = MutableLiveData(false)
    private var isLock = MutableLiveData(false)
    private var memoId = 0L
    private lateinit var backPressCallback: OnBackPressedCallback
    private var password = ""
    private var imageList = MutableLiveData(arrayListOf<String>())
    private var inputImage: InputImage? = null
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initViewModel()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MemoDebug", "DetailFragment::onViewCreated-()")

        EventBus.getDefault().register(this)

        val args: DetailFragmentArgs by navArgs()
        memoId = args.memoId

        ////////////////////////////////////////////////////////////////////
        // High-accuracy landmark detection and face classification
        // detector??? ?????? ?????? ??????
        // High-accuracy landmark detection and face classification
        // detector??? ?????? ?????? ??????
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()

        // detector??????
        val faceDetector: FaceDetector = FaceDetection.getClient(highAccuracyOpts)
        ////////////////////////////////////////////////////////////////////

        // ?????? ?????? ?????? ???, ?????? ???????????? ??????
        detailViewModel.getOneMemo(memoId) { memo ->
            binding.updatedTime.text = makeSimpleDate(memo.updatedTime)
            binding.textTitle.text = memo.title
            binding.textContent.text = memo.content
            isLock.postValue(memo.isLocked)
            Log.d("MemoDebug", "onViewCreated-() : ${isLock.value}")

            if(isLock.value == true){
                password = memo.password.toString()
                Log.d("MemoDebug", "password : $password")
            }
            // image ????????? ??????
            memo.image.let{
                if(it?.size!! > 0){
                    val savedImageUri = memo.image!![0]
                    Log.d("MemoDebug", "savedImageUri: $savedImageUri")
                    imageUri = savedImageUri.toUri()

                    inputImage = context?.let { context -> InputImage.fromFilePath(context, savedImageUri.toUri()) }
                    Log.d("MemoDebug", "inputImage: $inputImage")

                    activity?.runOnUiThread(Runnable {
                        // UI ????????? ??? ????????? ?????????.
                        binding.imageView.setImageURI(savedImageUri.toUri())
                        binding.imageView.visibility = View.VISIBLE
                    })
                }
            }
        }

        // ????????? ?????? ??? ????????? ??????
        binding.imageView.setOnClickListener {
            Log.d("MemoDebug", "????????? ????????????")
            showImageDeleteDialog(memoId){ result ->
                if(result) {
                    Handler(Looper.getMainLooper()).post {
                        binding.imageView.visibility = View.GONE
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////////////
        binding.detectBtn.setOnClickListener {
            val result = inputImage?.let { inputImage ->
                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        // Task completed successfully
                        if(faces.isEmpty()){
                            Toast.makeText(binding.root.context, "No faces detected", Toast.LENGTH_SHORT).show()
                        }else{
                            for (face in faces) {
                                val bounds = face.boundingBox
                                val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                                val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                                // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                // nose available):
                                val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                                leftEar?.let {
                                    val leftEarPos = leftEar.position
                                }

                                // If classification was enabled:
                                if (face.smilingProbability != null) {
                                    val smileProb = face.smilingProbability
                                }
                                if (face.rightEyeOpenProbability != null) {
                                    val rightEyeOpenProb = face.rightEyeOpenProbability
                                }

                                // If face tracking was enabled:
                                if (face.trackingId != null) {
                                    val id = face.trackingId
                                }
                            }

                        }
                        Log.d("MemoDebug", "faces: $faces")

                        if(faces.size > 0){
                            Log.d("MemoDebug", "face top => ${faces[0].boundingBox.top}")
                            Log.d("MemoDebug", "face left => ${faces[0].boundingBox.left}")
                            Log.d("MemoDebug", "face right => ${faces[0].boundingBox.right}")
                            Log.d("MemoDebug", "face bottom => ${faces[0].boundingBox.bottom}")

                            val bottom = faces[0].boundingBox.bottom.toFloat()
                            val top = faces[0].boundingBox.top.toFloat()
                            val left = faces[0].boundingBox.left.toFloat()
                            val right = faces[0].boundingBox.right.toFloat()

                            val bitmapImage = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                            val mutableBitmap = bitmapImage.copy(Bitmap.Config.ARGB_8888, true)
                            val canvas = Canvas(mutableBitmap)

                            val paint = Paint()
                            paint.strokeWidth = 7f
                            paint.style = Paint.Style.STROKE
                            paint.color = Color.RED
                            canvas.drawRect(left, top, right, bottom, paint)

                            binding.imageView.setImageBitmap(mutableBitmap)
                        }
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        Log.d("MemoDebug", "e: $e")
                    }
            }
        }
        ////////////////////////////////////////////////////////////////////

        isEditMode.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode) { // ????????????
                Log.d("MemoDebug", "isEditMode : $isEditMode")
                binding.editBtn.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
                binding.editTitle.visibility = View.VISIBLE
                binding.editContent.visibility = View.VISIBLE
                binding.addPhotoBtn.visibility = View.VISIBLE

                binding.textTitle.visibility = View.GONE
                binding.textContent.visibility = View.GONE

                if(isLock.value == true){ // ????????????
                    Log.d("MemoDebug", "isEditMode ?????? isLock : ${isLock.value}")
                    binding.lockBtn.visibility = View.VISIBLE
                    binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_24)

                }else if(isLock.value == false){ // ????????????
                    Log.d("MemoDebug", "isEditMode ?????? isLock : ${isLock.value}")
                    binding.lockBtn.visibility = View.VISIBLE
                    binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
                }
            } else { // ???????????? ??????
                Log.d("MemoDebug", "isEditMode : $isEditMode")
                binding.editBtn.visibility = View.INVISIBLE
                binding.editTitle.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.INVISIBLE
                binding.editContent.visibility = View.INVISIBLE
                binding.lockBtn.visibility = View.INVISIBLE
                //binding.unlockBtn.visibility = View.INVISIBLE
                binding.addPhotoBtn.visibility = View.INVISIBLE

                binding.textTitle.visibility = View.VISIBLE
                binding.textContent.visibility = View.VISIBLE

                // ?????? ?????? -> textView ??????
                binding.textTitle.text = binding.editTitle.text
                binding.textContent.text = binding.editContent.text

                // ??????, ???????????? ?????? INVISIBLE
                //binding.unlockBtn.visibility = View.INVISIBLE
                binding.lockBtn.visibility = View.INVISIBLE

                // TODO:: ????????? ????????? ????????? ???????????? ??? ?????????
            }
        }

        binding.lockBtn.setOnClickListener {
            if(isLock.value == true){
                Log.d("MemoDebug", "?????? ???????????? ????????? ??????")
                showUnlockDialog(memoId)
            }
        }

        //TODO:: DEBUG ?????? ??? ???????????? ??????X
        isLock.observe(viewLifecycleOwner){ isLock ->
            if(isLock && isEditMode.value == true){ // ????????????
                Log.d("MemoDebug", "11111 isLock?????? isLock : $isLock")
                binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_24)
                binding.lockBtn.setOnClickListener {
                   showUnlockDialog(memoId)
                }

            }else if(!isLock && isEditMode.value == true){ // ????????????
                Log.d("MemoDebug", "22222 isLock?????? isLock : $isLock")
                binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
            }
        }

        binding.textTitle.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())

            // ?????? ?????????
            binding.editTitle.apply {
                setSelection(binding.textTitle.length())
                requestFocus()
            }

            // ????????? ????????? // TODO:: ????????? ?????? ???????????? ??????
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.editTitle, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.textContent.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())

            // ????????? & ????????? ?????????
            binding.editContent.apply {
                setSelection(binding.textContent.length())
                requestFocus()
            }

            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.editContent, InputMethodManager.SHOW_IMPLICIT)
        }

        // edit ?????? -> ?????? ????????????
        binding.editBtn.setOnClickListener {
            detailViewModel.getOneMemo(memoId) { current ->
                current.title = binding.editTitle.text.toString()
                current.content = binding.editContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                if(imageList.value?.isNotEmpty() == true){
                    current.image = imageList.value
                }

                detailViewModel.updateMemo(current)

                // ????????? ?????????
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity?.currentFocus?.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )

                isEditMode.postValue(false)
            }
        }

        // delete ??????
        binding.deleteBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("?????? ?????????????????????????\n????????? ???????????????????")
                .setPositiveButton("yes") { _, _ ->
                    detailViewModel.deleteMemo(memoId)
                    this.findNavController().navigate(R.id.homeFragment)
                }
                .setNegativeButton("no") { _, _ ->
                    // do nothing
                }
                .show()
        }

        isLock.observe(viewLifecycleOwner){ isLock ->
            if(isLock){ // ?????? ??????

            }else{ // ?????? ?????? ??????
                binding.lockBtn.setOnClickListener {
                    showFirstLockDialog()
                }
            }
        }

        // ?????? ?????? ??????
        binding.addPhotoBtn.setOnClickListener {
//            CropImage.activity()
//                .start(requireContext(), this)

            // ????????? ????????? ?????? ?????????
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // ?????? ???????????? true
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, 100)
            //activity?.startActivityForResult(intent, 100)

        }


        // back ?????? -> ?????? ????????????
        binding.backBtn.setOnClickListener {
            //memoId
            reviseMemo()
        }


    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        detailViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    // back key -> ?????? ????????????
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(findNavController().currentDestination?.id == R.id.detailFragment) {
                    Log.d("MemoDebug", "DetailFragment::handleOnBackPressed-()")
                    reviseMemo()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressCallback.remove()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reviseMemo(){
        if (isEditMode.value == true) {
            detailViewModel.getOneMemo(memoId) { current ->
                current.title = binding.editTitle.text.toString()
                current.content = binding.editContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                if(imageList.value?.isNotEmpty() == true){
                    current.image = imageList.value
                }

                detailViewModel.updateMemo(current)
                isEditMode.postValue(false)
            }
        } else {
            detailViewModel.getOneMemo(memoId) { current ->
                current.title = binding.textTitle.text.toString()
                current.content = binding.textContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                detailViewModel.updateMemo(current)
                isEditMode.postValue(false)
            }
        }
        this.findNavController().navigate(R.id.homeFragment)
    }

    private fun makeSimpleDate(before: String): String {
        return before.split("T")[0]
    }

    private fun showFirstLockDialog(){
        if (binding.inputPw.parent != null){
                (binding.inputPw.parent as ViewGroup).removeView(binding.inputPw)
                binding.inputPw.visibility = View.VISIBLE
                binding.inputPw.requestFocus()
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setMessage("???????????? ?????? ???????????? ??????????????????")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->

                val firstPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "firstPw: $firstPw")
                if(firstPw.length != 4){
                    Toast.makeText(context, "??? ????????? ???????????????", Toast.LENGTH_SHORT).show() // TODO :: dialog ?????? toast ????????? ????????? dialog ??? ?????? ??? ??????????
                    binding.inputPw.text = null
                    return@setPositiveButton
                }else {
                    showSecondLockDialog(firstPw)
                }
            }
            .setNegativeButton("cancel") { _, _ ->
                binding.inputPw.text = null
                return@setNegativeButton
            }
            .show()

        val window = dialogBuilder.window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        //binding.inputPw.showKeyboard()
    }

    private fun showSecondLockDialog(firstPw: String){
        if (binding.inputPw.parent != null){
            (binding.inputPw.parent as ViewGroup).removeView(binding.inputPw)
            binding.inputPw.text = null
            binding.inputPw.requestFocus()
            binding.inputPw.visibility = View.VISIBLE
        }

        val dialogBuilder2 = AlertDialog.Builder(context)
            .setMessage("??????????????? ?????? ??????????????????")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->

                val secondPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "secondPw: $secondPw")
                if(firstPw == secondPw){
                    Log.d("MemoDebug", "???????????? ??????")
                    // ???????????? ????????????
                    detailViewModel.lockMemo(memoId, secondPw)

                    Log.d("MemoDebug", "???????????? ?????? ??? isEditMode: ${isEditMode.value}, isLock.value: ${isLock.value}")
                    isLock.value = true
                    Log.d("MemoDebug", "isLock.value: ${isLock.value}")

                }else{
                    Log.d("MemoDebug", "???????????? ?????????")
                    binding.inputPw.text = null
                    showSecondLockDialog(firstPw)
                }
            }
            .setNegativeButton("cancel") { _, _ ->
                binding.inputPw.text = null
            }
            .setNeutralButton("???????????????"){_, _ ->
                binding.inputPw.text = null
                showFirstLockDialog()
            }
            .show()

        val window = dialogBuilder2.window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun showUnlockDialog(id: Long){
        if (binding.inputPw.parent != null){
            (binding.inputPw.parent as ViewGroup).removeView(binding.inputPw)
            binding.inputPw.visibility = View.VISIBLE
            binding.inputPw.text = null
            binding.inputPw.requestFocus()
        }

        val dialogBuilder3 = AlertDialog.Builder(context)
            .setMessage("??????????????? ?????? ??????????????? ???????????????!")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->
                val unlockPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "????????? ????????????: $unlockPw")
                detailViewModel.getOneMemo(id){ memo ->
                    if(memo.password == unlockPw){
                        detailViewModel.unlockMemo(id)

                        binding.inputPw.text = null
                        isLock.postValue(false)

                    }else{
                        Log.d("MemoDebug", "memo.password: ${memo.password}," +
                                "???????????? ????????? pw: $unlockPw, ??????X")
                        binding.inputPw.text = null
                        isLock.postValue(false)
                        return@getOneMemo
                    }
                }

            }
            .setNegativeButton("cancel") { _, _ ->
                binding.inputPw.text = null
            }
            .show()

        val window = dialogBuilder3.window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun showImageDeleteDialog(id: Long, result:(Boolean)-> Unit){
        AlertDialog.Builder(context)
            .setMessage("???????????? ??????????????????????")
            .setPositiveButton("yes") { _, _ ->
                Log.d("MemoDebug", "????????? ????????? memoId: $id")
                detailViewModel.getOneMemo(id){ memo ->
                    Log.d("MemoDebug", "????????? ?????? ??? memo: $memo")
                    memo.image = null
                    detailViewModel.updateMemo(memo)
                    Log.d("MemoDebug", "????????? ?????? ??? memo: $memo")
                    result.invoke(true)
                }
            }
            .setNegativeButton("cancel") { _, _ ->
               //nothing
            }
            .show()
    }

    private fun EditText.showKeyboard() {
        requestFocus()
//        // ????????? ????????? // TODO:: ????????? ?????? ???????????? ??????
//        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(binding.editTitle, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)

        Log.d("MemoDebug", "DetailFragment::startActivityForResult-()")
        Log.d("MemoDebug", "DetailFragment:: requestCode: $requestCode, data: $intent")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ImageEvent?) {
        Log.d("MemoDebug", "DetailFragment::onMessageEvent-()")
        event?.let {
            //onActivityResult(it.requestCode, it.resultCode, it.data)
            Log.d("MemoDebug", "DetailFragment:: imageEvent: $event")

            var data = event.data?.data // ??????????????? ??????
            var clipData = event.data?.clipData // ??????????????? ??????

            Log.d("MemoDebug", "DetailFragment:: imageEvent data: $data")
            Log.d("MemoDebug", "DetailFragment:: imageEvent clipData: $clipData")

            var uriList = arrayListOf<Uri>()

            // ??????????????? ??????
            data.let { contentUri ->
                Log.d("MemoDebug", "DetailFragment:: imageEvent contentUri=> $contentUri")

                val filePath = getRealPathFromURI(requireContext(), contentUri)
                Log.d("MemoDebug", "filePath=> $filePath")

                val realUri = Uri.fromFile(File(filePath))
                realUri.let {
                    Log.d("MemoDebug", "realUri==> $realUri")
                }
                imageList.value?.add(realUri.toString())
                binding.imageView.setImageURI(realUri)
                binding.imageView.visibility = View.VISIBLE
            }

            // ??????????????? ??????
            clipData.let {
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri: Uri = clipData.getItemAt(i).uri // ????????? ??????????????? uri??? ????????????.
                    try {
                        uriList.add(imageUri) //uri??? list??? ?????????.
                        Log.d("MemoDebug", "uriList=> $uriList")
                    } catch (e: Exception) {
                        Log.d("MemoDebug", "e=> $e")
                    }
                }
                val contentUri = clipData.getItemAt(0).uri
                Log.d("MemoDebug", "contentUri====> $contentUri")

                val filePath = getRealPathFromURI(requireContext(), contentUri)
                Log.d("MemoDebug", "filePath====> $filePath")

                val realUri = Uri.fromFile(File(filePath))
                realUri.let {
                    Log.d("MemoDebug", "realUri====> $realUri")
                }
                imageList.value?.add(realUri.toString())
                binding.imageView.setImageURI(realUri)
                binding.imageView.visibility = View.VISIBLE
            }
        }
    }

    // content Uri -> Path(????????????)
    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
