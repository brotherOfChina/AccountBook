package com.example.administrator.accountbook.supercalendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.database.getFinancialDao
import com.example.administrator.accountbook.financial.FinancialsAdapter
import com.ldf.calendar.Utils
import com.ldf.calendar.component.CalendarAttr
import com.ldf.calendar.component.CalendarViewAdapter
import com.ldf.calendar.interf.OnSelectDateListener
import com.ldf.calendar.model.CalendarDate
import com.ldf.calendar.view.Calendar
import com.ldf.calendar.view.MonthPager
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.back_head.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

/**
 * Created by ldf on 16/11/4.
 */

@SuppressLint("SetTextI18n")
class SyllabusActivity : AppCompatActivity() {
    lateinit var tvYear: TextView
    lateinit var tvMonth: TextView
    lateinit var backToday: TextView
    lateinit var content: CoordinatorLayout
    lateinit var monthPager: MonthPager
    lateinit var rvToDoList: RecyclerView
    lateinit var scrollSwitch: TextView
    lateinit var themeSwitch: TextView
    lateinit var nextMonthBtn: TextView
    lateinit var lastMonthBtn: TextView

    private var currentCalendars = ArrayList<Calendar>()
    private var calendarAdapter: CalendarViewAdapter? = null
    private var onSelectDateListener: OnSelectDateListener? = null
    private var mCurrentPage = MonthPager.CURRENT_DAY_INDEX
    private var context: Context? = null
    private var currentDate: CalendarDate? = null
    private var initiated = false
    val financialsAdapter: FinancialsAdapter by lazy {
        FinancialsAdapter(rvToDoList, R.layout.adapter_financial_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_syllabus)
        iv_back.setOnClickListener {
            finish()
        }
        head_title.text = "理财"
        context = this
        content = findViewById<View>(R.id.content) as CoordinatorLayout
        monthPager = findViewById<View>(R.id.calendar_view) as MonthPager
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.viewHeight = Utils.dpi2px(context!!, 270f)
        tvYear = findViewById<View>(R.id.show_year_view) as TextView
        tvMonth = findViewById<View>(R.id.show_month_view) as TextView
        backToday = findViewById<View>(R.id.back_today_button) as TextView
        scrollSwitch = findViewById<View>(R.id.scroll_switch) as TextView
        themeSwitch = findViewById<View>(R.id.theme_switch) as TextView
        nextMonthBtn = findViewById<View>(R.id.next_month) as TextView
        lastMonthBtn = findViewById<View>(R.id.last_month) as TextView
        rvToDoList = findViewById<View>(R.id.list) as RecyclerView
        rvToDoList.setHasFixedSize(true)
        //这里用线性显示 类似于listview
        rvToDoList.layoutManager = LinearLayoutManager(this)
        rvToDoList.adapter = financialsAdapter
        initCurrentDate()
        initCalendarView()
        showMarks()
        initToolbarClickListener()
        showFinancials(CalendarDate())
        Log.e("ldf", "OnCreated")
    }

    private fun showMarks() {
        async(UI) {
            val financials = bg {
                getFinancialDao().loadAllFinancials()
            }
            ViseLog.d(financials.await())

            val markData = HashMap<String, String>()
            for (financial in financials.await()) {
                markData[financial.income_date!!] = "0"
            }

            calendarAdapter!!.setMarkData(markData)
            calendarAdapter?.notifyDataChanged()
        }
    }

    fun showFinancials(date: CalendarDate) {
        ViseLog.d(date)
        val dateString = "${date.year}-${date.month}-${date.day}"
        ViseLog.d(dateString)
        async(UI) {
            val financils = bg {
                getFinancialDao().loadDayFinancials(dateString)
            }
            ViseLog.d(financils.await())
            financialsAdapter.data = financils.await()
        }
    }

    /**
     * onWindowFocusChanged回调时，将当前月的种子日期修改为今天
     *
     * @return void
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !initiated) {
            refreshMonthPager()
            initiated = true
        }
    }

    /*
    * 如果你想以周模式启动你的日历，请在onResume是调用
    * Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
    * calendarAdapter.switchToWeek(monthPager.getRowIndex());
    * */
    override fun onResume() {
        super.onResume()
    }

