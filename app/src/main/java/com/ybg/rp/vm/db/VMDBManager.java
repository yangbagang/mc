package com.ybg.rp.vm.db;

import android.text.TextUtils;

import com.ybg.rp.vm.bean.LogInfo;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.bean.TransactionData;
import com.ybg.rp.vmbase.bean.VMOperator;
import com.ybg.rp.vmbase.utils.DateUtil;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.SDUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangbagang on 16/8/20.
 */
public class VMDBManager {

    private DbManager manager;

    private static VMDBManager instance;

    public static VMDBManager getInstance() {
        if (null == instance) {
            instance = new VMDBManager();
        }
        return instance;
    }

    private VMDBManager() {
        DbManager.DaoConfig config = new DbManager.DaoConfig();
        config.setDbName(VMConstant.DB_NAME);
        config.setDbVersion(VMConstant.DB_VERSION);
        String dbPath = SDUtil.getSDCardPath() + "/ybg_vm";
        config.setDbDir(new File(dbPath));//需要修改成 数据库外放
        config.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager dbManager, int i, int i1) {
                // db.addColumn(...);
                // db.dropTable(...);
                // ...
            }
        });
        manager = x.getDb(config);
    }

    public DbManager getDb() {
        return manager;
    }


    /**
     * 添加log日志
     *
     * @param content 内容
     */
    public void saveLog(VMOperator operator, String content) {
        try {
            LogInfo logInfo = new LogInfo();
            logInfo.setContent(content);
            if (null != operator) {
                logInfo.setOperatorName(operator.getOperatorName());
                logInfo.setOperatorId(operator.getOperatorId());
            }
            logInfo.setCreateDate(DateUtil.getCurrentDate(DateUtil.dateFormatYMDHMS));
            instance.addObject(logInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录错误轨道信息
     *
     * @param trackNo
     */
    public void saveFaultTrackNo(String trackNo) {
        try {
            TrackBean tv = manager.selector(TrackBean.class).where("track_no", "=", trackNo).findFirst();
            if (null == tv) {
                return;
            }
            tv.setFault(1);
            instance.saveOrUpdate(tv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加数据
     *
     * @param obj
     */
    public void addObject(Object obj) {
        try {
            manager.save(obj);
        } catch (DbException e) {
            LogUtil.e("添加数据错误---" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 添加数据OR 修改
     *
     * @param obj
     */
    public void saveOrUpdate(Object obj) {
        try {
            manager.saveOrUpdate(obj);
        } catch (Exception e) {
            LogUtil.e("保存数据错误---" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 查询最后一个
     *
     * @param cls
     * @return
     */
    public Object findFirst(Class<?> cls) {
        try {
            return manager.findFirst(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T findFirst(Class<T> entityType, String columnName, String op, Object value) {
        try {
            return manager.selector(entityType).where(columnName, op, value).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询全部数据
     *
     * @param entityType
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> findAll(Class<T> entityType) {
        try {
            return (ArrayList<T>) manager.findAll(entityType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询全部数据
     *
     * @param entityType
     * @param <T>
     * @return
     */
    public <T> ArrayList<T> findAll(Class<T> entityType, String columnName, String op, Object value) {
        try {
            return (ArrayList<T>) manager.selector(entityType).where(columnName, op, value).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询-销售数据-接口
     *
     * @param startDate 开始时间- yyyy-MM-dd HH:mm:ss
     * @param endDate   结束时间- yyyy-MM-dd HH:mm:ss
     * @param limit     显示条数
     * @param pageSize  页码 1开始
     * @return
     */
    public ArrayList<TransactionData> findTranData(String startDate, String endDate, int limit, int pageSize) {
        LogUtil.i("[-- startDate =" + startDate + "--- endDate =" + endDate + "--- limit = " + limit + "--- pageSize = " + pageSize);
        ArrayList<TransactionData> tranOnlineDatas = new ArrayList<TransactionData>();
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            LogUtil.e("时间字段不能为NULL");
            return tranOnlineDatas;
        }

        try {
            String sql = "select * from TRAN_ONLINE t where t.[SALE_RESULT] != :status  " +
                    " and date(t.[TRAN_DATE]) >= date(:startDate) and date(t.[TRAN_DATE]) <= date(:endDate)" +
                    " order by t.[CREATE_DATE] desc Limit :limit offset :pageSize";
            SqlInfo sqlInfo = new SqlInfo();
            /** 两个时间之间的数据*/
            sqlInfo.addBindArg(new KeyValue("status", 0));// 0 失败 1 成功
            sqlInfo.addBindArg(new KeyValue("startDate", startDate));
            sqlInfo.addBindArg(new KeyValue("endDate", endDate));
            sqlInfo.addBindArg(new KeyValue("limit", pageSize)); // 条数
            sqlInfo.addBindArg(new KeyValue("pageSize",(pageSize * limit - 1)));//页面-0开始
            sqlInfo.setSql(sql);
            List<DbModel> dbModels = instance.getDb().findDbModelAll(sqlInfo);
            for (int i = 0; i < dbModels.size(); i++) {
                DbModel dm = dbModels.get(i);
                TransactionData data = new TransactionData();
                data.setCreateDate(dm.getString("create_date"));//CREATE_DATE
                data.setOrderNo(dm.getString("order_no"));
                data.setTransactionDate(dm.getString("transaction_date"));
                data.setPayType(dm.getString("pay_type"));
                data.setSaleResult(dm.getString("sale_result"));
                data.setOrderPrice(dm.getDouble("order_price"));
                data.setTrackNo(dm.getString("track_no"));
                tranOnlineDatas.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tranOnlineDatas;
    }

}
