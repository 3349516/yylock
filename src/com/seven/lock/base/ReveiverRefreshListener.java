package com.seven.lock.base;

/**
 * �㲥ˢ�¼���
 * @author ll
 *
 */
public interface ReveiverRefreshListener {

	//type  -1 ȫ��ˢ��       0 Encry ��ˢ��  1 Filter��ˢ�� 
	void onRefresh(String packageName,int type);
}
