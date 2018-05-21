package com.example.administrator.accountbook.bill

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.db.entities.Account
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.fragment_accounts_reports.*
import org.jetbrains.anko.support.v4.toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AccountsReportsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AccountsReportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AccountsReportsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var accountsAdapter: AccountsAdapter? = null
    private val accounts = mutableListOf<Account>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_accounts_reports, container, false)
        initView(view)
        return view
    }

    var mPie: SelectPieView? = null
    var monthAccounts: MutableList<Account>? = null
    var btnSelect:Button?=null
    private fun initView(view: View?) {
        mPie = view?.findViewById<SelectPieView>(R.id.select_pie_view)
        val ll_expenditure = view?.findViewById<LinearLayout>(R.id.ll_expenditure)
        val ll_income = view?.findViewById<LinearLayout>(R.id.ll_income)
        val rvReports = view?.findViewById<RecyclerView>(R.id.rv_reports)
        rvReports?.layoutManager = LinearLayoutManager(activity)
        accountsAdapter = AccountsAdapter(R.layout.adapter_accounts, rvReports!!);
        rvReports.adapter = accountsAdapter
        monthAccounts = (activity as BillActivity).monthAccounts
        showAllIncome(monthAccounts!!, mPie, ll_income, ll_expenditure)
         btnSelect = view.findViewById<Button>(R.id.select)
        btnSelect?.setOnClickListener {
            if (btnSelect?.text == "收入图") {
                btnSelect?.text = "支出图"
                showAllExpenditure(monthAccounts!!, mPie, ll_income, ll_expenditure)
            } else {
                btnSelect?.text = "收入图"
                showAllIncome(monthAccounts!!, mPie, ll_income, ll_expenditure)
            }
        }


    }

    fun refresh() {

        monthAccounts = (activity as BillActivity).monthAccounts
        showAllIncome(monthAccounts!!, mPie, ll_income, ll_expenditure)
        btnSelect?.setOnClickListener {
            if (btnSelect?.text == "收入图") {
                btnSelect?.text = "支出图"
                showAllExpenditure(monthAccounts!!, mPie, ll_income, ll_expenditure)
            } else {
                btnSelect?.text = "收入图"
                showAllIncome(monthAccounts!!, mPie, ll_income, ll_expenditure)
            }
        }
    }

    private fun showAllExpenditure(monthAccounts: MutableList<Account>, mPie: SelectPieView?, ll_income: LinearLayout?, ll_expenditure: LinearLayout?) {
        ll_income?.visibility = View.INVISIBLE
        ll_expenditure?.visibility = View.VISIBLE
        var tongxunAmount = 0.0f
        val tongxunAccounts = mutableListOf<Account>()
        var lvyouAmount = 0.0f
        val lvyouAccounts = mutableListOf<Account>()
        var gouwuAmount = 0.0f
        val gouwuAccounts = mutableListOf<Account>()
        var canyinAmount = 0.0f
        val canyinAccounts = mutableListOf<Account>()
        var qitaAmount = 0.0f
        val qitaAccounts = mutableListOf<Account>()
        var allAmount = 0.0f
        ViseLog.d(tongxunAmount)
        for (account in monthAccounts) {
            if (account.type == "0") {
                when (account.status) {
                    "0" -> {
                        tongxunAmount += (-account.amount).toFloat()
                        tongxunAccounts.add(account)
                    }
                    "1" -> {
                        canyinAmount += (-account.amount).toFloat()
                        canyinAccounts.add(account)
                    }

                    "2" -> {
                        lvyouAmount += (-account.amount).toFloat()
                        lvyouAccounts.add(account)
                    }
                    "3" -> {
                        gouwuAmount += (-account.amount).toFloat()
                        gouwuAccounts.add(account)
                    }
                    else -> {
                        qitaAmount += (-account.amount).toFloat()
                        qitaAccounts.add(account)
                    }
                }
                allAmount += (-account.amount).toFloat()
            }

        }
        ViseLog.d(tongxunAmount)

        val datas = mutableListOf<SelectPieView.PieData>()
        datas.add(SelectPieView.PieData(1, tongxunAmount, R.color.color_tongxuan, R.drawable.tongxun))
        datas.add(SelectPieView.PieData(2, canyinAmount, R.color.color_canyin, R.drawable.canyin))
        datas.add(SelectPieView.PieData(3, lvyouAmount, R.color.color_lvyou, R.drawable.lvyou))
        datas.add(SelectPieView.PieData(4, gouwuAmount, R.color.color_gouwu, R.drawable.gouwu))
        datas.add(SelectPieView.PieData(5, qitaAmount, R.color.color_qita, R.drawable.qita))
        mPie?.reSetData(datas)
        mPie?.mTitle = "总支出"
        mPie?.setNumber(allAmount.toString())
        mPie?.invalidate()
        accountsAdapter?.data = tongxunAccounts
        mPie?.setCallBack(object : SelectPieView.SelectPieCallBack {
            override fun currentPostion(postion: Int) {
                when (postion) {
                    1 -> accountsAdapter?.data = tongxunAccounts
                    2 -> accountsAdapter?.data = canyinAccounts
                    3 -> accountsAdapter?.data = lvyouAccounts
                    4 -> accountsAdapter?.data = gouwuAccounts
                    5 -> accountsAdapter?.data = qitaAccounts

                }

            }
        })
    }

    private fun showAllIncome(monthAccounts: MutableList<Account>, mPie: SelectPieView?, ll_income: LinearLayout?, ll_expenditure: LinearLayout?) {
        ll_expenditure?.visibility = View.GONE
        ll_income?.visibility = View.VISIBLE
        var gongziAmount = 0.0f
        var gongziAccounts = mutableListOf<Account>()
        var licaiAmount = 0.0f
        var licaiAccounts = mutableListOf<Account>()
        var qitaAmount = 0.0f
        var qitaAccounts = mutableListOf<Account>()
        var allAmount = 0.0f
        for (account in monthAccounts) {
            if (account.type == "1") {
                when (account.status) {
                    "0" -> {
                        gongziAmount += (account.amount).toFloat()
                        gongziAccounts.add(account)
                    }
                    "1" -> {
                        licaiAmount += (account.amount).toFloat()
                        licaiAccounts.add(account)
                    }
                    else -> {
                        qitaAmount += (account.amount).toFloat()
                        qitaAccounts.add(account)
                    }
                }
                allAmount += (account.amount).toFloat()
            }

        }

        val datas = mutableListOf<SelectPieView.PieData>()
        datas.add(SelectPieView.PieData(1, gongziAmount, R.color.color_gongzi, R.drawable.gongzi))
        datas.add(SelectPieView.PieData(2, licaiAmount, R.color.color_licai, R.drawable.licai))
        datas.add(SelectPieView.PieData(3, qitaAmount, R.color.color_qita, R.drawable.qita))
        mPie?.reSetData(datas)
        mPie?.mTitle = "总收入"
        mPie?.setNumber(allAmount.toString())
        accountsAdapter?.data = gongziAccounts
        mPie?.setCallBack(object : SelectPieView.SelectPieCallBack {
            override fun currentPostion(postion: Int) {
                when (postion) {
                    1 -> {
                        accountsAdapter?.data = gongziAccounts
                    }
                    2 -> {
                        accountsAdapter?.data = licaiAccounts
                    }
                    3 -> {
                        accountsAdapter?.data = qitaAccounts
                    }


                }

            }
        })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountsReportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AccountsReportsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
