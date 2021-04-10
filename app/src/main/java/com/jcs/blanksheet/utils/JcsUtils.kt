package com.jcs.blanksheet.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.view.ViewAnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jcs.blanksheet.R
import com.jcs.blanksheet.model.Document
import org.jetbrains.anko.withAlpha
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jardson Costa on 22/03/2021.
 */

class JcsUtils {

    @SuppressLint("ConstantLocale")
    private val defaultLocale = Locale.getDefault()
    private val calendar = Calendar.getInstance()

    companion object {
        private const val PATTERN_FORMAT_DATE_USA = "MMM dd',' yyyy hh:mm a"
        private const val PATTERN_FORMAT_DATE_BR = "dd 'de' MMM 'de' yyyy hh:mm a"
        private const val PATTERN_FORMAT_DATE_DEFAULT = "dd/MM/yyyy hh:mm a"
    }

    /**
     * Obtem a data atual
     *
     * @return String
     **/
    fun actualDate(): String {
        val br = Locale("pt", "BR")
        val pt = Locale("pt", "PT")
        val usa: Locale = Locale.US

        val formatUSA = SimpleDateFormat(PATTERN_FORMAT_DATE_USA, usa)
        val formatBR = SimpleDateFormat(PATTERN_FORMAT_DATE_BR, br)
        val format = SimpleDateFormat(PATTERN_FORMAT_DATE_DEFAULT, Locale.getDefault())
//        return when (defaultLocale) {
//            usa -> formatUSA.format(calendar.time)
//            br, pt -> formatBR.format(calendar.time)
//            else -> format.format(calendar.time)
//        }
        return when (defaultLocale) {
            usa -> formatUSA.format(calendar.time)
            br, pt -> formatBR.format(calendar.time)
            else -> format.format(calendar.time)
        }
    }

    fun actualDateListGrid(date: Calendar): String{
        val format = SimpleDateFormat(PATTERN_FORMAT_DATE_DEFAULT, Locale.getDefault())
        return format.format(date)
    }

    /**
     * Obtem a data atual em milissegundos
     *
     * @return String
     **/
    fun dateForOrder(): String = System.currentTimeMillis().toString()

    /**
     * Obtem a primeira letra do título
     *
     * @return String
     **/
    fun getFirstLetter(text: String): String = text.first().toString().capitalize(Locale.ROOT)

    /**
     * Obtem o icon
     *
     * @param context
     * @param color
     * @return GradientDrawable
     **/

    fun iconTextDrawable(title: Any): GradientDrawable {
        return roundedIconDrawable(ColorGenerator.MATERIAL.getColor(title).withAlpha(70))
    }

    fun roundedIconDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            this.setColor(color)
            this.shape = GradientDrawable.OVAL
        }
    }


    /**
     *  Retorna a primeira linha do EditText
     *
     *  @param editText
     *  @return String
     * */
    fun getFormattedTitle(editText: EditText): String {
        val layout = editText.layout
        return editText.text.subSequence(layout.getLineStart(0), layout.getLineEnd(0)).toString()
            .trim()
    }

    /**
     *  Retorna a altura do ActionBar
     *
     *  @param context
     *  @return Int
     * */
    fun actionBarHeight(context: Context): Int {
        val styledAttributes: TypedArray =
            context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return actionBarSize
    }

    /**
     *  Ordena os documentos de acordo com os dados guardados no SharePreference
     *
     *  @param context
     *  @param documents
     *  @return Int
     * */
    @SuppressLint("CommitPrefEdits")
    fun onSortDocuments(context: Context, documents: ArrayList<Document>) {
        //  val mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val mPreferences =
            context.getSharedPreferences(Constants.RADIO_BUTTON, Context.MODE_PRIVATE)

        val sortBy = mPreferences.getString(Constants.SORT_BY, Constants.SORT_BY_NAME_AZ)
        //val isReverseMode = mPreferences.getBoolean(Constants.DIALOG_REVERSE_MODE, false)

        when (sortBy) {
            Constants.SORT_BY_NAME_AZ -> {
                documents.let {
                    it.sortWith { a, z ->
                        a.title.capitalize(Locale.ROOT).compareTo(z.title.capitalize(Locale.ROOT))
                    }
                }
            }
            Constants.SORT_BY_NAME_ZA -> {
                documents.let {
                    it.sortWith { a, z ->
                        z.title.capitalize(Locale.ROOT).compareTo(a.title.capitalize(Locale.ROOT))
                    }
                }
            }
            Constants.SORT_BY_DATE_RECENT -> {
                documents.let {
                    it.sortWith { new, old ->
                        old.dateForOrder.capitalize(Locale.ROOT)
                            .compareTo(new.dateForOrder.capitalize(Locale.ROOT))
                    }
                }
            }
            Constants.SORT_BY_DATE_OLDEST -> {
                documents.let {
                    it.sortWith { new, old ->
                        new.dateForOrder.capitalize(Locale.ROOT)
                            .compareTo(old.dateForOrder.capitalize(Locale.ROOT))
                    }
                }
            }

        }
    }
