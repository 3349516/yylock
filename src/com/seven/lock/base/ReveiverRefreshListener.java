package com.seven.lock.base;

/**
 * 广播刷新监听
 * @author ll
 *
 */
public interface ReveiverRefreshListener {

	//type  -1 全部刷新       0 Encry 不刷新  1 Filter不刷新 
	void onRefresh(String packageName,int type);
}
