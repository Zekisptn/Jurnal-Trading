package com.example.jurnaltreding.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val DASHBOARD = "dashboard"
    const val JOURNAL_LIST = "journal_list"
    const val PROFILE = "profile"

    const val EDIT_PROFILE = "edit_profile"

    const val ARG_ENTRY_ID = "entryId"
    const val JOURNAL_DETAIL = "journal_detail/{$ARG_ENTRY_ID}"
    const val JOURNAL_FORM = "journal_form?$ARG_ENTRY_ID={$ARG_ENTRY_ID}"

    /** Nilai argumen form saat menambah catatan baru (bukan mengedit). */
    const val NO_ENTRY_ID = -1L

    fun journalDetail(entryId: Long) = "journal_detail/$entryId"

    /** Tanpa [entryId] = tambah catatan baru, dengan [entryId] = edit catatan. */
    fun journalForm(entryId: Long? = null) =
        if (entryId == null) "journal_form" else "journal_form?$ARG_ENTRY_ID=$entryId"
}
