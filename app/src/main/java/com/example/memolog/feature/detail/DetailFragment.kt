package com.example.memolog.feature.detail

import android.R.attr
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.navigation.findNavController

import androidx.navigation.fragment.findNavController
import com.example.memolog.getCurrentTime
import com.theartofdev.edmodo.cropper.CropImage
import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.text.Spannable

import android.text.style.ImageSpan

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.drawable.Drawable

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned

import androidx.core.content.ContextCompat

import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var detailViewModel: DetailViewModel
    private var isEditMode = MutableLiveData(false)
    private var isLock = MutableLiveData(false)
    private var memoId = 0L
    private lateinit var backPressCallback: OnBackPressedCallback
    private var password = ""

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

        val args: DetailFragmentArgs by navArgs()
        memoId = args.memoId

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
            CropImage.activity()
                .start(requireContext(), this)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri: Uri = result.uri
                Log.d("MemoDebug", "가져온 uri: $resultUri")

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}