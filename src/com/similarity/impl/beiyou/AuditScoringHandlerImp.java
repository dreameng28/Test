package com.similarity.impl.beiyou;

import com.similarity.IAuditSimilarity;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by dreameng on 16/1/25.
 */

public class AuditScoringHandlerImp implements IAuditSimilarity {
	private static Logger log = Logger.getLogger(AuditScoringHandlerImp.class);

	public static int MAX_WORD_LENGTH ;
	/**
	 * 审计事项词词典路径
	 */
	private String auditItemDic = null;
	/**
	 * 审计负面词词典路径
	 */
	private String auditNegativeDic = null;
	/**
	 * 审计相关词词典路径
	 */
	private String auditRelateDic = null;
	int textLength;
	private String key;
	public static HashMap<String, MyKeyword> wordInfo;

	@Override
	public void setAuditItemDic(String auditItemDic) {
		this.auditItemDic = auditItemDic;
	}

	@Override
	public void setAuditNegativeDic(String auditNegativeDic) {
		this.auditNegativeDic = auditNegativeDic;
	}

	@Override
	public void setAuditRelateDic(String auditRelateDic) {
		this.auditRelateDic = auditRelateDic;
	}

	@Override
	public void setKeyWord(String key) {
		this.key = key;
	}

	@Override


    public void init() {
		long sysDate = System.currentTimeMillis();
		wordInfo = new HashMap<String, MyKeyword>();
		readWordsFromTxt("腐败.dic", "腐败");
		readWordsFromTxt("挪用公款.dic", "挪用公款");
		readWordsFromTxt("逃税漏税.dic", "逃税漏税");
        readWordsFromTxt("违法乱纪.dic", "违法乱纪");
        readWordsFromTxt("小金库.dic", "小金库");
        readWordsFromTxt("虚假财务报告.dic", "虚假财务报告");
		readWordsFromTxt("biaodian.txt", "标点");
		readSettiingTxt("SETTING.txt");

		log.debug("初始化时长:" + (System.currentTimeMillis() - sysDate) + "ms" + "\n");

	}


    private void readWordsFromTxt(String txtUrl, String category) {
		try {
			String ss[];
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(txtUrl), "UTF8"));
			String s;
			MyKeyword keyWord;
			ArrayList<String> categoryList;
			while ((s = br.readLine()) != null) {
				ss = s.split("\t");
				if (wordInfo.get(ss[0].trim()) != null){
					categoryList = wordInfo.get(ss[0].trim()).getCategory();
					categoryList.add(category);
					keyWord = wordInfo.get(ss[0].trim());
					keyWord.setScore(Math.log(6/categoryList.size()));
				}else {
					categoryList = new ArrayList<String>();
					categoryList.add(category);
					keyWord = new MyKeyword();
					keyWord.setCategory(categoryList);
					keyWord.setScore(Math.log(6));
				}
				wordInfo.put(ss[0].trim(), keyWord);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void readSettiingTxt(String txtUrl) {
		try {
			String ss[];
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(txtUrl), "UTF8"));
			String s;
			s = br.readLine();
			ss = s.split(" ");
			log.debug(ss[0] + ss[1]);
			MAX_WORD_LENGTH = Integer.parseInt(ss[1].trim());
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSegmentDic(String segDic) {
		// TODO Auto-generated method stub
	}


	public double[] textClassisy(String text, Boolean isTitle) {
		System.out.println(wordInfo.size());
		for (String key: wordInfo.keySet()) {
			System.out.println(key + ": " + wordInfo.get(key).getCategory() + " " + wordInfo.get(key).getScore());
		}

		int sentenceNum = 0;
		int i = 0;
		int sentence_start_i = 0;

		double[] classifyValue = new double[6];

		StringBuilder contentWord = new StringBuilder();

		if (isTitle){
			text += ".";
		}

		while (i < text.length()) {
			int wordLength; // 待匹配的词长
			// 判断待匹配词是否到达结尾,contentWord为待匹配词
			if (i + MAX_WORD_LENGTH < text.length()) {
				contentWord.append(text.substring(i, i + MAX_WORD_LENGTH));
				wordLength = MAX_WORD_LENGTH;
			} else {
				contentWord.append(text.substring(i, text.length()));
				wordLength = text.length() - i;
			}
			// 对待匹配词在HashMap的Key中进行寻找
			for (; wordLength > 0; wordLength--) {
				MyKeyword keyContentWord = AuditScoringHandlerImp.wordInfo
						.get(contentWord.toString());
				if (keyContentWord != null) {
					String myWord = contentWord.toString();
					ArrayList<String> keyContentWordCategory = keyContentWord
							.getCategory();
					log.debug(myWord + "found:" + keyContentWordCategory);
					if (keyContentWordCategory.contains("腐败")) {
                        classifyValue[0] += keyContentWord.getScore();
					} else if (keyContentWordCategory.contains("挪用公款")) {
                        classifyValue[1] += keyContentWord.getScore();
					} else if (keyContentWordCategory.contains("逃税漏税")) {
                        classifyValue[2] += keyContentWord.getScore();
					} else if (keyContentWordCategory.contains("违法乱纪")) {
                        classifyValue[3] += keyContentWord.getScore();
                    } else if (keyContentWordCategory.contains("小金库")) {
                        classifyValue[4] += keyContentWord.getScore();
                    } else if (keyContentWordCategory.contains("虚假财务报告")) {
                        classifyValue[5] += keyContentWord.getScore();
                    } else if (keyContentWordCategory.contains("标点")) {
						if (sentence_start_i != 0){
							sentence_start_i += 1;
						}
						log.debug(text.substring(sentence_start_i, i + 1).trim());
						sentence_start_i = i;

						sentenceNum++;

					}

					// 匹配成功则清空待匹配的词并重置i的值,然后跳出循环,用于仅仅匹配最长词
					contentWord.delete(0, wordLength);
					i += wordLength;
					break;
				} else {
					contentWord.delete(wordLength - 1, wordLength);
				}
			}
			// 没有成功则i++
			if (wordLength == 0) {
				i += 1;
			}
		}

		if (sentenceNum == 0) {
			sentenceNum = 1;
		}
		return classifyValue;
	}


    @Override
    public double getSimilarity(String title, String content, String timeInfo) throws Exception {
        return 0;
    }
}
