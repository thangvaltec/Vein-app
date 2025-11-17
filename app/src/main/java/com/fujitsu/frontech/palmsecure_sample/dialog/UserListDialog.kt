package com.fujitsu.frontech.palmsecure_sample.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.fujitsu.frontech.palmsecure_gui_sample.R

class UserListDialog(context: Context, private val userIds: List<String>, private val onUserSelected: (String) -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list_dialog)

        val listView = findViewById<ListView>(R.id.userListView)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_single_choice, userIds)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        findViewById<Button>(R.id.selectButton).setOnClickListener {
            val position = listView.checkedItemPosition
            if (position != ListView.INVALID_POSITION) {
                onUserSelected(userIds[position])
                dismiss()
            }
        }

        findViewById<Button>(R.id.closeButton).setOnClickListener {
            dismiss()
        }
    }
}