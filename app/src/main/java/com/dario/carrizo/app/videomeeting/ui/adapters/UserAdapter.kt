package com.dario.carrizo.app.videomeeting.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.base.BaseViewHolder
import com.dario.carrizo.app.videomeeting.listeners.UsersListener
import com.dario.carrizo.app.videomeeting.models.UserModel
import kotlinx.android.synthetic.main.item_container_user.view.*

/**
 * @author Dario Carrizo on 30/8/2020
 **/
class UserAdapter(
    private val users: MutableList<UserModel>,
    private val usersListener: UsersListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    val selectedUsers = mutableListOf<UserModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_container_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                holder.bind(users[position], position)
            }
        }
    }

    override fun getItemCount(): Int = users.size



    inner class UserViewHolder(itemView: View) : BaseViewHolder<UserModel>(itemView) {
        override fun bind(item: UserModel, position: Int) {
            itemView.textFirstChar.text = item.first_name.substring(0, 1)
            itemView.textUserName.text = String.format("%s %s", item.first_name, item.last_name)
            itemView.textEmail.text = item.email

            itemView.imageAudioMeeting.setOnClickListener {
                usersListener.initiateAudioMeeting(item)
            }

            itemView.imageViedeoMeeting.setOnClickListener {
                usersListener.initiateVideoMeeting(item)
            }
            itemView.userContainer.setOnLongClickListener {
                selectedUsers.add(item)
                itemView.imageSelected.visibility = View.VISIBLE
                itemView.imageViedeoMeeting.visibility = View.GONE
                itemView.imageAudioMeeting.visibility = View.GONE
                usersListener.onMultipleUsersAction(true)
                true
            }

            itemView.userContainer.setOnClickListener {
                if (itemView.imageSelected.visibility == View.VISIBLE) {
                    selectedUsers.remove(item)
                    itemView.imageSelected.visibility = View.GONE
                    itemView.imageViedeoMeeting.visibility = View.VISIBLE
                    itemView.imageAudioMeeting.visibility = View.VISIBLE
                    if (selectedUsers.size == 0) {
                        usersListener.onMultipleUsersAction(false)
                    }

                } else {
                    if(selectedUsers.size > 0){
                        selectedUsers.add(item)
                        itemView.imageSelected.visibility = View.VISIBLE
                        itemView.imageViedeoMeeting.visibility = View.GONE
                        itemView.imageAudioMeeting.visibility = View.GONE
                    }
                }
            }
        }
    }
}