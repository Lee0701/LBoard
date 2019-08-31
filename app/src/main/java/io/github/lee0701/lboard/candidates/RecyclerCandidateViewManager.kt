package io.github.lee0701.lboard.candidates

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.event.CandidateSelectEvent
import io.github.lee0701.lboard.event.CandidateUpdateEvent
import io.github.lee0701.lboard.prediction.Candidate
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RecyclerCandidateViewManager(val background: Int, val textColor: Int): CandidateViewManager {

    var contentView: RecyclerView? = null
    lateinit var adapter: CandidatesViewAdapter

    override fun init() {
        EventBus.getDefault().register(this)
    }

    override fun destroy() {
        EventBus.getDefault().unregister(this)
    }

    override fun initView(context: Context): View? {
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
        val candidates = event.candidates
                .let { if(it.size == 1) it + Candidate(0, "") else it }
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
            holder.bind(candidates[index], context)
            holder.itemView.layoutParams.apply {
                if(index < 3) width = holder.parentWidth / 3
                else width = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    inner class CandidateViewHolder(itemView: View, val parentWidth: Int): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var candidate: Candidate? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(candidate: Candidate, context: Context) {
            val text = itemView.findViewById<TextView>(R.id.text)
            text.setTextColor(textColor)
            text.text = candidate.text
            this.candidate = candidate
        }

        override fun onClick(v: View?) {
            if(adapterPosition == RecyclerView.NO_POSITION) return
            this.candidate?.let {
                if(it.text.isNotEmpty()) EventBus.getDefault().post(CandidateSelectEvent(it))
            }
        }
    }

}
