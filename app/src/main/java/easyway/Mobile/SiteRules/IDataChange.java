package easyway.Mobile.SiteRules;

public interface IDataChange {
	public void ItemClick(int index);		// 开启下载
//	public void Download(int index, int progress);		// 下载文件
	public void ItemClick(int index, boolean check);		// 选择类型
}
