package catchla.yep.fragment

import android.accounts.Account
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.annotation.ItemType

/**
 * Created by mariotaku on 16/8/11.
 */
class ReportReasonDialogFragment : BaseDialogFragment() {

    val account: Account by lazy {
        arguments.getParcelable<Account>(EXTRA_ACCOUNT)
    }
    val reportId: String by lazy {
        arguments.getString(EXTRA_ID)
    }

    @ItemType val itemType: Int by lazy {
        arguments.getInt(EXTRA_ITEM_TYPE)
    }

    val reportType: Int by lazy {
        arguments.getInt(EXTRA_REPORT_TYPE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            with(dialog as Dialog) {
                val editReportReason = findViewById(R.id.editReportReason) as EditText
                val reason = editReportReason.text.toString()
                ReportTypeDialogFragment.reportItem(context, account, itemType, reportId, reportType, reason)
            }
        }
        builder.setTitle(R.string.report_reason_title)
        builder.setView(R.layout.dialog_report_reason)
        val dialog = builder.create()
        dialog.setOnShowListener {

        }
        return dialog
    }
}
