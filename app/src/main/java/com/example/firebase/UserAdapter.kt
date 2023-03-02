package com.example.firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: ArrayList<User>, val onClickDelete : (Int) -> Unit, val onClickEdit : (Int) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position],position)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

   inner  class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: User, index: Int) {
            itemView.findViewById<TextView>(R.id.tvName).text = user.name
            itemView.findViewById<TextView>(R.id.tvPhone).text = user.phoneNumber
            itemView.findViewById<TextView>(R.id.tvEmail).text = user.emailAdd
            itemView.findViewById<TextView>(R.id.tvDate).text = user.birthDate
            try {
                itemView.findViewById<ImageView>(R.id.imageView).setImageURI(user.imageUri?.toUri())
            }catch (e : Exception){

            }
            itemView.findViewById<Button>(R.id.btnDelete).setOnClickListener{
                onClickDelete(index)
            }
            itemView.findViewById<Button>(R.id.btnEdit).setOnClickListener{
                onClickEdit(index)
            }
        }
    }
}