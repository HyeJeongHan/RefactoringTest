package com.hjhan.moduletest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjhan.moduletest.R
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.util.DateUtils

/**
 * 레거시 RecyclerView Adapter.
 * - DiffUtil 미사용 → notifyDataSetChanged() 호출
 * - ViewHolder에서 직접 모델 변경 (안티패턴)
 * - position 직접 사용 (notifyItemChanged에서 stale position 위험)
 */
class UserAdapter(
    private val users: MutableList<User>,
    private val onUserClick: (User) -> Unit,
    private val onFavoriteClick: (User, Boolean) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_user_name)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_user_email)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_user_phone)
        val ivFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)
        val tvLastUpdated: TextView = itemView.findViewById(R.id.tv_last_updated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvPhone.text = user.phone ?: "번호 없음"
        holder.tvLastUpdated.text = DateUtils.getRelativeTimeString(user.lastUpdated)

        holder.ivFavorite.setImageResource(
            if (user.isFavorite) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )

        holder.itemView.setOnClickListener { onUserClick(user) }

        holder.ivFavorite.setOnClickListener {
            user.isFavorite = !user.isFavorite  // 모델 직접 변경 - 안티패턴
            onFavoriteClick(user, user.isFavorite)
            notifyItemChanged(position)  // stale position 위험
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        val snapshot = newUsers.toMutableList()
        users.clear()
        users.addAll(snapshot)
        notifyDataSetChanged()  // DiffUtil 사용 권장
    }
}
