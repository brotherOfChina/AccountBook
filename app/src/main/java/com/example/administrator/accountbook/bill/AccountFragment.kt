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
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper

import com.example.administrator.accountbook.R
import com.example.administrator.accountbook.account.UserAccountActivity
import com.example.administrator.accountbook.db.database.userDb
import com.example.administrator.accountbook.db.entities.Account
import com.example.administrator.accountbook.db.entities.User
import com.vise.log.ViseLog
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.startActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AccountFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AccountFragment : Fragment() ,BGAOnItemChildClickListener{
    override fun onItemChildClick(parent: ViewGroup?, childView: View?, position: Int) {
            if (childView?.id==R.id.cl_content){
                startActivity<UserAccountActivity>("uid" to accountsDetailAdapter!!.data[position].uid)

            }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var accountsDetailAdapter: AccountsDetailAdapter? = null
    private val accountsList= mutableListOf<AccountDetailBean>()
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
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        initView(view)
        return view
    }
    var uid:String=""
    private fun initView(view: View?) {

        val rvAccounts = view?.findViewById<RecyclerView>(R.id.rv_accounts)
        rvAccounts?.layoutManager = LinearLayoutManager(activity)
        accountsDetailAdapter = AccountsDetailAdapter(rvAccounts!!, R.layout.adapter_accounts_item)
        accountsDetailAdapter?.setOnItemChildClickListener(this)
        rvAccounts.adapter=accountsDetailAdapter
        val monthAccounts=(activity as BillActivity) .monthAccounts

        async(UI){
            val users=bg{
                activity!!.userDb().userDao().getUsers()
            }
            loadAccounts(users.await(),monthAccounts)

        }
    }
    fun refesh(){
        val monthAccounts=(activity as BillActivity) .monthAccounts

        async(UI){
            val users=bg{
                activity!!.userDb().userDao().getUsers()
            }
            loadAccounts(users.await(),monthAccounts)

        }
    }
    private fun loadAccounts(users: List<User>, monthAccounts: MutableList<Account>){
        accountsList.clear()
        for ((i,user) in users.withIndex()){
            var zhichuAmount=0.0
            var incomeAmount=0.0
            for (account in monthAccounts){
                if (account.user_id==user.uid){
                    if (account.type=="0"){
                        incomeAmount+= -account.amount
                    }else if(account.type=="1"){
                        zhichuAmount+=(account.amount)
                    }

                }
            }
            accountsList.add(i,AccountDetailBean(user.nick_name,user.uid,zhichuAmount,incomeAmount))

        }
        ViseLog.d(accountsList)
        accountsDetailAdapter?.data=accountsList

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
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AccountFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

data class AccountDetailBean(val userName: String, var uid: String, var expenditure: Double, var income: Double)
class AccountsDetailAdapter(recyclerView: RecyclerView, layoutId: Int) : BGARecyclerViewAdapter<AccountDetailBean>(recyclerView, layoutId) {
    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        helper?.setItemChildClickListener(R.id.cl_content)
    }
    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: AccountDetailBean?) {

        helper?.setText(R.id.tv_account_name, "账户：" + model?.userName)
        helper?.setText(R.id.tv_expenditure_amount_item, "收入：" + model?.expenditure)
        helper?.setText(R.id.tv_income_amount_item, "支出：" + model?.income)
    }
}
