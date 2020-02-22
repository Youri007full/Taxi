package taxi.kassa.view.withdraws

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_whithdraw_date.view.*
import kotlinx.android.synthetic.main.item_withdrawal.view.*
import taxi.kassa.R
import taxi.kassa.model.responses.Withdraw
import taxi.kassa.util.Constants.APPROVED
import taxi.kassa.util.Constants.CANCELED
import taxi.kassa.util.Constants.NEW
import taxi.kassa.util.Constants.WRITTEN_OFF

class WithdrawsAdapter(
    withdraws: MutableList<Withdraw>,
    private val clickListener: (Withdraw) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_DATE = 1
        private const val DATE_ITEM_ID = "777"
    }

    private val withdraws = mutableListOf<Withdraw>()

    init {
        this.withdraws.addAll(withdraws)
        val dates = mutableSetOf<Withdraw>()
        try {
            for (i in 0..this.withdraws.size) {
                if (this.withdraws[i].getDate() != this.withdraws[i + 1].getDate()) {
                    dates.add(Withdraw(DATE_ITEM_ID, "0", this.withdraws[i].date, 0))
                }
            }
        } catch (e: IndexOutOfBoundsException) {
        }
        this.withdraws.addAll(dates)
        this.withdraws.sortBy { it.date }
        this.withdraws.reverse()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                WithdrawsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_withdrawal,
                        null
                    )
                )
            }
            TYPE_DATE -> {
                DateViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_whithdraw_date,
                        null
                    )
                )
            }
            else -> WithdrawsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_withdrawal,
                    null
                )
            )
        }
    }

    override fun getItemCount(): Int = withdraws.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val withdraw = withdraws[position]

        when (holder) {
            is WithdrawsViewHolder -> {
                val status = withdraw.getStatus()

                holder.time.text = withdraw.hours
                holder.amount.text = withdraw.amount
                holder.status.text = status

                when (status) {
                    NEW -> holder.statusImage.setImageResource(R.drawable.ic_yellow_circle)
                    APPROVED -> holder.statusImage.setImageResource(R.drawable.ic_yellow_circle)
                    WRITTEN_OFF -> holder.statusImage.setImageResource(R.drawable.ic_green_circle)
                    CANCELED -> holder.statusImage.setImageResource(R.drawable.ic_red_circle)
                }

                holder.itemView.setOnClickListener { clickListener(withdraw) }
            }
            is DateViewHolder -> {
                holder.date.text = withdraw.getDate()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (withdraws[position].source_id == DATE_ITEM_ID) {
            TYPE_DATE
        } else {
            TYPE_ITEM
        }
    }

    class WithdrawsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.time_tv
        val amount: TextView = view.amount_tv
        val status: TextView = view.status_tv
        val statusImage: ImageView = view.circle_image
    }

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.date
    }
}