package com.ruiec.testkotlin.dao

import android.content.Context
import com.ruiec.testkotlin.bean.DataBean

/**
 *
 * @author pengfaming
 * @date 2021/2/4 14:12
 */
class DataDaoManager private constructor(context: Context){
    var myDaoDao: DataBeanDao
    init {
        val devOpenHelper = DaoMaster.DevOpenHelper(context, "test_kotlin", null)
        val writableDb = devOpenHelper.writableDb
        val daoMaster = DaoMaster(writableDb)
        val newSession = daoMaster.newSession()
        this.myDaoDao = newSession.dataBeanDao
    }

    companion object{
        private var manager : DataDaoManager? = null
        fun getInstance(context: Context) : DataDaoManager?{
            if(manager == null){
                @Synchronized
                if(manager == null){
                    manager = DataDaoManager(context)
                }
            }
            return manager
        }
    }

    //添加数据
    fun getinsert(myData: DataBean){
        myDaoDao.insert(myData)
    }
    //删除数据
    fun getdelet(myData: DataBean){
        myDaoDao.delete(myData)
    }
    //修改数据
    fun getupdata(myData: DataBean){
        myDaoDao.update(myData)
    }
    //查询数据(指定数据)
    fun getquest_count(name:String):List<DataBean>{
        val where = myDaoDao.queryBuilder().where(DataBeanDao.Properties.Name.eq(name)).list()
        return where
    }
    //查询数据(所有数据)
    fun getquest_true(name:String):List<DataBean>{
        val list = myDaoDao.queryBuilder().list()
        return list
    }
    //查询数据(模糊数据)
    fun getquest_like(name:String):List<DataBean>{
        val where = myDaoDao.queryBuilder().where(DataBeanDao.Properties.Name.like("%$name%")).list()
        return where
    }
}