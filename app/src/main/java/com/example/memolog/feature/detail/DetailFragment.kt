package com.example.memolog.feature.detail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import com.example.memolog.event.ImageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.io.File
import java.net.HttpCookie.parse
import java.net.URI
import java.util.logging.Level.parse


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

        // 처음 메모 클릭 후, 내용 가져오는 부분
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
            // image 있는지 체크
            memo.image.let{
                if(it?.size!! > 0){
                    val savedImageUri = memo.image!![0]
                    Log.d("MemoDebug", "savedImageUri: $savedImageUri")

//                    var u: String = savedImageUri
//                    var link = u.toUri()

                    binding.imageView.setImageURI(savedImageUri.toUri())
                    binding.imageView.visibility = View.VISIBLE
                }
            }
        }

        isEditMode.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode) { // 수정모드
                Log.d("MemoDebug", "isEditMode : $isEditMode")
                binding.editBtn.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
                binding.editTitle.visibility = View.VISIBLE
                binding.editContent.visibility = View.VISIBLE
                binding.addPhotoBtn.visibility = View.VISIBLE

                binding.textTitle.visibility = View.GONE
                binding.textContent.visibility = View.GONE

                if(isLock.value == true){ // 잠금상태
                    Log.d("MemoDebug", "isEditMode 감시 isLock : ${isLock.value}")
                    binding.lockBtn.visibility = View.VISIBLE
                    binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_24)

                }else if(isLock.value == false){ // 해제상태
                    Log.d("MemoDebug", "isEditMode 감시 isLock : ${isLock.value}")
                    binding.lockBtn.visibility = View.VISIBLE
                    binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
                }
            } else { // 수정모드 해제
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

                // 수정 내용 -> textView 세팅
                binding.textTitle.text = binding.editTitle.text
                binding.textContent.text = binding.editContent.text

                // 잠금, 해제버튼 모두 INVISIBLE
                //binding.unlockBtn.visibility = View.INVISIBLE
                binding.lockBtn.visibility = View.INVISIBLE

                // TODO:: 키보드 내리기 이곳에 있었을때 왜 안됐지
            }
        }

        binding.lockBtn.setOnClickListener {
            if(isLock.value == true){
                Log.d("MemoDebug", "메모 잠금해제 아이콘 클릭")
                showUnlockDialog(memoId)
            }
        }

        //TODO:: DEBUG 모드 두 번타는거 이해X
        isLock.observe(viewLifecycleOwner){ isLock ->
            if(isLock && isEditMode.value == true){ // 잠금상태
                Log.d("MemoDebug", "11111 isLock감시 isLock : $isLock")
                binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_24)
                binding.lockBtn.setOnClickListener {
                   showUnlockDialog(memoId)
                }

            }else if(!isLock && isEditMode.value == true){ // 해제상태
                Log.d("MemoDebug", "22222 isLock감시 isLock : $isLock")
                binding.lockBtn.setBackgroundResource(R.drawable.ic_baseline_lock_open_24)
            }
        }

        binding.textTitle.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())

            // 자동 포커스
            binding.editTitle.apply {
                setSelection(binding.textTitle.length())
                requestFocus()
            }

            // 키보드 올리기 // TODO:: 함수로 따로 만들어서 빼기
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.editTitle, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.textContent.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())

            // 포커스 & 키보드 올리기
            binding.editContent.apply {
                setSelection(binding.textContent.length())
                requestFocus()
            }

            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.editContent, InputMethodManager.SHOW_IMPLICIT)
        }

        // edit 버튼 -> 메모 업데이트
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

                // 키보드 내리기
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity?.currentFocus?.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )

                isEditMode.postValue(false)
            }
        }

        // delete 버튼
        binding.deleteBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("정말 삭제하시겠습니까?\n복구는 불가합니다😭")
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
            if(isLock){ // 잠금 상태

            }else{ // 잠금 해제 상태
                binding.lockBtn.setOnClickListener {
                    showFirstLockDialog()
                }
            }
        }

        // 사진 추가 버튼
        binding.addPhotoBtn.setOnClickListener {
//            CropImage.activity()
//                .start(requireContext(), this)

            // 갤러리 다중이미지 선택 띄우기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 다중 이미지 true
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, 100)
            //activity?.startActivityForResult(intent, 100)

        }


        // back 버튼 -> 메모 업데이트
        binding.backBtn.setOnClickListener {
            //memoId
            reviseMemo()
        }


    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        detailViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    // back key -> 메모 업데이트
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
            .setMessage("비밀번호 숫자 네자리를 설정해주세요")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->

                val firstPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "firstPw: $firstPw")
                if(firstPw.length != 4){
                    Toast.makeText(context, "네 자리를 채워주세요", Toast.LENGTH_SHORT).show() // TODO :: dialog 위에 toast 띄우고 그대로 dialog 를 살릴 수 없는지?
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
            .setMessage("비밀번호를 다시 입력해주세요")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->

                val secondPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "secondPw: $secondPw")
                if(firstPw == secondPw){
                    Log.d("MemoDebug", "비밀번호 일치")
                    // 비밀번호 업데이트
                    detailViewModel.lockMemo(memoId, secondPw)

                    Log.d("MemoDebug", "비밀번호 업뎃 후 isEditMode: ${isEditMode.value}, isLock.value: ${isLock.value}")
                    isLock.value = true
                    Log.d("MemoDebug", "isLock.value: ${isLock.value}")

                }else{
                    Log.d("MemoDebug", "비밀번호 불일치")
                    binding.inputPw.text = null
                    showSecondLockDialog(firstPw)
                }
            }
            .setNegativeButton("cancel") { _, _ ->
                binding.inputPw.text = null
            }
            .setNeutralButton("재설정하기"){_, _ ->
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
            .setMessage("잠금해제를 위해 비밀번호를 적어주세요!")
            .setView(binding.inputPw)
            .setPositiveButton("yes") { _, _ ->
                val unlockPw = binding.inputPw.text.toString()
                Log.d("MemoDebug", "해제할 비밀번호: $unlockPw")
                detailViewModel.getOneMemo(id){ memo ->
                    if(memo.password == unlockPw){
                        detailViewModel.unlockMemo(id)

                        binding.inputPw.text = null
                        isLock.postValue(false)

                    }else{
                        Log.d("MemoDebug", "memo.password: ${memo.password}," +
                                "사용자가 입력한 pw: $unlockPw, 일치X")
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

    private fun EditText.showKeyboard() {
        requestFocus()
//        // 키보드 올리기 // TODO:: 함수로 따로 만들어서 빼기
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

            var clipData = event.data?.clipData
            var uriList = arrayListOf<Uri>()
            clipData.let {
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri: Uri = clipData.getItemAt(i).uri // 선택한 이미지들의 uri를 가져온다.
                    try {
                        uriList.add(imageUri) //uri를 list에 담는다.
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

    // content Uri -> Path(파일경로)
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