//
//    fun openFile(activity: AppCompatActivity?, isTitle: Boolean): String {
//        val uri = activity!!.intent.data
//        var content = ""
//
//        try {
//            val inputStream = activity.contentResolver.openInputStream(uri!!)
//            val size = inputStream!!.available()
//            val dis = DataInputStream(inputStream)
//            val byte = ByteArray(size)
//            val length = dis.read(byte, 0, size)
//            content = String(byte, 0, length, Charsets.UTF_8)
//            inputStream.close()
//            dis.close()
//        } catch (e: FileNotFoundException) {
//            activity.toast("Arquivo não encontrado!")
//        } catch (e: IOException) {
//            activity.toast("Arquivo não pode ser aberto!")
//        }
//
//        return if (isTitle) File(uri!!.path.toString()).nameWithoutExtension else content
//    }

    fun readTextFromIntent(activity: AppCompatActivity, isTitle: Boolean): String {
        if (!verifyStoragePermissions(activity)) return ""
        var uri = activity.intent.data
        val intent = activity.intent
        when (intent?.action) {
            Intent.ACTION_VIEW, Intent.ACTION_EDIT -> {
                return readTextFromFile(uri, isTitle)
            }
            Intent.ACTION_SEND -> {
                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                return readTextFromFile(activity, uri, isTitle)
            }
        }
        return ""
    }

    // Ler arquivos recebidos por Intent.ACTION_SEND
    private fun readTextFromFile(
        activity: AppCompatActivity?,
        uri: Uri?,
        isTitle: Boolean
    ): String {
        val reader: BufferedReader?
        val builder: StringBuilder = java.lang.StringBuilder()
        val fileName = File(uri!!.path.toString())
        try {
            reader =
                BufferedReader(
                    InputStreamReader(
                        activity!!.contentResolver.openInputStream(uri),
                        Charsets.UTF_8
                    )
                )

            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (isTitle) fileName.nameWithoutExtension
        else builder.toString()
    }

    // Ler arquivos recebidos por Intent.ACTION_VIEW, Intent.ACTION_EDIT
    private fun readTextFromFile(uri: Uri?, isTitle: Boolean): String {
        val file = File(uri!!.path.toString())
        val inputStream = FileInputStream(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (isTitle)
            file.nameWithoutExtension
        else
            byteArrayOutputStream.toString()
    }

    // Requisita permissão para acessar arquivos de texto
    private fun verifyStoragePermissions(activity: AppCompatActivity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        val permission: Int
        val permissionAlreadyDenied: Boolean
        val permissionStorage: Array<String>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permission = activity.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            permissionAlreadyDenied =
                activity.shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            permissionStorage = arrayOf(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } else {
            permission = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionAlreadyDenied =
                activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionStorage = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(activity.resources.getString(R.string.permission))
            .setMessage(activity.resources.getString(R.string.message_need_permission_storage))
            .setPositiveButton(activity.resources.getText(R.string.grant)) { _, _ ->
                activity.requestPermissions(permissionStorage, Constants.REQUEST_EXTERNAL_STORAGE)
            }
            .setNegativeButton(activity.resources.getText(R.string.deny)) { d, _ ->
                d.dismiss()
            }

        return if (permission != PackageManager.PERMISSION_GRANTED) {
            if (permissionAlreadyDenied) {
                dialog.create().show()
            } else {
                activity.requestPermissions(permissionStorage, Constants.REQUEST_EXTERNAL_STORAGE)
            }
            false
        } else true
    }

    // Verifica se a tem permissão para ler arquivos da memória externa
    fun checkIfAlreadyHavePermission(activity: AppCompatActivity?): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

}