    /**
     * 初始化对应功能的listener
     *
     * @return void
     */
    private fun initToolbarClickListener() {
        backToday.setOnClickListener { onClickBackToDayBtn() }
        scrollSwitch.setOnClickListener {
            if (calendarAdapter!!.calendarType == CalendarAttr.CalendarType.WEEK) {
                Utils.scrollTo(content, rvToDoList, monthPager.viewHeight, 200)
                calendarAdapter!!.switchToMonth()
            } else {
                Utils.scrollTo(content, rvToDoList, monthPager.cellHeight, 200)
                calendarAdapter!!.switchToWeek(monthPager.rowIndex)
            }
        }
        themeSwitch.setOnClickListener { refreshSelectBackground() }
        nextMonthBtn.setOnClickListener { monthPager.currentItem = monthPager.currentPosition + 1 }
        lastMonthBtn.setOnClickListener { monthPager.currentItem = monthPager.currentPosition - 1 }
    }

    /**
     * 初始化currentDate
     *
     * @return void
     */
    private fun initCurrentDate() {
        currentDate = CalendarDate()
        tvYear.text = currentDate!!.getYear().toString() + "年"
        tvMonth.text = currentDate!!.getMonth().toString() + ""
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private fun initCalendarView() {
        initListener()
        val customDayView = CustomDayView(context, R.layout.custom_day)
        calendarAdapter = CalendarViewAdapter(
                context,
                onSelectDateListener,

                CalendarAttr.WeekArrayType.Monday,
                customDayView)
        calendarAdapter!!.setOnCalendarTypeChangedListener { rvToDoList.scrollToPosition(0) }
        initMonthPager()
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     */
    private fun initMarkData() {

    }

    private fun initListener() {
        onSelectDateListener = object : OnSelectDateListener {
            override fun onSelectDate(date: CalendarDate) {
                refreshClickDate(date)
                if (calendarAdapter!!.calendarType != CalendarAttr.CalendarType.WEEK) {
                    Utils.scrollTo(content, rvToDoList, monthPager.cellHeight, 200)
                    calendarAdapter!!.switchToWeek(monthPager.rowIndex)

                } else {
//                    Utils.scrollTo(content, rvToDoList, monthPager.viewHeight, 200)
//                    calendarAdapter!!.switchToMonth()
                }
            }

            override fun onSelectOtherMonth(offset: Int) {
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                monthPager.selectOtherMonth(offset)
            }
        }
    }


    private fun refreshClickDate(date: CalendarDate) {
        currentDate = date
        showFinancials(date)
        tvYear.text = date.getYear().toString() + "年"
        tvMonth.text = date.getMonth().toString() + ""
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private fun initMonthPager() {
        monthPager.adapter = calendarAdapter
        monthPager.currentItem = MonthPager.CURRENT_DAY_INDEX
        monthPager.setPageTransformer(false) { page, position ->
            var position = position
            position = Math.sqrt((1 - Math.abs(position)).toDouble()).toFloat()
            page.alpha = position
        }
        monthPager.addOnPageChangeListener(object : MonthPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                mCurrentPage = position
                currentCalendars = calendarAdapter!!.pagers
                if (currentCalendars[position % currentCalendars.size] != null) {
                    val date = currentCalendars[position % currentCalendars.size].seedDate
                    currentDate = date
                    tvYear.text = date.getYear().toString() + "年"
                    tvMonth.text = date.getMonth().toString() + ""
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    fun onClickBackToDayBtn() {
        refreshMonthPager()
    }

    private fun refreshMonthPager() {
        val today = CalendarDate()
        calendarAdapter!!.notifyDataChanged(today)
        tvYear.text = today.getYear().toString() + "年"
        tvMonth.text = today.getMonth().toString() + ""
    }

    private fun refreshSelectBackground() {
        val themeDayView = ThemeDayView(context, R.layout.custom_day_focus)
        calendarAdapter!!.setCustomDayRenderer(themeDayView)
        calendarAdapter!!.notifyDataSetChanged()
        calendarAdapter!!.notifyDataChanged(CalendarDate())
    }
}

