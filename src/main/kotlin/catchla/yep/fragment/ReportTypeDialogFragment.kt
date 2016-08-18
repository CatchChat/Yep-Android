package catchla.yep.fragment

import android.accounts.Account
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.annotation.ItemType
import catchla.yep.annotation.ReportType
import catchla.yep.extension.Bundle
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.dialog_bottom_sheet_report.*
import nl.komponents.kovenant.task

/**
 * Created by mariotaku on 16/8/9.
 */
class ReportTypeDialogFragment : BottomSheetDialogFragment() {

    val account: Account by lazy {
        arguments.getParcelable<Account>(EXTRA_ACCOUNT)
    }

    val reportId: String by lazy {
        arguments.getString(EXTRA_ID)
    }

    @ItemType val itemType: Int by lazy {
        arguments.getInt(EXTRA_ITEM_TYPE)
    }

    val reportTypeEntries: IntArray = intArrayOf(R.string.sexual_content, R.string.spam,
            R.string.phishing, R.string.other_reason)
    val reportTypeValues: IntArray = intArrayOf(ReportType.SEXUAL_CONTENT, ReportType.SPAM,
            ReportType.PHISHING, ReportType.OTHER)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val entries = reportTypeEntries.map { getString(it) }
        reportType.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, entries)
        reportType.setOnItemClickListener { view, child, pos, id ->
            val reportTypeValue = reportTypeValues[pos]
            when (reportTypeValue) {
                ReportType.OTHER -> {
                    // TODO open reason input
                    val df = ReportReasonDialogFragment()
                    df.arguments = Bundle {
                        putParcelable(EXTRA_ACCOUNT, account)
                        putInt(EXTRA_ITEM_TYPE, itemType)
                        putString(EXTRA_ID, reportId)
                        putInt(EXTRA_REPORT_TYPE, reportTypeValue)
                    }
                    df.show(fragmentManager, "report_reason")
                    dismiss()
                }
                else -> {
                    reportItem(context, account, itemType, reportId, reportTypeValue, null)
                    dismiss()
                }
            }
        }
    }

    companion object {

        fun show(fragmentManager: FragmentManager, account: Account, id: String, @ItemType itemType: Int): ReportTypeDialogFragment {
            val df = ReportTypeDialogFragment()
            df.arguments = Bundle {
                putString(EXTRA_ID, id)
                putInt(EXTRA_ITEM_TYPE, itemType)
                putParcelable(EXTRA_ACCOUNT, account)
            }
            df.show(fragmentManager, "report_type")
            return df
        }

        fun reportItem(context: Context, account: Account, @ItemType itemType: Int, reportId: String, reportType: Int, reason: String?) {
            task {
                val yep = YepAPIFactory.getInstance(context, account)
                when (itemType) {
                    ItemType.TOPIC -> {
                        yep.reportTopic(reportId, reportType, reason)
                    }
                    ItemType.USER -> {
                        yep.reportUser(reportId, reportType, reason)
                    }
                    ItemType.MESSAGE -> {
                        yep.reportMessage(reportId, reportType, reason)
                    }
                    else -> {
                    }
                }
            }
        }
    }
}
