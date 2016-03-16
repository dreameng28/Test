package com.similarity;

public interface IAuditSimilarity {
	/**
	 * 设置分词词典
	 * 
	 * @param segDic
	 */
	public void setSegmentDic(String segDic);

	/**
	 * 设置审计事项词词典
	 * 
	 * @param auditItemDic
	 */
	public void setAuditItemDic(String auditItemDic);

	/**
	 * 设置审计负面词词典
	 * 
	 * @param auditNegativeDic
	 */
	public void setAuditNegativeDic(String auditNegativeDic);

	/**
	 * 设置审计相关词词典
	 * 
	 * @param auditRelateDic
	 */
	public void setAuditRelateDic(String auditRelateDic);

	/**
	 * 设置搜索关键词
	 * 
	 * @param key
	 */
	public void setKeyWord(String key);

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * 计算审计相关度
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            正文
	 * @param timeInfo
	 *            时间信息
	 * @return
	 * @throws Exception
	 */
	public double getSimilarity(String title, String content, String timeInfo)
			throws Exception;
}
