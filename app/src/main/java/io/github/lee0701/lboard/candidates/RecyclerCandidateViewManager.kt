package io.github.lee0701.lboard.candidates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.event.CandidateLongClickEvent
import io.github.lee0701.lboard.event.CandidateSelectEvent
import io.github.lee0701.lboard.event.CandidateUpdateEvent
import io.github.lee0701.lboard.inputmethod.InputMethodInfo
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.SingleCandidate
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RecyclerCandidateViewManager(val background: Int, val textColor: Int): CandidateViewManager {

    var contentView: RecyclerView? = null
    lateinit var adapter: CandidatesViewAdapter

    lateinit var methodInfo: InputMethodInfo

    override fun init() {
        EventBus.getDefault().register(this)
    }

    override fun destroy() {
        EventBus.getDefault().unregister(this)
    }

    override fun initView(context: Context): View? {
        if(this.contentView != null) return contentView

        val mainView = LayoutInflater.from(context).inflate(R.layout.candidate_view, null)
        val contentView = mainView.findViewById(R.id.recycler_view) as RecyclerView

        adapter = CandidatesViewAdapter(context)
        contentView.adapter = adapter
        contentView.layoutManager = CandidatesViewLayoutManager(context)
        contentView.setBackgroundResource(background)

        this.contentView = contentView
        return mainView
    }

    override fun getView(): View? {
        return contentView
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCandidateUpdate(event: CandidateUpdateEvent) {
        methodInfo = event.methodInfo
        val candidates = event.candidates.toList()
                .let { if(it.size == 1) it + SingleCandidate("", "") else it }
                .let { if(it.size >= 2) listOf(it[1], it[0]) + it.subList(2, it.size) else it }
        adapter.candidates = candidates
        adapter.notifyDataSetChanged()
    }

    inner class CandidatesViewLayoutManager(context: Context): LinearLayoutManager(context) {

        init {
            orientation = HORIZONTAL
        }

    }

    inner class CandidatesViewAdapter(val context: Context, var candidates: List<Candidate> = listOf()): RecyclerView.Adapter<CandidateViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, index: Int): CandidateViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.candidate_item, parent, false)
            return CandidateViewHolder(view, parent.width)
        }

        override fun getItemCount(): Int {
            return candidates.size
        }

        override fun onBindViewHolder(holder: CandidateViewHolder, index: Int) {
            holder.itemView.layoutParams.apply {
                if(index < 3) width = holder.parentWidth / 3
                else width = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            holder.bind(candidates[index])
        }
    }

    inner class CandidateViewHolder(itemView: View, val parentWidth: Int): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        var candidate: Candidate? = null

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(candidate: Candidate) {
            val text = itemView.findViewById<TextView>(R.id.text)
            text.setTextColor(textColor)
            text.text = candidate.text
            this.candidate = candidate
        }

        override fun onClick(v: View?) {
            if(adapterPosition == RecyclerView.NO_POSITION) return
            this.candidate?.let {
                if(it.text.isNotEmpty()) EventBus.getDefault().post(CandidateSelectEvent(methodInfo, it))
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            if(adapterPosition == RecyclerView.NO_POSITION) return false
            this.candidate?.let {
                Toast.makeText(itemView.context, R.string.msg_user_word_deleted, Toast.LENGTH_SHORT).show()
                if(it.text.isNotEmpty()) EventBus.getDefault().post(CandidateLongClickEvent(methodInfo, it))
                return true
            }
            return false
        }
    }

}
