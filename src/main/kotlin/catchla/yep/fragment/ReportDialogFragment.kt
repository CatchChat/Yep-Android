package catchla.yep.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ViewAnimator
import catchla.yep.R
import catchla.yep.annotation.ReportType

/**
 * Created by mariotaku on 16/8/9.
 */
class ReportDialogFragment : BaseDialogFragment() {

    val reportTypeEntries: IntArray = intArrayOf(R.string.sexual_content, R.string.spam,
            R.string.phishing, R.string.other_reason)
    val reportTypeValues: IntArray = intArrayOf(ReportType.SEXUAL_CONTENT, ReportType.SPAM,
            ReportType.PHISHING, ReportType.OTHER)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.dialog_report)
        val dialog = builder.create()
        dialog.setOnShowListener {
            with(it as Dialog) {
                val viewAnimator = findViewById(R.id.viewAnimator) as ViewAnimator
                val reportTypeView = findViewById(R.id.reportType) as ListView
                val entries = reportTypeEntries.map {
                    getString(it)
                }
                reportTypeView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, entries)
                reportTypeView.setOnItemClickListener { view, child, pos, id ->
                    val reportType = reportTypeValues[pos]
                    when (reportType) {
                        ReportType.OTHER -> {
                            viewAnimator.showNext()
                        }
                        else -> {
                            doReport(reportType, null)
                        }
                    }
                }
            }
        }
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {

        }
        return dialog
    }

    private fun doReport(reportType: Int, reason: String?) {

    }

}
