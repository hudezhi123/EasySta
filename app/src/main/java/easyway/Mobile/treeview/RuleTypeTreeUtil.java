package easyway.Mobile.treeview;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import easyway.Mobile.treeview.databean.ChildRuleFile;
import easyway.Mobile.treeview.databean.ParentRuleType;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeNode;


/**
 * Created by boy on 2017/5/8.
 */

public class RuleTypeTreeUtil {

    public static List<TreeNode> BuildTree(String jsonResult, HashMap<String, ChildRuleFile> childMap) {
        List<TreeNode> originNodes = new ArrayList<TreeNode>();
        if (!TextUtils.isEmpty(jsonResult)) {
            StringBuilder type = new StringBuilder("");
            try {
                JSONObject jsonObject = new JSONObject(jsonResult);
                JSONArray dataArray = jsonObject.optJSONArray("Data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject firstJson = dataArray.optJSONObject(i);
                    JSONObject firstConfig = firstJson.optJSONObject("Configuration");
                    ParentRuleType grandRuleType = new ParentRuleType();
                    grandRuleType.text = firstConfig.optString("@text");
                    grandRuleType.id = firstConfig.optString("@id");
                    type.append(grandRuleType.text + "/");
                    TreeNode<ParentTab> grandNode = new TreeNode<ParentTab>(new ParentTab(grandRuleType));
                    originNodes.add(grandNode);
//                    grandTypeList.add(parentRuleType);
                    if (firstConfig.optJSONObject("Configuration") == null && firstConfig.optJSONArray("Configuration") != null) {
                        JSONArray secondConfig = firstConfig.optJSONArray("Configuration");
//                    parentTypeList = new ArrayList<ParentRuleType>();
                        for (int j = 0; j < secondConfig.length(); j++) {
                            JSONObject secondJson = secondConfig.optJSONObject(j);
                            ParentRuleType parentRuleType = new ParentRuleType();
                            parentRuleType.text = secondJson.optString("@text");
                            parentRuleType.id = secondJson.optString("@id");
                            type.append(parentRuleType.text + "/");
                            TreeNode<ParentTab> parentNode = new TreeNode<ParentTab>(new ParentTab(parentRuleType));
                            grandNode.addChild(parentNode);
//                        parentTypeList.add(parentRuleType);
//                        childTypeList = new ArrayList<ParentRuleType>();
                            JSONArray thirdConfig = secondJson.optJSONArray("Configuration");
                            if (thirdConfig != null) {
                                for (int k = 0; k < thirdConfig.length(); k++) {

                                    JSONObject thirdJson = thirdConfig.optJSONObject(k);
                                    ParentRuleType childRuleType = new ParentRuleType();
                                    childRuleType.text = thirdJson.optString("@text");
                                    childRuleType.id = thirdJson.optString("@id");
                                    type.append(childRuleType.text + "/");
                                    TreeNode<ParentTab> childNode = new TreeNode<ParentTab>(new ParentTab(childRuleType));
                                    parentNode.addChild(childNode);
                                    Iterator iter = childMap.keySet().iterator();
                                    while (iter.hasNext()) {
                                        String key = (String) iter.next();
                                        String keyEn = "";
                                        try {
                                            keyEn = java.net.URLDecoder.decode(key, "utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        String partKey = keyEn.replace("Regulation/", "@");
                                        String keyValue[] = partKey.split("@");
                                        String typeStr = keyValue[1];
                                        String typeStrValue = typeStr.substring(0, typeStr.lastIndexOf("/") + 1);
                                        String typeValue = type.toString();
                                        if (!TextUtils.isEmpty(typeValue) && typeValue.equals(typeStrValue)) {
                                            ChildRuleFile childRuleFile = childMap.get(key);
                                            childNode.addChild(new TreeNode<ChildTab>(new ChildTab(childRuleFile)));
                                        }
                                    }
                                    type.deleteCharAt(type.lastIndexOf("/"));
                                    type.delete(type.lastIndexOf("/") + 1, type.length());
//                            childTypeList.add(childRuleType);
                                }
                            }
                            type.delete(type.indexOf("/") + 1, type.length() - 1);
                        }
                    }
                    if (firstConfig.optJSONArray("Configuration") == null && firstConfig.optJSONObject("Configuration") != null) {
                        JSONObject secondConfig = firstConfig.optJSONObject("Configuration");
                        ParentRuleType parentRuleType = new ParentRuleType();
                        parentRuleType.text = secondConfig.optString("@text");
                        parentRuleType.id = secondConfig.optString("@id");
                        type.append(parentRuleType.text + "/");
                        TreeNode<ParentTab> parentNode = new TreeNode<ParentTab>(new ParentTab(parentRuleType));
                        grandNode.addChild(parentNode);
                        JSONArray thirdConfig = secondConfig.optJSONArray("Configuration");
                        for (int k = 0; k < thirdConfig.length(); k++) {
                            JSONObject thirdJson = thirdConfig.optJSONObject(k);
                            ParentRuleType childRuleType = new ParentRuleType();
                            childRuleType.text = thirdJson.optString("@text");
                            childRuleType.id = thirdJson.optString("@id");
                            type.append(childRuleType.text + "/");
                            TreeNode<ParentTab> childNode = new TreeNode<ParentTab>(new ParentTab(childRuleType));
                            parentNode.addChild(childNode);
                            Iterator iter = childMap.keySet().iterator();
                            while (iter.hasNext()) {
                                String key = (String) iter.next();
                                String keyEn = "";
                                try {
                                    keyEn = java.net.URLDecoder.decode(key, "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String partKey = keyEn.replace("Regulation/", "@");
                                String keyValue[] = partKey.split("@");
                                String typeStr = keyValue[1];
                                String typeStrValue = typeStr.substring(0, typeStr.lastIndexOf("/") + 1);
                                String typeValue = type.toString();
                                if (!TextUtils.isEmpty(typeValue) && typeValue.equals(typeStrValue)) {
                                    ChildRuleFile childRuleFile = childMap.get(key);
                                    childNode.addChild(new TreeNode<ChildTab>(new ChildTab(childRuleFile)));
                                }
                            }
                            type.deleteCharAt(type.lastIndexOf("/"));
                            type.delete(type.lastIndexOf("/") + 1, type.length());
//                            childTypeList.add(childRuleType);
                        }
                        type.delete(type.indexOf("/") + 1, type.length() - 1);
                    }
                    type = new StringBuilder("");
                }
                return originNodes;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return originNodes;
    }


    public static List<TreeNode> BuildTree(String jsonResult, HashMap<String, ChildRuleFile> childMap, int noUse) {
        List<TreeNode> originNodes = new ArrayList<TreeNode>();
        if (!TextUtils.isEmpty(jsonResult)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResult);
                JSONArray datas = jsonObject.optJSONArray("Data");
                for (int i = 0; i < datas.length() + 1; i++) {
                    ParentRuleType parentRuleType = new ParentRuleType();
                    if (i == datas.length()) {
                        parentRuleType.setId("未分组文件");
                        parentRuleType.setText("未分组文件");
                    } else {
                        JSONObject object = datas.optJSONObject(i);
                        JSONObject configObject = object.optJSONObject("Configuration");
                        parentRuleType.setId(configObject.optString("@id"));
                        parentRuleType.setText(configObject.optString("@text"));
                    }
                    TreeNode<ParentTab> parentNode = new TreeNode<ParentTab>(new ParentTab(parentRuleType));
                    originNodes.add(parentNode);
                    Iterator iter = childMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        ChildRuleFile childRuleFile = childMap.get(key);
                        String type = childRuleFile.RType;
                        if (TextUtils.isEmpty(type)) {
                            childRuleFile.RType = "未分组文件";
                        }
                        if (!TextUtils.isEmpty(type) && parentRuleType.getText().equals(type)) {
                            parentNode.addChild(new TreeNode<ChildTab>(new ChildTab(childRuleFile)));
                        } else {
                            continue;
                        }

                    }
                }
                return originNodes;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
