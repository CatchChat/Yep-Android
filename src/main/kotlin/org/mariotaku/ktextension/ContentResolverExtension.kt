package org.mariotaku.ktextension

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import catchla.yep.util.YepArrayUtils

const val MAX_BULK_COUNT = 128

fun <T> ContentResolver.bulkDelete(uri: Uri, inColumn: String,
                                   colValues: Collection<T>?, extraWhere: String,
                                   extraWereArgs: Array<String>): Int {
    if (colValues == null) return 0
    return bulkDelete(uri, inColumn, colValues.toTypedArray<Any?>(), extraWhere, extraWereArgs)
}

fun <T : Any?> ContentResolver.bulkDelete(uri: Uri, inColumn: String, colValues: Array<T>,
                                          extraWhere: String?, extraWereArgs: Array<String>?): Int {
    if (colValues.isEmpty()) return 0
    val colValuesLength = colValues.size
    val blocksCount = colValuesLength / MAX_BULK_COUNT + 1
    var rowsDeleted = 0
    for (i in 0 until blocksCount) {
        val start = i * MAX_BULK_COUNT
        val endInclusive = Math.min(start + MAX_BULK_COUNT, colValuesLength) - 1
        val block = colValues.sliceArray(start..endInclusive).toStringArray()
        val whereArgs: Array<String?>
        if (extraWereArgs != null) {
            whereArgs = arrayOfNulls<String>(block.size + extraWereArgs.size)
            System.arraycopy(block, 0, whereArgs, 0, block.size)
            System.arraycopy(extraWereArgs, 0, whereArgs, block.size, extraWereArgs.size)
        } else {
            whereArgs = block
        }
        val where = StringBuilder(inColumn + " IN(" + YepArrayUtils.toStringForSQL(block) + ")")
        if (extraWhere?.isNotEmpty() ?: false) {
            where.append(" AND ").append(extraWhere)
        }
        rowsDeleted += delete(uri, where.toString(), whereArgs)
    }
    return rowsDeleted
}

fun ContentResolver.bulkInsertSliced(uri: Uri, values: Collection<ContentValues?>): Int {
    return bulkInsertSliced(uri, values.toTypedArray())
}

fun ContentResolver.bulkInsertSliced(uri: Uri, values: Array<ContentValues?>): Int {
    if (values.size == 0) return 0
    val colValuesLength = values.size
    val blocksCount = colValuesLength / MAX_BULK_COUNT + 1
    var rowsInserted = 0
    for (i in 0 until blocksCount) {
        val start = i * MAX_BULK_COUNT
        val end = Math.min(start + MAX_BULK_COUNT, colValuesLength)
        val block = arrayOfNulls<ContentValues>(end - start)
        System.arraycopy(values, start, block, 0, end - start)
        rowsInserted += bulkInsert(uri, block)
    }
    return rowsInserted
}
