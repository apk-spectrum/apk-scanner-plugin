package com.apkscanner.plugin.apkcompare;

import com.apkscanner.data.apkinfo.ApkInfo;
import com.apkscanner.plugin.AbstractExternalTool;
import com.apkscanner.plugin.PlugInManager;
import com.apkscanner.plugin.PlugInPackage;
import com.apkscanner.plugin.manifest.Component;
import com.apkscanner.util.Log;

public class ApkCompareLinker extends AbstractExternalTool {

	public ApkCompareLinker(PlugInPackage pluginPackage, Component component) {
		super(pluginPackage, component);
	}

	@Override
	public void launch() {
		ApkInfo info = PlugInManager.getApkInfo();
		if(info == null) return;
		launch(info.filePath);
	}

	@Override
	public void launch(final String src) {
		if(src == null) return;
		Log.e("launch " + src);
	}

	@Override
	public void launch(String src1, String src2) {
		Log.e("launch " + src1 + ", " + src2);
	}

}
