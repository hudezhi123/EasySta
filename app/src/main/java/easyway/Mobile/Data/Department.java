package easyway.Mobile.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import easyway.Mobile.util.JsonUtil;

// 部门信息
public class Department {
//	public long deptId;				// 部门ID
//	public String deptName;		// 部门名称
//	public long parentDeptId;		// 上级部门ID
//
//	
	public long DeptId;
	public long ParentDeptId;
//	public String DeptCode;
	public String FullName;
//	public String ShortName;
//	public long ManagerStaffId;
//	public String Manager;
//	public long AssistantManagerStaffId;
//	public String AssistantManager;
//	public long AssistantManagerStaffId2;
//	public String AssistantManager2;
//	public long BranchedPassagerStaffId;
//	public String BranchedPassager;
//	public String Layer;
	public long DeptMark;
//	public String Postalcode;
//	public String OrgAddress;
	public String Expend1;
//	public String Expend2;
//	public String Expend3;
//	public String Remarks;
//	public String Creater;
//	public String CreateTime;
//	public String Modifier;
//	public String ModifyTime;
//	public long Validity;
	
	public int type;						// 部门类型
	
	public static ArrayList<Department> ParseFromString(String result) {
		ArrayList<Department> list = new ArrayList<Department>();

		if (result == null)
			return list;

		JSONArray jsonArray = JsonUtil.GetJsonArray(result, "Data");
		if (jsonArray == null || jsonArray.length() == 0)
			return list;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
			Department dept = new Department();
			
			dept.DeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptId");
			dept.ParentDeptId = JsonUtil.GetJsonObjLongValue(jsonObj, "ParentDeptId");
//			dept.DeptCode = JsonUtil.GetJsonObjStringValue(jsonObj, "DeptCode");
			dept.FullName = JsonUtil.GetJsonObjStringValue(jsonObj, "FullName");
//			dept.ShortName = JsonUtil.GetJsonObjStringValue(jsonObj, "ShortName");
//			dept.ManagerStaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "ManagerStaffId");
//			dept.Manager = JsonUtil.GetJsonObjStringValue(jsonObj, "Manager");
//			dept.AssistantManagerStaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "AssistantManagerStaffId");
//			dept.AssistantManager = JsonUtil.GetJsonObjStringValue(jsonObj, "AssistantManager");
//			dept.AssistantManagerStaffId2 = JsonUtil.GetJsonObjLongValue(jsonObj, "AssistantManagerStaffId2");
//			dept.AssistantManager2 = JsonUtil.GetJsonObjStringValue(jsonObj, "AssistantManager2");
//			dept.BranchedPassagerStaffId = JsonUtil.GetJsonObjLongValue(jsonObj, "BranchedPassagerStaffId");
//			dept.BranchedPassager = JsonUtil.GetJsonObjStringValue(jsonObj, "BranchedPassager");
//			dept.Layer = JsonUtil.GetJsonObjStringValue(jsonObj, "Layer");
			dept.DeptMark = JsonUtil.GetJsonObjLongValue(jsonObj, "DeptMark");
//			dept.Postalcode = JsonUtil.GetJsonObjStringValue(jsonObj, "Postalcode");
//			dept.OrgAddress = JsonUtil.GetJsonObjStringValue(jsonObj, "OrgAddress");
			dept.Expend1 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend1");
			dept.Expend1 = dept.Expend1.replace(",", "");	// 过滤逗号
//			dept.Expend2 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend2");
//			dept.Expend3 = JsonUtil.GetJsonObjStringValue(jsonObj, "Expend3");
//			dept.Remarks = JsonUtil.GetJsonObjStringValue(jsonObj, "Remarks");
//			dept.Creater = JsonUtil.GetJsonObjStringValue(jsonObj, "Creater");
//			dept.CreateTime = JsonUtil.GetJsonObjStringValue(jsonObj, "CreateTime");
//			dept.Modifier = JsonUtil.GetJsonObjStringValue(jsonObj, "Modifier");
//			dept.ModifyTime = JsonUtil.GetJsonObjStringValue(jsonObj, "ModifyTime");
//			dept.Validity = JsonUtil.GetJsonObjLongValue(jsonObj, "Validity");

			list.add(dept);
		}

		return list;
	}
}
